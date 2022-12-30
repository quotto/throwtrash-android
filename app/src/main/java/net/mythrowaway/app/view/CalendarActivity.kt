package net.mythrowaway.app.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*
import net.mythrowaway.app.R
import net.mythrowaway.app.adapter.CalendarViewInterface
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.controller.CalendarControllerImpl
import net.mythrowaway.app.adapter.di.CalendarComponent
import net.mythrowaway.app.databinding.ActivityCalendarBinding
import net.mythrowaway.app.service.CalendarManagerImpl
import net.mythrowaway.app.service.UsageInfoService
import net.mythrowaway.app.usecase.*
import net.mythrowaway.app.viewmodel.CalendarViewModel
import javax.inject.Inject

class CalendarActivity : AppCompatActivity(),CalendarFragment.FragmentListener, NavigationView.OnNavigationItemSelectedListener,
    CalendarViewInterface,CoroutineScope by MainScope() {
    @Inject
    lateinit var controller: CalendarControllerImpl
    @Inject
    lateinit var presenter: CalendarPresenterInterface
    @Inject
    lateinit var configRepository: ConfigRepositoryInterface
    @Inject
    lateinit var calendarManager: CalendarManagerImpl

    @Inject
    lateinit var usageInfoService: UsageInfoService

    lateinit var calendarComponent: CalendarComponent

    private lateinit var activityCalendarBinding: ActivityCalendarBinding

    @VisibleForTesting
    private val idlingResource: CountingIdlingResource = CountingIdlingResource("CalendarViewIdling")

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun getIdlingResources(): CountingIdlingResource{
        return idlingResource
    }

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            launch {
                idlingResource.increment()
                launch {
                    controller.syncData()
                }.join()
                // DB同期後にViewPagerのFragmentを更新する
                supportFragmentManager.fragments.forEach {
                    if(it is CalendarFragment) {
                        it.arguments?.getInt(CalendarFragment.POSITION)?.let { position->
                            controller.generateCalendarFromPositionAsync(position)
                        }
                    }
                }
                idlingResource.decrement()
            }
        }
    }
    /*
    Activityの実装
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        calendarComponent = (application as MyThrowTrash).appComponent.calendarComponent().create()
        calendarComponent.inject(this)

        super.onCreate(savedInstanceState)
        Log.d(this.javaClass.simpleName, "onCreate")

        presenter.setView(this)

        activityCalendarBinding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(activityCalendarBinding.root)

        setSupportActionBar(activityCalendarBinding.calendarToolbar)

        activityCalendarBinding.calendarPager.offscreenPageLimit = 3

        val cPagerAdapter = CalendarPagerAdapter(this)

        // ツールバーのタイトルはonCreateOptionsで初期化されるためインスタンス変数に格納後に
        // onCreateOptions内で設定する
        activityCalendarBinding.calendarToolbar.title = savedInstanceState?.getString(TITLE)
            ?: "${calendarManager.getYear()}年${calendarManager.getMonth()}月"

        if(savedInstanceState == null) {
            // アプリ起動時はDBと同期をとる
            launch {
                if (configRepository.getSyncState() == CalendarUseCase.SYNC_COMPLETE ||
                        configRepository.getSyncState() == CalendarUseCase.SYNC_NO) {
                    configRepository.setSyncState(CalendarUseCase.SYNC_WAITING)
                }
                launch {
                    controller.syncData()
                }.join()
                // リモートDBとの同期後にViewPagerを生成する
                activityCalendarBinding.calendarPager.adapter = cPagerAdapter

                // レビュー促進処理
                if(usageInfoService.isContinuousUsed() && !usageInfoService.isReviewed()) {
                    // レビューダイアログを出す
                    usageInfoService.showReviewDialog(this@CalendarActivity)
                }
            }
        } else {
            // アクティビティ再生成時はCalendarFragmentから即座にデータ更新が行われるためPagerAdapterの設定を同期する
            activityCalendarBinding.calendarPager.adapter = cPagerAdapter
        }
        activityCalendarBinding.calendarPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageScrollStateChanged(state: Int) {
                if(state == ViewPager2.SCROLL_STATE_IDLE) {
                    Log.d(
                        this.javaClass.simpleName,
                        "Scrolled page -> ${activityCalendarBinding.calendarPager.currentItem}"
                    )
                    val adapter: CalendarPagerAdapter =
                        activityCalendarBinding.calendarPager.adapter as CalendarPagerAdapter
                    val fragment: CalendarFragment =
                        supportFragmentManager.findFragmentByTag("f${activityCalendarBinding.calendarPager.currentItem}") as CalendarFragment
                    // Activityのタイトルを変更
                    activityCalendarBinding.calendarToolbar.title = "${fragment.arguments?.getInt(CalendarFragment.YEAR)}年${fragment.arguments?.getInt(
                        CalendarFragment.MONTH
                    )}月"
                    if(activityCalendarBinding.calendarPager.currentItem == adapter.itemCount - 2) {
                        val currentPosition = activityCalendarBinding.calendarPager.currentItem
                        adapter.addPage()
                        activityCalendarBinding.calendarPager.currentItem = currentPosition
                    }
                }
            }
        })

        setSupportActionBar(activityCalendarBinding.calendarToolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            activityCalendarBinding.calendarActivityRoot,
            activityCalendarBinding.calendarToolbar,
            R.string.menu_item_open_browser,
            R.string.menu_item_open_browser)
        activityCalendarBinding.calendarActivityRoot.addDrawerListener(toggle)
        toggle.syncState()

        activityCalendarBinding.mainNavView.setNavigationItemSelectedListener(this)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(this.javaClass.simpleName, "onSaveInstanceState")
        outState.putString(TITLE,activityCalendarBinding.calendarToolbar.title.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
    /*
    ICalendarViewの実装
     */

    override fun update(viewModel: CalendarViewModel) {
        activityCalendarBinding.calendarPager.adapter?.apply {
            launch {
                withContext(Dispatchers.Main) {
                    val fragment: CalendarFragment =
                        supportFragmentManager.findFragmentByTag("f${viewModel.position}") as CalendarFragment
                    fragment.setCalendar(
                        viewModel.year,
                        viewModel.month,
                        viewModel.dateList,
                        viewModel.trashList
                    )
                }
            }
        }
    }

    /*
    FragmentListenerの実装
     */

    override fun onFragmentNotify(notifyCode: Int, data: Intent) {
        when(notifyCode) {
            ActivityCode.CALENDAR_REQUEST_CREATE_FRAGMENT ->
                launch {
                    controller.generateCalendarFromPositionAsync(data.getIntExtra(CalendarFragment.POSITION,0))
                }
        }
    }

    /*
        OnPageChangeListenerの実装
    */

    companion object {
        private const val TITLE = "TITLE"
    }

    inner class CalendarPagerAdapter(activity: FragmentActivity): FragmentStateAdapter (activity) {
        // 初期サイズを指定
        private var mPageCount = 5

        fun addPage() {
            mPageCount++
            super.notifyDataSetChanged()
        }

        override fun createFragment(position: Int): Fragment {
            return CalendarFragment.newInstance(
                position
            )
        }

        override fun getItemCount(): Int {
            return mPageCount
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(this.javaClass.simpleName, item.itemId.toString())
        when(item.itemId) {
            R.id.menuItemAdd -> {
                val intent = Intent(this, EditActivity::class.java)
                activityLauncher.launch(intent)
            }
            R.id.menuItemList -> {
                val intent = Intent(
                    this,
                    ScheduleListActivity::class.java
                )
                activityLauncher.launch(intent)
            }
            R.id.menuItemNotification -> {
                val intent = Intent(this, AlarmActivity::class.java)
                startActivity(intent)
            }
            R.id.menuItemShare -> {
                val intent = Intent(
                    this,
                    ConnectActivity::class.java
                )

                activityLauncher.launch(intent)
            }
            R.id.menuItemAsk -> {
                val intent = Intent(this, InquiryActivity::class.java)
                startActivity(intent)
            }
            R.id.menuItemInfo -> {
                val intent = Intent(this, InformationActivity::class.java)
                activityLauncher.launch(intent)
            }
            R.id.menuItemLicense -> {
                OssLicensesMenuActivity.setActivityTitle("ライセンス")
                startActivity(Intent(applicationContext, OssLicensesMenuActivity::class.java))
            }
        }
        activityCalendarBinding.calendarActivityRoot.closeDrawer(GravityCompat.START)
        return true
    }
}

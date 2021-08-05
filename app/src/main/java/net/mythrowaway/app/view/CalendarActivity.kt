package net.mythrowaway.app.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.*
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.ICalendarView
import net.mythrowaway.app.adapter.controller.CalendarControllerImpl
import net.mythrowaway.app.databinding.ActivityCalendarBinding
import net.mythrowaway.app.viewmodel.CalendarViewModel
import net.mythrowaway.app.usecase.CalendarUseCase
import net.mythrowaway.app.usecase.ICalendarManager
import net.mythrowaway.app.usecase.IConfigRepository

class CalendarActivity : AppCompatActivity(),
    CalendarFragment.FragmentListener,
    ICalendarView,CoroutineScope by MainScope() {
    private lateinit var controller: CalendarControllerImpl
    private lateinit var activityCalendarBinding: ActivityCalendarBinding
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            launch {
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
            }
        }
    }

    /*
    Activityの実装
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(this.javaClass.simpleName, "onCreate")

        controller =
            CalendarControllerImpl(
                this,
                DIContainer.resolve(
                    ICalendarManager::class.java
                )!!
            )

        activityCalendarBinding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(activityCalendarBinding.root)

        activityCalendarBinding.calendarPager.offscreenPageLimit = 3

        activityCalendarBinding.addScheduleButton.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            activityLauncher.launch(intent)
        }

        activityCalendarBinding.listButton.setOnClickListener {
            val intent = Intent(
                this,
                ScheduleListActivity::class.java
            )
            activityLauncher.launch(intent)
        }

        activityCalendarBinding.alarmButton.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }

        activityCalendarBinding.connectButton.setOnClickListener {
            val intent = Intent(
                this,
                ConnectActivity::class.java
            )

            startActivity(intent)
        }

        activityCalendarBinding.helpButton.setOnClickListener {
            val intent = Intent(this, InquiryActivity::class.java)
            startActivity(intent)
        }

        val calendarManager = DIContainer.resolve(
            ICalendarManager::class.java
        )!!

        val cPagerAdapter = CalendarPagerAdapter(this)

        title = savedInstanceState?.getString(TITLE)
            ?: "${calendarManager.getYear()}年${calendarManager.getMonth()}月"

        if(savedInstanceState == null) {
            // アプリ起動時はDBと同期をとる
            launch {
                val configRepository = DIContainer.resolve(
                    IConfigRepository::class.java
                )
                if (configRepository?.getSyncState() == CalendarUseCase.SYNC_COMPLETE) {
                    configRepository.setSyncState(CalendarUseCase.SYNC_WAITING)
                }
                launch {
                    controller.syncData()
                }.join()
                // リモートDBとの同期後にViewPagerを生成する
                activityCalendarBinding.calendarPager.adapter = cPagerAdapter
            }
        } else {
            // アクティビティ再生性時はCalendarFragmentから即座にデータ更新が行われるためPagerAdapterの設定を同期する
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
                    title = "${fragment.arguments?.getInt(CalendarFragment.YEAR)}年${fragment.arguments?.getInt(
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
    }

    override fun onStart() {
        super.onStart()
        Log.d(this.javaClass.simpleName, "onStart")

    }

    override fun onPause() {
        super.onPause()
        Log.d(this.javaClass.simpleName, "onPause")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(this.javaClass.simpleName, "onSaveInstanceState")
        outState.putString(TITLE,title.toString())
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
}

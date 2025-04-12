package net.mythrowaway.app.module.trash.presentation.view.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.*
import net.mythrowaway.app.R
import net.mythrowaway.app.application.MyThrowTrash
import net.mythrowaway.app.application.di.CalendarComponent
import net.mythrowaway.app.databinding.ActivityCalendarBinding
import net.mythrowaway.app.module.migration.usecase.VersionRepositoryInterface
import net.mythrowaway.app.module.review.usecase.ReviewUseCase
import net.mythrowaway.app.module.alarm.presentation.view.AlarmActivity
import net.mythrowaway.app.module.account.presentation.view.AccountActivity
import net.mythrowaway.app.module.account_link.presentation.view.AccountLinkActivity
import net.mythrowaway.app.module.inquiry.presentation.view.InquiryActivity
import net.mythrowaway.app.module.trash.presentation.view.edit.EditActivity
import net.mythrowaway.app.module.trash.presentation.view.edit.EditScreenType
import net.mythrowaway.app.module.trash.presentation.view.share.ShareActivity
import net.mythrowaway.app.module.trash.presentation.view.share.ShareScreenType
import net.mythrowaway.app.module.trash.presentation.view_model.viewModelFactory
import net.mythrowaway.app.module.trash.presentation.view_model.CalendarViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class CalendarActivity :
  AppCompatActivity(),
  NavigationView.OnNavigationItemSelectedListener,
  MonthCalendarFragment.MonthCalendarFragmentListener,
  CoroutineScope by MainScope() {
  @Inject
  lateinit var configRepository: VersionRepositoryInterface
  @Inject
  lateinit var reviewUseCase: ReviewUseCase
  @Inject
  lateinit var calendarViewModelFactory: CalendarViewModel.Factory

  lateinit var calendarComponent: CalendarComponent

  private lateinit var activityCalendarBinding: ActivityCalendarBinding

  private val calendarViewModel: CalendarViewModel by lazy {
    ViewModelProvider(
      this,
      viewModelFactory {
        calendarViewModelFactory.create()
      }
    )[CalendarViewModel::class.java]
  }


  private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    launch {
      activityCalendarBinding.indicatorLayout.visibility = View.VISIBLE
      calendarViewModel.refresh()
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

    activityCalendarBinding = ActivityCalendarBinding.inflate(layoutInflater)
    setContentView(activityCalendarBinding.root)

    setSupportActionBar(activityCalendarBinding.calendarToolbar)

    activityCalendarBinding.calendarPager.offscreenPageLimit = 3

    val cPagerAdapter = CalendarPagerAdapter(this)

    // ツールバーのタイトルはonCreateOptionsで初期化されるためインスタンス変数に格納後に
    // onCreateOptions内で設定する
    val today = LocalDate.now()
    activityCalendarBinding.calendarToolbar.title = savedInstanceState?.getString(TITLE)
      ?: "${today.year}年${today.monthValue}月"

    if(savedInstanceState == null) {
      // アプリ起動時はDBと同期をとる
      val parent = this
      lifecycleScope.launch {
        val app = application as MyThrowTrash
        app.isAppInitialized().collect { isInitialized ->
          when (isInitialized) {
            true -> {
              Log.d(this@CalendarActivity.javaClass.simpleName, "Auth initialization completed successfully")
              activityCalendarBinding.indicatorLayout.visibility = View.VISIBLE
              calendarViewModel.refresh()
              activityCalendarBinding.calendarPager.adapter = cPagerAdapter
              reviewUseCase.updateLastLaunchedTime(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond())
              val review = reviewUseCase.getReview()
              // レビュー促進処理
              if(!review.reviewed && review.continuousUseDateCount >= 2) {
                val reviewManager = ReviewManagerFactory.create(applicationContext)
                val request = reviewManager.requestReviewFlow()
                request.addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = reviewManager.launchReviewFlow(parent, reviewInfo)
                    flow.addOnCompleteListener {
                      Log.d(this.javaClass.simpleName, "review complete")
                    }
                    reviewUseCase.review(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond())
                  } else {
                    Log.e(this.javaClass.simpleName, "Review flow failed")
                  }
                }
              }
            }
            false -> {
              Log.e(this@CalendarActivity.javaClass.simpleName, "Auth initialization failed")
            }
            null -> {
              // まだ初期化中なので待機
              Log.d(this@CalendarActivity.javaClass.simpleName, "Waiting for auth initialization")
            }
          }
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
          val fragment: MonthCalendarFragment =
            supportFragmentManager.findFragmentByTag("f${activityCalendarBinding.calendarPager.currentItem}") as MonthCalendarFragment
          // Activityのタイトルを変更
          activityCalendarBinding.calendarToolbar.title = "${fragment.arguments?.getInt(
            MonthCalendarFragment.YEAR
          )}年${fragment.arguments?.getInt(
            MonthCalendarFragment.MONTH
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
      Log.d(this.javaClass.simpleName, "Create Calendar Fragment -> $position")
      return MonthCalendarFragment.newInstance(
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
          EditActivity::class.java
        )
        intent.putExtra(EditActivity.SCREEN_TYPE, EditScreenType.List.name)
        activityLauncher.launch(intent)
      }
      R.id.menuItemNotification -> {
        val intent = Intent(this, AlarmActivity::class.java)
        startActivity(intent)
      }
      R.id.menuItemPublish -> {
        val intent = Intent(
          this,
          ShareActivity::class.java
        )
        intent.putExtra(ShareActivity.SCREEN_TYPE, ShareScreenType.Publish.name)

        activityLauncher.launch(intent)
      }
      R.id.menuItemImport -> {
        val intent = Intent(
          this,
          ShareActivity::class.java
        )
        intent.putExtra(ShareActivity.SCREEN_TYPE, ShareScreenType.Activate.name)

        activityLauncher.launch(intent)
      }
      R.id.menuItemAlexa -> {
        val intent = Intent(this, AccountLinkActivity::class.java)
        startActivity(intent)
      }
      R.id.menuItemAsk -> {
        val intent = Intent(this, InquiryActivity::class.java)
        startActivity(intent)
      }
      R.id.menuItemInfo -> {
        val intent = Intent(this, AccountActivity::class.java)
        activityLauncher.launch(intent)
      }
      R.id.menuItemLicense -> {
        OssLicensesMenuActivity.setActivityTitle("ライセンス")
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
      }
    }
    activityCalendarBinding.calendarActivityRoot.closeDrawer(GravityCompat.START)
    return true
  }

  override fun onFinishRefresh() {
    activityCalendarBinding.indicatorLayout.visibility = View.INVISIBLE
  }
}

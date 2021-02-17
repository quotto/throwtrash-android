package net.mythrowaway.app.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.coroutines.*
import net.mythrowaway.app.adapter.ICalendarView
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.controller.CalendarControllerImpl
import net.mythrowaway.app.adapter.di.CalendarComponent
import net.mythrowaway.app.usecase.*
import net.mythrowaway.app.viewmodel.CalendarViewModel
import javax.inject.Inject

class CalendarActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,
    CalendarFragment.FragmentListener,
    ICalendarView,CoroutineScope by MainScope() {
    @Inject
    lateinit var controller: CalendarControllerImpl
    @Inject
    lateinit var presenter: ICalendarPresenter
    @Inject
    lateinit var configRepository: IConfigRepository
    @Inject
    lateinit var calendarManager: CalendarManager

    lateinit var calendarComponent: CalendarComponent
    /*
    Activityの実装
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        calendarComponent = (application as MyThrowTrash).appComponent.calendarComponent().create()
        calendarComponent.inject(this)

        super.onCreate(savedInstanceState)
        Log.d(this.javaClass.simpleName, "onCreate")

        presenter.setView(this)

        setContentView(R.layout.activity_calendar)
        addScheduleButton.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivityForResult(
                intent,
                ActivityCode.CALENDAR_REQUEST_UPDATE
            )
        }

        listButton.setOnClickListener {
            val intent = Intent(
                this,
                ScheduleListActivity::class.java
            )
            startActivityForResult(
                intent,
                ActivityCode.CALENDAR_REQUEST_UPDATE
            )
        }

        alarmButton.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }

        connectButton.setOnClickListener {
            val intent = Intent(
                this,
                ConnectActivity::class.java
            )
            startActivityForResult(intent, ActivityCode.CALENDAR_REQUEST_UPDATE)
        }

        val cPagerAdapter = CalendarPagerAdapter(supportFragmentManager)
        calendarPager.addOnPageChangeListener(this)

        title = savedInstanceState?.getString(TITLE)
            ?: "${calendarManager.getYear()}年${calendarManager.getMonth()}月"

        if(savedInstanceState == null) {
            // アプリ起動時はDBと同期をとる
            launch {
                if (configRepository?.getSyncState() == CalendarUseCase.SYNC_COMPLETE) {
                    configRepository.setSyncState(CalendarUseCase.SYNC_WAITING)
                }
                launch {
                    controller.syncData()
                }.join()
                // リモートDBとの同期後にViewPagerを生成する
                calendarPager.adapter = cPagerAdapter
            }
        } else {
            // アクティビティ再生性時はCalendarFragmentから即座にデータ更新が行われるためPagerAdapterの設定を同期する
            calendarPager.adapter = cPagerAdapter
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            ActivityCode.CALENDAR_REQUEST_UPDATE -> {
                if(resultCode == Activity.RESULT_OK) {
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
        }
    }

    /*
    ICalendarViewの実装
     */

    override fun update(viewModel: CalendarViewModel) {
        calendarPager.adapter?.apply {
            launch {
                withContext(Dispatchers.Main) {
                    val fragment =
                        instantiateItem(calendarPager, viewModel.position) as CalendarFragment
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

    override fun onPageScrollStateChanged(state: Int) {
        if(state == ViewPager.SCROLL_STATE_IDLE) {
            Log.d(this.javaClass.simpleName, "Scrolled page -> ${calendarPager.currentItem}")
            val adapter: CalendarActivity.CalendarPagerAdapter = calendarPager.adapter as CalendarActivity.CalendarPagerAdapter
            val fragment = adapter.instantiateItem(calendarPager,calendarPager.currentItem) as CalendarFragment
            // Activityのタイトルを変更
            title = "${fragment.arguments?.getInt(CalendarFragment.YEAR)}年${fragment.arguments?.getInt(
                CalendarFragment.MONTH
            )}月"
            if(calendarPager.currentItem == adapter.count - 2) {
                val currentPosition = calendarPager.currentItem
                adapter.addPage()
                calendarPager.currentItem = currentPosition
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }

    companion object {
        private const val TITLE = "TITLE"
    }

    inner class CalendarPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        // 初期サイズを指定
        private var mPageCount = 5

        fun addPage() {
            mPageCount++
            super.notifyDataSetChanged()
        }
        override fun getItem(position: Int): Fragment {
            return CalendarFragment.newInstance(
                position
            )
        }

        override fun getCount(): Int {
            return mPageCount
        }
    }
}

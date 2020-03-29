package com.example.mythrowtrash.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.mythrowtrash.R
import com.example.mythrowtrash.adapter.*
import com.example.mythrowtrash.usecase.CalendarUseCase
import com.example.mythrowtrash.usecase.ICalendarManager
import com.example.mythrowtrash.usecase.IConfigRepository
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.coroutines.*

class CalendarActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,CalendarFragment.FragmentListener,ICalendarView,CoroutineScope by MainScope() {
    private lateinit var controller:CalendarController
    override fun update(viewModel: CalendarViewModel) {
        calendarPager.adapter?.apply {
                launch {
                withContext(Dispatchers.Main) {
                    val fragment =
                        instantiateItem(calendarPager, viewModel.position) as CalendarFragment
                    fragment.updateCalendar(
                        viewModel.year,
                        viewModel.month,
                        viewModel.dateList,
                        viewModel.trashList
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        controller = CalendarController(this,DIContainer.resolve(ICalendarManager::class.java)!!)

        setContentView(R.layout.activity_calendar)
        addScheduleButton.setOnClickListener {
            val intent: Intent = Intent(this, EditActivity::class.java)
            startActivityForResult(intent, REQUEST_UPDATE)
        }

        listButton.setOnClickListener {
            val intent: Intent = Intent(this,ScheduleListActivity::class.java)
            startActivityForResult(intent, REQUEST_UPDATE)
        }

        alarmButton.setOnClickListener {
            val intent: Intent = Intent(this,AlarmActivity::class.java)
            startActivity(intent)
        }

        connectButton.setOnClickListener {
            val intent = Intent(this,ConnectActivity::class.java)
            startActivity(intent)
        }

        val calendarManager = DIContainer.resolve(ICalendarManager::class.java)!!
        title = savedInstanceState?.getString(TITLE) ?: "${calendarManager.getYear()}年${calendarManager.getMonth()}月"

        val cPagerAdapter = CalendarPagerAdapter(supportFragmentManager,controller)
        calendarPager.addOnPageChangeListener(this)

        launch {
            val configRepository = DIContainer.resolve(IConfigRepository::class.java)
            if(configRepository?.getSyncState() == CalendarUseCase.SYNC_COMPLETE) {
                configRepository.setSyncState(CalendarUseCase.SYNC_WAITING)
            }
            launch {
                controller.syncData()
            }.join()
            // リモートDBとの同期後にViewPagerを生成する
            calendarPager.adapter = cPagerAdapter
        }
    }

    override fun onPause() {
        super.onPause()
        println("[MyApp - Main Activity] onPause")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("[MyApp - Main Activity] onSaveInstanceState ")
        outState.putString(TITLE,title.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_UPDATE -> {
                if(resultCode == Activity.RESULT_OK) {
                    launch {
                        launch {
                            controller.syncData()
                        }.join()
                        // DB同期後にViewPagerのFragmentを更新する
                        supportFragmentManager.fragments.forEach {
                            if(it is CalendarFragment) {
                                it.arguments?.getInt(CalendarFragment.POSITION)?.let {position->
                                    controller.generateCalendarFromPositionAsync(position)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onFragmentNotify(notifyCode: Int, data: Intent) {
        when(notifyCode) {
            REQUEST_FRAGMENT_CREATED ->
                data?.getIntExtra(CalendarFragment.POSITION,0)?.let {
                    launch {
                        controller.generateCalendarFromPositionAsync(it)
                    }
                }
        }
    }



    companion object {
        private const val TITLE = "TITLE"
        const val REQUEST_FRAGMENT_CREATED = 0
        const val REQUEST_UPDATE = 1
    }

    inner class CalendarPagerAdapter(fm: FragmentManager, private val controller: ICalendarController): FragmentStatePagerAdapter(fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        // 初期サイズを指定
        private var mPageCount = 5

        fun addPage() {
            mPageCount++
            super.notifyDataSetChanged()
        }
        override fun getItem(position: Int): Fragment {
            val calendarFragment:CalendarFragment = CalendarFragment.newInstance(position)
            return calendarFragment
        }

        override fun getCount(): Int {
            return mPageCount
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        if(state == ViewPager.SCROLL_STATE_IDLE) {
            println("[MyApp - MainActivity]scrolled page: ${calendarPager.currentItem}")
            val adapter:CalendarPagerAdapter = calendarPager.adapter as CalendarPagerAdapter
            val fragment = adapter.instantiateItem(calendarPager,calendarPager.currentItem) as CalendarFragment
            // Activityのタイトルを変更
            title = "${fragment.arguments?.getInt(CalendarFragment.YEAR)}年${fragment.arguments?.getInt(CalendarFragment.MONTH)}月"
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
}

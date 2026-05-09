package net.mythrowaway.app.module.trash.presentation.view.calendar

import android.content.Intent
import android.Manifest
import android.os.Bundle
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.text.TextPaint
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.annotation.VisibleForTesting
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
import net.mythrowaway.app.module.account_link.presentation.view.AccountLinkActivity
import net.mythrowaway.app.module.info.presentation.view.InformationActivity
import net.mythrowaway.app.module.inquiry.presentation.view.InquiryActivity
import net.mythrowaway.app.module.other.presentation.view.OtherActivity
import net.mythrowaway.app.module.theme.usecase.ThemeUseCase
import net.mythrowaway.app.module.trash.presentation.view.edit.EditActivity
import net.mythrowaway.app.module.trash.presentation.view.edit.EditScreenType
import net.mythrowaway.app.module.trash.presentation.view.share.ShareActivity
import net.mythrowaway.app.module.trash.presentation.view.share.ShareScreenType
import net.mythrowaway.app.module.trash.presentation.view_model.viewModelFactory
import net.mythrowaway.app.module.trash.presentation.view_model.CalendarViewModel
import net.mythrowaway.app.module.trash.presentation.view_model.ScheduleSearchImportViewModel
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
    lateinit var themeUseCase: ThemeUseCase
    @Inject
    lateinit var calendarViewModelFactory: CalendarViewModel.Factory
    @Inject
    lateinit var scheduleSearchImportViewModelFactory: ScheduleSearchImportViewModel.Factory

    lateinit var calendarComponent: CalendarComponent

    private lateinit var activityCalendarBinding: ActivityCalendarBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var isRefreshingInProgress: Boolean = false
    @VisibleForTesting
    internal var refreshTriggerCount: Int = 0
        private set

    private val calendarViewModel: CalendarViewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory {
                calendarViewModelFactory.create()
            }
        )[CalendarViewModel::class.java]
    }
    private val scheduleSearchImportViewModel: ScheduleSearchImportViewModel by lazy {
        ViewModelProvider(this, scheduleSearchImportViewModelFactory)[ScheduleSearchImportViewModel::class.java]
    }

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        launch {
            Log.d(this.javaClass.simpleName, "Activity Result OK")
            startRefresh()
        }
    }
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
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

        // ステータスバーとナビゲーションバーのインセットを適切に処理
        setupWindowInsets()

        setSupportActionBar(activityCalendarBinding.calendarToolbar)

        activityCalendarBinding.calendarPager.offscreenPageLimit = 3
        activityCalendarBinding.calendarSwipeRefresh.setOnRefreshListener {
            startRefresh()
        }

        val cPagerAdapter = CalendarPagerAdapter(this)

        // ツールバーのタイトルはonCreateOptionsで初期化されるためインスタンス変数に格納後に
        // onCreateOptions内で設定する
        val today = LocalDate.now()
        activityCalendarBinding.calendarToolbar.title = savedInstanceState?.getString(TITLE)
            ?: "${today.year}年${today.monthValue}月"

        if(savedInstanceState == null) {
            // アプリ起動時はDBと同期をとる
            val parent = this
            launch {
                startRefresh()
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
            if (scheduleSearchImportViewModel.shouldShowStartupDialog()) {
                scheduleSearchImportViewModel.suppressStartupDialog()
                showScheduleSearchImportDialog()
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

        drawerToggle = ActionBarDrawerToggle(
            this,
            activityCalendarBinding.calendarActivityRoot,
            activityCalendarBinding.calendarToolbar,
            R.string.menu_item_open_browser,
            R.string.menu_item_open_browser)
        activityCalendarBinding.calendarActivityRoot.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        drawerToggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.md_theme_onBackground)

        activityCalendarBinding.mainNavView.setNavigationItemSelectedListener(this)
        activityCalendarBinding.darkModeSwitch.setOnCheckedChangeListener { _, checked ->
            themeUseCase.updateTheme(checked)
            invalidateOptionsMenu()
        }
    }

    override fun onResume() {
        super.onResume()
        activityCalendarBinding.darkModeSwitch.apply {
            setOnCheckedChangeListener(null)
            isChecked = themeUseCase.isDarkModeEnabled()
            setOnCheckedChangeListener { _, checked ->
                themeUseCase.updateTheme(checked)
                invalidateOptionsMenu()
            }
        }
    }

    /**
     * ウィンドウのインセット（ステータスバーやナビゲーションバーなど）を適切に処理する
     * API 35/36ではインセットの処理が厳格化されているため、明示的な処理が必要
     */
    private fun setupWindowInsets() {
        // ViewのWindowInsetsを監視
        ViewCompat.setOnApplyWindowInsetsListener(activityCalendarBinding.calendarContainer) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // コンテナビューのパディングを更新（必要に応じて）
            view.updateLayoutParams<MarginLayoutParams>{
                topMargin = insets.top
            }
            // インセットが消費されたことをシステムに通知
            WindowInsetsCompat.CONSUMED
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.calendar_actions, menu)
        updateRefreshIcon(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return when (item.itemId) {
            R.id.menuItemRefresh -> {
                startRefresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
            R.id.menuItemExceptionDays -> {
                val intent = Intent(
                    this,
                    EditActivity::class.java
                )
                intent.putExtra(EditActivity.SCREEN_TYPE, EditScreenType.CommonExcludeDayOfMonth.name)
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
                val intent = Intent(this, InformationActivity::class.java)
                activityLauncher.launch(intent)
            }
            R.id.menuItemOther -> {
                startActivity(Intent(this, OtherActivity::class.java))
            }
        }
        activityCalendarBinding.calendarActivityRoot.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFinishRefresh() {
        activityCalendarBinding.calendarSwipeRefresh.isRefreshing = false
        isRefreshingInProgress = false
    }

    private fun startRefresh() {
        if (isRefreshingInProgress) {
            return
        }
        isRefreshingInProgress = true
        refreshTriggerCount++
        activityCalendarBinding.calendarSwipeRefresh.isRefreshing = true
        launch {
            calendarViewModel.refresh()
        }
    }

    private fun updateRefreshIcon(menu: Menu) {
        val refreshItem = menu.findItem(R.id.menuItemRefresh)
        val iconRes = if (themeUseCase.isDarkModeEnabled()) {
            R.drawable.ic_outline_autorenew_white_24
        } else {
            R.drawable.ic_outline_autorenew_black_24
        }
        refreshItem.icon = ContextCompat.getDrawable(this, iconRes)
    }

    private fun showScheduleSearchImportDialog() {
        val input = EditText(this).apply {
            hint = getString(R.string.label_schedule_search_input)
            filters = arrayOf(InputFilter.LengthFilter(50))
            setSingleLine(true)
        }
        val noticeLink = TextView(this).apply {
            text = createScheduleSearchNoticeText()
            setPadding(0, 24, 0, 0)
            movementMethod = LinkMovementMethod.getInstance()
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val padding = resources.getDimensionPixelSize(R.dimen.text_margin)
            setPadding(padding, 0, padding, 0)
            addView(input)
            addView(noticeLink)
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.title_schedule_search_import_dialog)
            .setView(content)
            .setPositiveButton(R.string.label_schedule_search_execute_button, null)
            .setNegativeButton(R.string.label_close_button, null)
            .create()
        dialog.setOnShowListener {
            val executeButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            executeButton.isEnabled = false
            executeButton.setOnClickListener {
                requestNotificationPermissionIfNeeded()
                scheduleSearchImportViewModel.startImport(input.text.toString())
                Toast.makeText(
                    this,
                    R.string.message_schedule_search_import_started,
                    Toast.LENGTH_LONG
                ).show()
                dialog.dismiss()
            }
            input.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    executeButton.isEnabled = !s.isNullOrBlank()
                }
                override fun afterTextChanged(s: Editable?) = Unit
            })
        }
        dialog.show()
    }

    private fun showScheduleSearchNoticeDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_schedule_search_notice_dialog)
            .setMessage(R.string.text_schedule_search_notice)
            .setPositiveButton(R.string.label_close_button, null)
            .show()
    }

    private fun createScheduleSearchNoticeText(): SpannableString {
        val linkText = getString(R.string.text_schedule_search_notice_link)
        val fullText = linkText + getString(R.string.text_schedule_search_notice_suffix)
        return SpannableString(fullText).apply {
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        showScheduleSearchNoticeDialog()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = true
                    }
                },
                0,
                linkText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

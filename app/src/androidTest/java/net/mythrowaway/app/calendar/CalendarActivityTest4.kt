package net.mythrowaway.app.calendar


import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.mythrowaway.app.R
import org.hamcrest.Matchers.*
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import net.mythrowaway.app.AndroidTestUtil.Companion.childAtPosition
import net.mythrowaway.app.view.CalendarActivity
import org.junit.After
import org.junit.Before

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest4 {

  @Rule
  @JvmField
  var mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)

  private var mIdlingResource: CountingIdlingResource? = null

  @Before
  fun setUp(){
    mActivityScenarioRule.scenario.onActivity { activity ->
      mIdlingResource = activity.getIdlingResources()
      IdlingRegistry.getInstance().register(mIdlingResource)
    }
  }

  @After
  fun tearDown (){
    if(mIdlingResource != null) {
      IdlingRegistry.getInstance().unregister(mIdlingResource)
    }
  }

  /*
  登録済みゴミ出し情報を編集するシナリオ
  - 最初の登録したゴミ出し情報で編集画面が表示されること（もえるゴミ、毎週日曜日）
  - 編集後の情報がカレンダー画面に表示されること（もえるゴミ⇒ペットボトル、日曜日⇒月曜日）
   */

  @Test
  fun calendarActivityTest4() {
    val appCompatImageButton = onView(
      allOf(
        childAtPosition(
          allOf(
            withId(R.id.calendarToolbar),
            childAtPosition(
              withId(R.id.calendarContainer),
              0
            )
          ),
          1
        ),
        isDisplayed()
      )
    )
    appCompatImageButton.perform(click())

    val navigationMenuItemView = onView(
      allOf(
        withId(R.id.menuItemAdd),
        childAtPosition(
          allOf(
            withId(R.id.design_navigation_view),
            childAtPosition(
              withId(R.id.main_nav_view),
              0
            )
          ),
          1
        ),
        isDisplayed()
      )
    )
    navigationMenuItemView.perform(click())

    val appCompatButton = onView(
      allOf(
        withId(R.id.registerButton), withText("登録"),
        childAtPosition(
          allOf(
            withId(R.id.buttonContainer),
            childAtPosition(
              withId(R.id.mainScheduleContainer),
              3
            )
          ),
          0
        ),
        isDisplayed()
      )
    )
    appCompatButton.perform(click())

    val appCompatImageButton2 = onView(
      allOf(
        withContentDescription("Chromeで開く"),
        childAtPosition(
          allOf(
            withId(R.id.calendarToolbar),
            childAtPosition(
              withId(R.id.calendarContainer),
              0
            )
          ),
          1
        ),
        isDisplayed()
      )
    )
    appCompatImageButton2.perform(click())

    val navigationMenuItemView2 = onView(
      allOf(
        withId(R.id.menuItemList),
        childAtPosition(
          allOf(
            withId(R.id.design_navigation_view),
            childAtPosition(
              withId(R.id.main_nav_view),
              0
            )
          ),
          2
        ),
        isDisplayed()
      )
    )
    navigationMenuItemView2.perform(click())

    val recyclerView = onView(
      allOf(
        withId(R.id.scheduleListFragment),
        childAtPosition(
          withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
          0
        )
      )
    )
    recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

    val textView = onView(
      allOf(
        withId(android.R.id.text1), withText("もえるゴミ"),
        withParent(
          allOf(
            withId(R.id.trashTypeList),
            withParent(withId(R.id.trashTypeContainer))
          )
        ),
        isDisplayed()
      )
    )
    textView.check(matches(withText("もえるゴミ")))

    val textView2 = onView(
      allOf(
        withId(android.R.id.text1), withText("日曜日"),
        withParent(
          allOf(
            withId(R.id.weekdayWeekdayList),
            withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
          )
        ),
        isDisplayed()
      )
    )
    textView2.check(matches(withText("日曜日")))

    val appCompatSpinner = onView(
      allOf(
        withId(R.id.weekdayWeekdayList),
        childAtPosition(
          childAtPosition(
            withId(R.id.scheduleInput),
            0
          ),
          1
        ),
        isDisplayed()
      )
    )
    appCompatSpinner.perform(click())

    val appCompatTextView = onData(anything())
      .inAdapterView(
        childAtPosition(
          withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
          0
        )
      )
      .atPosition(1)
    appCompatTextView.perform(click())

    val appCompatSpinner2 = onView(
      allOf(
        withId(R.id.trashTypeList),
        childAtPosition(
          allOf(
            withId(R.id.trashTypeContainer),
            childAtPosition(
              withId(R.id.mainScheduleContainer),
              2
            )
          ),
          2
        ),
        isDisplayed()
      )
    )
    appCompatSpinner2.perform(click())

    val appCompatTextView2 = onData(anything())
      .inAdapterView(
        childAtPosition(
          withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
          0
        )
      )
      .atPosition(5)
    appCompatTextView2.perform(click())

    val appCompatButton2 = onView(
      allOf(
        withId(R.id.registerButton), withText("登録"),
        childAtPosition(
          allOf(
            withId(R.id.buttonContainer),
            childAtPosition(
              withId(R.id.mainScheduleContainer),
              3
            )
          ),
          0
        ),
        isDisplayed()
      )
    )
    appCompatButton2.perform(click())

    pressBack()

    val editText = onView(
      allOf(
        withId(R.id.trashText),
        childAtPosition(
          allOf(
            withId(R.id.linearLayout),
            childAtPosition(
              withId(R.id.calendar),
              8
            )
          ),
          1
        ),
        isDisplayed()
      )
    )
    editText.check(matches(withText("ペットボトル")))
  }
}

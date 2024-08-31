package net.mythrowaway.app.edit


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.mythrowaway.app.R
import net.mythrowaway.app.domain.trash.presentation.view.calendar.CalendarActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditActivityTest5 {

  @Rule
  @JvmField
  var mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)


  /*
  例外設定日のシナリオ
  - 例外設定日画面の初期状態では1件もデータが無いため保存ボタンが無効であること
  - 1件以上の例外日を追加すると保存ボタンが有効になること
  - 10件の例外設定日を追加すると追加ボタンが表示されなくなること
  - 例外日登録後に一覧画面から該当するゴミの編集画面に遷移し、そこから例外設定画面に遷移すると登録時のデータが復元されること。
   */
  @Test
  fun editActivityTest5() {
    val appCompatImageButton = onView(
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

//    val appCompatButton = onView(
//      allOf(
//        withId(R.id.buttonSetExcludeDate), withText("例外日を設定"),
//        childAtPosition(
//          allOf(
//            withId(R.id.mainScheduleContainer),
//            childAtPosition(
//              withId(R.id.editMainLayout),
//              0
//            )
//          ),
//          1
//        ),
//        isDisplayed()
//      )
//    )
//    appCompatButton.perform(click())

//    val textView = onView(
//      allOf(
//        withId(R.id.trashName), withText("もえるゴミ"),
//        withParent(
//          allOf(
//            withId(R.id.editExcludeDate),
//            withParent(withId(android.R.id.content))
//          )
//        ),
//        isDisplayed()
//      )
//    )
//    textView.check(matches(withText("もえるゴミ")))

    // 例外日設定を開いた直後は保存ボタンが無効状態であること
//    val registerExcludeButton = onView(
//      allOf(
//        withId(R.id.buttonRegisterExcludeDate), withText("保存"),
//        childAtPosition(
//          allOf(
//            withId(R.id.editExcludeDate),
//            childAtPosition(
//              withId(android.R.id.content),
//              0
//            )
//          ),
//          3
//        ),
//        isDisplayed()
//      )
//    )
//    registerExcludeButton.check(matches(isNotEnabled()))

//    val appCompatImageButton2 = onView(
//      allOf(
//        withId(R.id.buttonAddExcludeDate),
//        childAtPosition(
//          allOf(
//            withId(R.id.listExcludeDate),
//            childAtPosition(
//              withId(R.id.scrollViewExcludeDate),
//              0
//            )
//          ),
//          0
//        )
//      )
//    )
//    appCompatImageButton2.perform(scrollTo(), click())

    // 例外日を1件以上追加した状態の場合は保存ボタンが有効であること
//    registerExcludeButton.check(matches(isEnabled()))

//    val appCompatTextView = onView(
//      allOf(
//        withId(R.id.textExcludeDate), withText("1 月 1 日"),
//        childAtPosition(
//          allOf(
//            withId(R.id.excludeList),
//            childAtPosition(
//              withId(R.id.listExcludeDate),
//              0
//            )
//          ),
//          1
//        )
//      )
//    )
//    appCompatTextView.check(matches(withText("1 月 1 日")))
//
//    val appCompatImageButton3 = onView(
//      allOf(
//        withId(R.id.buttonAddExcludeDate),
//        childAtPosition(
//          allOf(
//            withId(R.id.listExcludeDate),
//            childAtPosition(
//              withId(R.id.scrollViewExcludeDate),
//              0
//            )
//          ),
//          1
//        )
//      )
//    )
//    appCompatImageButton3.perform(scrollTo(), click())
//
//    val appCompatImageButton4 = onView(
//      allOf(
//        withId(R.id.buttonAddExcludeDate),
//        childAtPosition(
//          allOf(
//            withId(R.id.listExcludeDate),
//            childAtPosition(
//              withId(R.id.scrollViewExcludeDate),
//              0
//            )
//          ),
//          2
//        )
//      )
//    )
//    appCompatImageButton4.perform(scrollTo(), click())
//
//    val appCompatImageButton5 = onView(
//      allOf(
//        withId(R.id.buttonAddExcludeDate),
//        childAtPosition(
//          allOf(
//            withId(R.id.listExcludeDate),
//            childAtPosition(
//              withId(R.id.scrollViewExcludeDate),
//              0
//            )
//          ),
//          3
//        )
//      )
//    )
//    appCompatImageButton5.perform(scrollTo(), click())
//
//    val appCompatImageButton6 = onView(
//      allOf(
//        withId(R.id.buttonAddExcludeDate),
//        childAtPosition(
//          allOf(
//            withId(R.id.listExcludeDate),
//            childAtPosition(
//              withId(R.id.scrollViewExcludeDate),
//              0
//            )
//          ),
//          4
//        )
//      )
//    )
//    appCompatImageButton6.perform(scrollTo(), click())
//
//    val appCompatImageButton7 = onView(
//      allOf(
//        withId(R.id.buttonAddExcludeDate),
//        childAtPosition(
//          allOf(
//            withId(R.id.listExcludeDate),
//            childAtPosition(
//              withId(R.id.scrollViewExcludeDate),
//              0
//            )
//          ),
//          5
//        )
//      )
//    )
//    appCompatImageButton7.perform(scrollTo(), click())
//
//    val appCompatImageButton8 = onView(
//      allOf(
//        withId(R.id.buttonAddExcludeDate),
//        childAtPosition(
//          allOf(
//            withId(R.id.listExcludeDate),
//            childAtPosition(
//              withId(R.id.scrollViewExcludeDate),
//              0
//            )
//          ),
//          6
//        )
//      )
//    )
//    appCompatImageButton8.perform(scrollTo(), click())
//
//    val appCompatImageButton9 = onView(
//      allOf(
//        withId(R.id.buttonAddExcludeDate),
//        childAtPosition(
//          allOf(
//            withId(R.id.listExcludeDate),
//            childAtPosition(
//              withId(R.id.scrollViewExcludeDate),
//              0
//            )
//          ),
//          7
//        )
//      )
//    )
//    appCompatImageButton9.perform(scrollTo(), click())
//
//    val appCompatImageButton10 = onView(
//      allOf(
//        withId(R.id.buttonAddExcludeDate),
//        childAtPosition(
//          allOf(
//            withId(R.id.listExcludeDate),
//            childAtPosition(
//              withId(R.id.scrollViewExcludeDate),
//              0
//            )
//          ),
//          8
//        )
//      )
//    )
//    appCompatImageButton10.perform(scrollTo(), click())
//
//    val appCompatImageButton11 = onView(
//      allOf(
//        withId(R.id.buttonAddExcludeDate),
//        childAtPosition(
//          allOf(
//            withId(R.id.listExcludeDate),
//            childAtPosition(
//              withId(R.id.scrollViewExcludeDate),
//              0
//            )
//          ),
//          9
//        )
//      )
//    )
//    appCompatImageButton11.perform(scrollTo(),click())
//
//    // 例外日が10個設定された場合は追加ボタンが表示されない
//    val addExcludeButton = onView(
//      allOf(
//        withId(R.id.buttonAddExcludeDate),
//        withParent(
//          allOf(
//            withId(R.id.listExcludeDate),
//            withParent(withId(R.id.scrollViewExcludeDate))
//          )
//        ),
//        isDisplayed()
//      )
//    )
//    addExcludeButton.check(doesNotExist())
//
//    val appCompatImageButton12 = onView(
//      allOf(
//        withId(R.id.buttonRemoveExcludeDate),
//        childAtPosition(
//          allOf(
//            withId(R.id.excludeList),
//            childAtPosition(
//              withId(R.id.listExcludeDate),
//              0
//            )
//          ),
//          0
//        )
//      )
//    )
//    appCompatImageButton12.perform(scrollTo(), click())
//
//    // 1件削除して例外日が10未満になった場合は再度追加ボタンが表示されること
//    onView(withId(R.id.scrollViewExcludeDate)).perform(swipeUp())
//    addExcludeButton.check(matches(isDisplayed()))
//
//    val appCompatButton3 = onView(
//      allOf(
//        withId(R.id.buttonRegisterExcludeDate), withText("保存"),
//        childAtPosition(
//          allOf(
//            withId(R.id.editExcludeDate),
//            childAtPosition(
//              withId(android.R.id.content),
//              0
//            )
//          ),
//          3
//        ),
//        isDisplayed()
//      )
//    )
//    appCompatButton3.perform(click())
//
//    val appCompatButton4 = onView(
//      allOf(
//        withId(R.id.buttonSetExcludeDate), withText("例外日を設定"),
//        childAtPosition(
//          allOf(
//            withId(R.id.mainScheduleContainer),
//            childAtPosition(
//              withId(R.id.editMainLayout),
//              0
//            )
//          ),
//          1
//        ),
//        isDisplayed()
//      )
//    )
//    appCompatButton4.perform(click())
//
//    pressBack()
//
//    val appCompatButton5 = onView(
//      allOf(
//        withId(R.id.registerButton), withText("登録"),
//        childAtPosition(
//          allOf(
//            withId(R.id.buttonContainer),
//            childAtPosition(
//              withId(R.id.mainScheduleContainer),
//              3
//            )
//          ),
//          0
//        ),
//        isDisplayed()
//      )
//    )
//    appCompatButton5.perform(click())

    val appCompatImageButton13 = onView(
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
    appCompatImageButton13.perform(click())

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

//    val recyclerView = onView(
//      allOf(
//        withId(R.id.scheduleListFragment),
//        childAtPosition(
//          withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
//          0
//        )
//      )
//    )
//    recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))
//
//    val appCompatButton6 = onView(
//      allOf(
//        withId(R.id.buttonSetExcludeDate), withText("例外日を設定"),
//        childAtPosition(
//          allOf(
//            withId(R.id.mainScheduleContainer),
//            childAtPosition(
//              withId(R.id.editMainLayout),
//              0
//            )
//          ),
//          1
//        ),
//        isDisplayed()
//      )
//    )
//    appCompatButton6.perform(click())
//
//    val textView2 = onView(
//      allOf(
//        withId(R.id.textExcludeDate), withText("1 月 1 日"),
//        withParentIndex(1),
//        withParent(
//          allOf(
//            withId(R.id.excludeList),
//            withParent(withId(R.id.listExcludeDate)),
//            withParentIndex(0)
//          )
//        ),
//        isDisplayed()
//      )
//    )
//    textView2.check(matches(withText("1 月 1 日")))
  }

  private fun childAtPosition(
    parentMatcher: Matcher<View>, position: Int
  ): Matcher<View> {

    return object : TypeSafeMatcher<View>() {
      override fun describeTo(description: Description) {
        description.appendText("Child at position $position in parent ")
        parentMatcher.describeTo(description)
      }

      public override fun matchesSafely(view: View): Boolean {
        val parent = view.parent
        return parent is ViewGroup && parentMatcher.matches(parent)
                && view == parent.getChildAt(position)
      }
    }
  }
}

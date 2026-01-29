package net.mythrowaway.app.calendar


import android.view.View
import android.widget.ScrollView
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import net.mythrowaway.app.R
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import net.mythrowaway.app.AndroidTestUtil.Companion.childAtPosition
import net.mythrowaway.app.module.trash.presentation.view.calendar.CalendarActivity
import net.mythrowaway.app.lib.AndroidTestHelper.Companion.waitUntilDisplayed
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest5 {

    @get:Rule
    val composeRule = createAndroidComposeRule(CalendarActivity::class.java)

    private fun openDrawer() {
        composeRule.waitUntil {
            composeRule.activity.findViewById<View>(R.id.calendarActivityRoot) != null
        }
        onView(withId(R.id.calendarActivityRoot)).perform(DrawerActions.open())
    }

    private val resource = InstrumentationRegistry.getInstrumentation().targetContext.resources
    @Before
    fun setUp(){
    }

    @After
    fun tearDown (){
    }

    /*
    複数のゴミを登録するシナリオ
    - 4つ以上のゴミを登録した場合に最初の3つが日付セルに表示されること
    - 4つ以上のゴミがある場合は日付せるの4行目が「...+1」になること
     */
    @Test
    fun add_four_trashes_and_calendar_shows_omitted_text() {
        // 1つ目: もえるゴミの登録
        openDrawer()
        onView(withId(R.id.menuItemAdd)).perform(click())

        composeRule.waitUntil {
            composeRule.onNodeWithText("もえるゴミ").isDisplayed()
        }

        composeRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
        composeRule.waitUntil {
            composeRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBack()

        // 2つ目: その他（テスト）の登録
        openDrawer()
        onView(withId(R.id.menuItemAdd)).perform(click())
        composeRule.waitUntil {
            composeRule.onNodeWithText("もえるゴミ").isDisplayed()
        }
        composeRule.onNodeWithText("もえるゴミ").performClick()
        composeRule.waitUntil {
            composeRule.onNodeWithText("自分で入力").isDisplayed()
        }
        composeRule.onNodeWithText("自分で入力").performClick()
        composeRule.waitUntil {
            composeRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).isDisplayed()
        }
        composeRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextInput("テスト")
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
        composeRule.waitUntil {
            composeRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }
        Espresso.pressBack()

        // 3つ目: もえないゴミの登録
        openDrawer()
        onView(withId(R.id.menuItemAdd)).perform(click())

        composeRule.waitUntil {
            composeRule.onNodeWithText("もえるゴミ").isDisplayed()
        }
        composeRule.onNodeWithText("もえるゴミ").performClick()
        composeRule.waitUntil {
            composeRule.onNodeWithText("もえないゴミ").isDisplayed()
        }
        composeRule.onNodeWithText("もえないゴミ").performClick()
        composeRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
        composeRule.waitUntil {
            composeRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBack()

        // 4つ目: プラスチックの登録
        openDrawer()
        onView(withId(R.id.menuItemAdd)).perform(click())

        composeRule.waitUntil {
            composeRule.onNodeWithText("もえるゴミ").isDisplayed()
        }
        composeRule.onNodeWithText("もえるゴミ").performClick()
        composeRule.waitUntil {
            composeRule.onNodeWithText("プラスチック").isDisplayed()
        }
        composeRule.onNodeWithText("プラスチック").performClick()
        composeRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
        composeRule.waitUntil {
            composeRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBack()

        waitUntilDisplayed("プラスチック", 5000)

        val trashTextLinearLayout = allOf(
                withId(R.id.trashTextListLayout),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout),
                        childAtPosition(
                            withId(R.id.calendar),
                            7
                        )
                    ),
                    1
                ),
            )
        val firstTrashTextOnSunday = onView(
            allOf(
                withId(R.id.trashText),
                childAtPosition(
                    childAtPosition(
                        trashTextLinearLayout,
                        0
                    ),
            0
                ),
                isDisplayed()
            ),
        )
        firstTrashTextOnSunday.check(matches(withText("もえるゴミ")))

        val secondTrashTextOnSunday = onView(
            allOf(
                withId(R.id.trashText),
                childAtPosition(
                    childAtPosition(
                        trashTextLinearLayout,
                    1
                    ),
                    0
                ),
                isDisplayed()
            ),
        )
        secondTrashTextOnSunday.check(matches(withText("テスト")))

        val thirdTrashTextOnSunday = onView(
            allOf(
                withId(R.id.trashText),
                childAtPosition(
                    childAtPosition(
                        trashTextLinearLayout,
                        2
                    ),
                    0
                ),
                isDisplayed()
            ),
        )
        thirdTrashTextOnSunday.check(matches(withText("もえないゴミ")))


        val forthTrashTextOnSunday = onView(
            allOf(
                childAtPosition(
                    trashTextLinearLayout,
                    3
                ),
                isDisplayed()
            ),
        )
        forthTrashTextOnSunday.check(matches(withText("...+ 1")))

        // ダイアログを開く
        firstTrashTextOnSunday.perform(click())
        val dialogTrashText = onView(
            allOf(
                withId(android.R.id.message),
                withParent(
                    withParent(
                        IsInstanceOf.instanceOf(
                            ScrollView::class.java
                        )
                    )
                ),
                isDisplayed()
            )
        )
        dialogTrashText.check(matches(withText("もえるゴミ\nテスト\nもえないゴミ\nプラスチック")))

        Espresso.pressBack()
    }
}

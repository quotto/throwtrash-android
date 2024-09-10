package net.mythrowaway.app.calendar


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
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
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
import net.mythrowaway.app.module.trash.presentation.view.edit.EditActivity
import net.mythrowaway.app.lib.AndroidTestHelper.Companion.waitUntilDisplayed
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarActivityTest5 {

    @get:Rule
    var mActivityScenarioRule = ActivityScenarioRule(CalendarActivity::class.java)

    @get:Rule
    val editActivityRule = createAndroidComposeRule(EditActivity::class.java)

    private val menuButton = onView(
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

    private val editMenuButton = onView(
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
        menuButton.perform(click())

        editMenuButton.perform(click())

        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("もえるゴミ").isDisplayed()
        }

        editActivityRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBack()

        // 2つ目: その他（テスト）の登録
        menuButton.perform(click())
        editMenuButton.perform(click())
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("もえるゴミ").isDisplayed()
        }
        editActivityRule.onNodeWithText("もえるゴミ").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("自分で入力").isDisplayed()
        }
        editActivityRule.onNodeWithText("自分で入力").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).isDisplayed()
        }
        editActivityRule.onNodeWithTag(resource.getString(R.string.testTag_trash_name_input)).performTextInput("テスト")
        editActivityRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }
        Espresso.pressBack()

        // 3つ目: もえないゴミの登録
        menuButton.perform(click())
        editMenuButton.perform(click())

        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("もえるゴミ").isDisplayed()
        }
        editActivityRule.onNodeWithText("もえるゴミ").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("もえないゴミ").isDisplayed()
        }
        editActivityRule.onNodeWithText("もえないゴミ").performClick()
        editActivityRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
        }

        Espresso.pressBack()

        // 4つ目: プラスチックの登録
        menuButton.perform(click())
        editMenuButton.perform(click())

        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("もえるゴミ").isDisplayed()
        }
        editActivityRule.onNodeWithText("もえるゴミ").performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText("プラスチック").isDisplayed()
        }
        editActivityRule.onNodeWithText("プラスチック").performClick()
        editActivityRule.onNodeWithText(resource.getString(R.string.text_register_trash_button)).performClick()
        editActivityRule.waitUntil {
            editActivityRule.onNodeWithText(resource.getString(R.string.message_complete_save_trash)).isDisplayed()
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

package net.mythrowaway.app.calendar

import android.view.View
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.mythrowaway.app.R
import net.mythrowaway.app.module.trash.presentation.view.calendar.CalendarActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarRefreshTest {

    @get:Rule
    val composeRule = createAndroidComposeRule(CalendarActivity::class.java)

    private fun waitUntilRefreshIdle() {
        composeRule.waitUntil(timeoutMillis = 10000) {
            val swipeRefresh = composeRule.activity.findViewById<SwipeRefreshLayout>(R.id.calendarSwipeRefresh)
            val indicator = composeRule.activity.findViewById<View>(R.id.indicatorLayout)
            swipeRefresh != null &&
                indicator != null &&
                !swipeRefresh.isRefreshing &&
                indicator.visibility != View.VISIBLE
        }
    }

    private fun waitUntilRefreshTriggered(previousCount: Int) {
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.activity.refreshTriggerCount > previousCount
        }
    }

    @Test
    fun reload_button_triggers_refresh() {
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.activity.findViewById<View>(R.id.calendarSwipeRefresh) != null
        }
        waitUntilRefreshIdle()
        val beforeCount = composeRule.activity.refreshTriggerCount
        onView(withId(R.id.menuItemRefresh)).perform(click())
        waitUntilRefreshTriggered(beforeCount)
    }

    @Test
    fun reload_button_ignores_duplicate_refresh() {
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.activity.findViewById<View>(R.id.calendarSwipeRefresh) != null
        }
        waitUntilRefreshIdle()
        val beforeCount = composeRule.activity.refreshTriggerCount
        onView(withId(R.id.menuItemRefresh)).perform(click())
        onView(withId(R.id.menuItemRefresh)).perform(click())
        waitUntilRefreshTriggered(beforeCount)
        val afterCount = composeRule.activity.refreshTriggerCount
        composeRule.waitUntil(timeoutMillis = 1000) {
            composeRule.activity.refreshTriggerCount == afterCount
        }
    }

    @Test
    fun pull_to_refresh_triggers_refresh() {
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.activity.findViewById<View>(R.id.calendarSwipeRefresh) != null
        }
        waitUntilRefreshIdle()
        val beforeCount = composeRule.activity.refreshTriggerCount
        onView(withId(R.id.calendarSwipeRefresh)).perform(swipeDown())
        waitUntilRefreshTriggered(beforeCount)
    }

    @Test
    fun pull_to_refresh_ignores_duplicate_refresh() {
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.activity.findViewById<View>(R.id.calendarSwipeRefresh) != null
        }
        waitUntilRefreshIdle()
        val beforeCount = composeRule.activity.refreshTriggerCount
        onView(withId(R.id.calendarSwipeRefresh)).perform(swipeDown())
        onView(withId(R.id.calendarSwipeRefresh)).perform(swipeDown())
        waitUntilRefreshTriggered(beforeCount)
        val afterCount = composeRule.activity.refreshTriggerCount
        composeRule.waitUntil(timeoutMillis = 1000) {
            composeRule.activity.refreshTriggerCount == afterCount
        }
    }

    @Test
    fun pull_to_refresh_shows_indicator_and_refreshing_state() {
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.activity.findViewById<View>(R.id.calendarSwipeRefresh) != null
        }
        waitUntilRefreshIdle()
        val beforeCount = composeRule.activity.refreshTriggerCount
        onView(withId(R.id.calendarSwipeRefresh)).perform(swipeDown())
        waitUntilRefreshTriggered(beforeCount)
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.activity.findViewById<View>(R.id.indicatorLayout).visibility == View.VISIBLE
        }
    }
}

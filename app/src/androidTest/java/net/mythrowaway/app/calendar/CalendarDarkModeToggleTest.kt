package net.mythrowaway.app.calendar

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.shape.MaterialShapeDrawable
import net.mythrowaway.app.R
import net.mythrowaway.app.lib.AndroidTestHelper.Companion.dismissScheduleSearchStartupDialogIfDisplayed
import net.mythrowaway.app.module.theme.infra.PreferenceThemeRepositoryImpl
import net.mythrowaway.app.module.trash.presentation.view.calendar.CalendarActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.hamcrest.Matchers.`is`

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarDarkModeToggleTest {
  private lateinit var themeRepository: PreferenceThemeRepositoryImpl
  private lateinit var context: android.content.Context

  @Before
  fun setUp() {
    context = InstrumentationRegistry.getInstrumentation().targetContext
    themeRepository = PreferenceThemeRepositoryImpl(context)
  }

  @Test
  fun toggleDarkMode_FromDrawer() {
    themeRepository.saveDarkModeEnabled(false)
    val scenario = ActivityScenario.launch(CalendarActivity::class.java)
    dismissScheduleSearchStartupDialogIfDisplayed()

    onView(withId(R.id.calendarActivityRoot)).perform(DrawerActions.open())
    onView(withId(R.id.main_nav_view)).check(matches(isDisplayed()))
    onView(withId(R.id.darkModeSwitch)).check(matches(isDisplayed()))
    onView(withId(R.id.darkModeSwitch)).perform(click())
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()

    assertTrue(themeRepository.isDarkModeEnabled())
    scenario.close()
  }

  @Test
  fun toggleDarkMode_DisableFromEnabled() {
    themeRepository.saveDarkModeEnabled(true)
    val scenario = ActivityScenario.launch(CalendarActivity::class.java)
    dismissScheduleSearchStartupDialogIfDisplayed()

    onView(withId(R.id.calendarActivityRoot)).perform(DrawerActions.open())
    onView(withId(R.id.main_nav_view)).check(matches(isDisplayed()))
    onView(withId(R.id.darkModeSwitch)).perform(click())
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()

    assertFalse(themeRepository.isDarkModeEnabled())
    scenario.close()
  }

  @Test
  fun darkModeContainer_matchesNavigationMenuBackgroundColor() {
    themeRepository.saveDarkModeEnabled(false)
    val scenario = ActivityScenario.launch(CalendarActivity::class.java)
    dismissScheduleSearchStartupDialogIfDisplayed()

    scenario.onActivity { activity ->
      val navigationBackground = activity.findViewById<android.view.View>(R.id.main_nav_view).background
      val darkModeBackground = activity.findViewById<android.view.View>(R.id.darkModeContainer).background

      assertThat(extractBackgroundColor(darkModeBackground), `is`(extractBackgroundColor(navigationBackground)))
    }

    scenario.close()
  }

  @Test
  fun darkModeContainer_matchesNavigationMenuBackgroundColor_inDarkMode() {
    themeRepository.saveDarkModeEnabled(true)
    val scenario = ActivityScenario.launch(CalendarActivity::class.java)
    dismissScheduleSearchStartupDialogIfDisplayed()

    scenario.onActivity { activity ->
      val navigationBackground = activity.findViewById<android.view.View>(R.id.main_nav_view).background
      val darkModeBackground = activity.findViewById<android.view.View>(R.id.darkModeContainer).background

      assertThat(extractBackgroundColor(darkModeBackground), `is`(extractBackgroundColor(navigationBackground)))
    }

    scenario.close()
  }

  private fun extractBackgroundColor(drawable: android.graphics.drawable.Drawable?): Int {
    return when (drawable) {
      is android.graphics.drawable.ColorDrawable -> drawable.color
      is MaterialShapeDrawable -> drawable.fillColor?.defaultColor
      else -> null
    } ?: error("背景色を取得できませんでした: ${drawable?.javaClass?.name}")
  }
}

package net.mythrowaway.app

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

class AndroidTestUtil {
    companion object {
        fun getTextViewOfCalendarCell(calendarPosition: Int, rowNumberOfTrashText: Int) : ViewInteraction {
            return Espresso.onView(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.trashText),
                    childAtPosition(
                        childAtPosition(
                            childAtPosition(
                                Matchers.allOf(
                                    ViewMatchers.withId(R.id.linearLayout),
                                    childAtPosition(
                                        ViewMatchers.withId(R.id.calendar),
                                        calendarPosition
                                    )
                                ),
                                1
                            ),
                            rowNumberOfTrashText
                        ),
                        0
                    ),
                    ViewMatchers.isDisplayed()
                )
            )
        }
        fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int
        ): Matcher<View> {

            return object : TypeSafeMatcher<View>() {
                override fun describeTo(description: Description) {
                    description.appendText("Child at position " + position + " in parent ")
                    parentMatcher.describeTo(description)
                }

                override fun matchesSafely(view: View): Boolean {
                    val parent: ViewParent = view.getParent()
                    return parent is ViewGroup && parentMatcher.matches(parent)
                            && view.equals((parent).getChildAt(position))
                }
            }
        }

        fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?>? {
            return object : TypeSafeMatcher<View?>() {
                var currentIndex = 0
                override fun describeTo(description: Description) {
                    description.appendText("with index: ")
                    description.appendValue(index)
                    matcher.describeTo(description)
                }

                override fun matchesSafely(view: View?): Boolean {
                    return matcher.matches(view) && currentIndex++ == index
                }
            }
        }

        fun getText(matcher: ViewInteraction): String {
            var text = String()
            matcher.perform(object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return ViewMatchers.isAssignableFrom(TextView::class.java)
                }

                override fun getDescription(): String {
                    return "Text of the view"
                }

                override fun perform(uiController: UiController, view: View) {
                    val tv = view as TextView
                    text = tv.text.toString()
                }
            })

            return text
        }

    }
}
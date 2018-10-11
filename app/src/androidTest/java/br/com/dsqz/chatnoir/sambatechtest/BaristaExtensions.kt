package br.com.dsqz.chatnoir.sambatechtest

import android.content.res.Resources
import android.support.annotation.IdRes
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withTagKey
import android.support.v7.widget.RecyclerView
import android.view.View
import br.com.dsqz.chatnoir.sambatechtest.EspressoUtil.withIndex
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions
import com.schibsted.spain.barista.interaction.BaristaClickInteractions
import com.schibsted.spain.barista.interaction.BaristaListInteractions
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import com.schibsted.spain.barista.internal.matcher.DrawableMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.internal.matchers.TypeSafeMatcher

/**
 * Created by diogosq on 10/11/18.
 */
fun BaristaRecyclerViewAssertions.itemAtPositionAssertAnyDrawable(@IdRes listViewId: Int, itemPosition: Int) {
    BaristaListInteractions.scrollListToPosition(listViewId, itemPosition)

    BaristaSleepInteractions.sleep(2000)

    Espresso.onView(EspressoUtil.withRecyclerView(listViewId)
            .atPosition(itemPosition))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(Matchers.allOf<View>(DrawableMatcher.withAnyDrawable()))))

}

/**
 * BaristaClickInteractions extensions
 */
fun BaristaClickInteractions.clickOn(tagKey: Int, tagValue: String, index: Int = 0) {
    BaristaSleepInteractions.sleep(1000)
    Espresso.onView(withIndex(withTagKey(tagKey, Matchers.`is`<Any>(tagValue)), index)).perform(click())
    BaristaSleepInteractions.sleep(1000)
}


object EspressoUtil {

    fun withRecyclerView(recyclerViewId: Int): EssentialRecyclerViewMatcher {

        return EssentialRecyclerViewMatcher(recyclerViewId)
    }

    fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var currentIndex = 0

            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }

}

class EssentialRecyclerViewMatcher(private val recyclerViewId: Int) {

    fun atPosition(position: Int): Matcher<View> {
        return atPositionOnView(position, -1)
    }

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            var resources: Resources? = null
            var childView: View? = null

            override fun describeTo(description: org.hamcrest.Description) {
                var idDescription = Integer.toString(recyclerViewId)
                if (this.resources != null) {
                    try {
                        idDescription = this.resources!!.getResourceName(recyclerViewId)
                    } catch (var4: Resources.NotFoundException) {
                        idDescription = String.format("%s (resource name not found)",
                                Integer.valueOf(recyclerViewId))
                    }

                }

                description.appendText("with id: $idDescription")
            }

            override fun matchesSafely(view: View): Boolean {

                this.resources = view.resources

                if (childView == null) {
                    val recyclerView = view.rootView.findViewById<RecyclerView>(recyclerViewId)
                    if (recyclerView != null && recyclerView.id == recyclerViewId) {
                        childView = recyclerView.findViewHolderForAdapterPosition(position).itemView
                    } else {
                        return false
                    }
                }

                if (targetViewId == -1) {
                    return view === childView
                } else {
                    val targetView = childView!!.findViewById<View>(targetViewId)
                    return view === targetView
                }

            }
        }
    }
}
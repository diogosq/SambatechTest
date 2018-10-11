package br.com.dsqz.chatnoir.sambatechtest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.annotation.IdRes
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.core.internal.deps.guava.collect.Iterables
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import com.schibsted.spain.barista.rule.BaristaRule
import com.schibsted.spain.barista.rule.cleardata.ClearDatabaseRule
import com.schibsted.spain.barista.rule.cleardata.ClearFilesRule
import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule
import com.schibsted.spain.barista.rule.flaky.FlakyTestRule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@Suppress("unused")
class PersonalBaristaRule<T : Activity> private constructor(activityClass: Class<T>) : TestRule {


    companion object {
        private const val DEFAULT_FLAKY_ATTEMPTS = 1
        private const val LAUNCH_ACTIVITY_AUTOMATICALLY = false
        private const val INITIAL_TOUCH_MODE_ENABLED = true

        inline fun <reified T : Activity> create(): BaristaRule<T> = BaristaRule.create(T::class.java)

        @JvmStatic
        fun <T : Activity> create(activityClass: Class<T>): PersonalBaristaRule<T> {
            return PersonalBaristaRule(activityClass)
        }
    }

    private val clearPreferencesRule: ClearPreferencesRule = ClearPreferencesRule()
    private val clearDatabaseRule: ClearDatabaseRule = ClearDatabaseRule()
    private val clearFilesRule: ClearFilesRule = ClearFilesRule()
    private val flakyTestRule: FlakyTestRule = FlakyTestRule().apply {
        allowFlakyAttemptsByDefault(DEFAULT_FLAKY_ATTEMPTS)
    }
    private val activityTestRule: ActivityTestRule<T> = ActivityTestRule(activityClass,
            INITIAL_TOUCH_MODE_ENABLED,
            LAUNCH_ACTIVITY_AUTOMATICALLY)

    override fun apply(base: Statement, description: Description): Statement {
        return RuleChain.outerRule(flakyTestRule)
                // ↓ All rules below flakyTestRule will be repeated
                .around(activityTestRule)
                // ↓ All rules below activityTestRule will execute before launching the activity
                .around(clearPreferencesRule)
                .around(clearDatabaseRule)
                .around(clearFilesRule)
                .apply(base, description)
    }

    fun launchActivity() {
        activityTestRule.launchActivity(null)
    }

    fun launchActivity(startIntent: Intent) {
        activityTestRule.launchActivity(startIntent)
    }

    fun getContext(): Context =
            InstrumentationRegistry.getTargetContext()

    private fun getCurrentActivity(): Activity? {
        return getCurrentActivity(activityTestRule)
    }

    private fun getCurrentActivity(rule: ActivityTestRule<*>): Activity? {
        getInstrumentation().waitForIdleSync()
        val activity = arrayOfNulls<Activity>(1)

        rule.runOnUiThread {
            val activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            activity[0] = Iterables.getOnlyElement(activities)
        }
        return activity[0]
    }

    fun getString(@IdRes stringIdRes: Int): String =
            getCurrentActivity()!!.resources.getString(stringIdRes)
}
package com.abnamro.apps.referenceandroid.support

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.abnamro.apps.referenceandroid.MainActivity
import com.abnamro.apps.referenceandroid.screens.MainScreen
import org.junit.Rule

/**
 * Base for UI tests: keeps setup out of test bodies. Fresh [MainActivity] per test via
 * [ActivityScenarioRule] + a ready [MainScreen]. Permission/prefs/mock-server setup would live here.
 */
abstract class BaseUiTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    protected val mainScreen = MainScreen()
}

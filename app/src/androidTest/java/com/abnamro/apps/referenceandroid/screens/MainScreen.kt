package com.abnamro.apps.referenceandroid.screens

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.abnamro.apps.referenceandroid.R
import com.abnamro.apps.referenceandroid.support.waitForView
import com.abnamro.apps.referenceandroid.support.waitUntilGone
import org.hamcrest.CoreMatchers.allOf

/** Page object for the single main screen: centralized [Locators], fluent (returns `this`), tests express intent not Espresso. */
class MainScreen {

    /** Centralized locators (ids + user-visible text). */
    object Locators {
        const val GREETING = "Hello World!"
        const val MESSAGE = "Replace with your own action"
        const val ACTION = "Action"

        val toolbar = withId(R.id.toolbar)
        val fab = withId(R.id.fab)
        val greeting = withText(GREETING)
        val snackbarMessage = withText(MESSAGE)
        val snackbarAction = withText(ACTION)
        val settingsItem = withText(R.string.action_settings)
        val appTitle = withText(R.string.app_name)
    }

    fun assertCoreScreenDisplayed(): MainScreen {
        onView(Locators.toolbar).check(matches(isDisplayed()))
        onView(Locators.greeting).check(matches(isDisplayed()))
        onView(Locators.fab).check(matches(allOf(isDisplayed(), isClickable())))
        return this
    }

    fun assertGreetingDisplayed(): MainScreen {
        onView(Locators.greeting).check(matches(isDisplayed()))
        return this
    }

    fun tapFab(): MainScreen {
        onView(Locators.fab).perform(click())
        return this
    }

    fun assertSnackbarMessageDisplayed(): MainScreen {
        // Snackbar is transient/animated: wait explicitly, then assert.
        waitForView(Locators.snackbarMessage)
        onView(Locators.snackbarMessage)
            .check(matches(isDisplayed()))
        return this
    }

    /** No action button: MainActivity's `setAction("Action", null)` (null listener) renders none. Encodes the defect. */
    fun assertSnackbarActionNotDisplayed(): MainScreen {
        onView(Locators.snackbarAction)
            .check(doesNotExist())
        return this
    }

    /** Isolation cleanup: wait out the process-wide SnackbarManager LENGTH_LONG Snackbar so the next test starts clean. */
    fun waitForSnackbarDismissed(): MainScreen {
        waitUntilGone(Locators.snackbarMessage)
        return this
    }

    /** Toolbar title = app name (activity label). */
    fun assertAppTitleDisplayed(): MainScreen {
        onView(Locators.appTitle)
            .check(matches(isDisplayed()))
        return this
    }

    /** Opens the toolbar overflow menu (holds "Settings"). */
    fun openOptionsMenu(): MainScreen {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        return this
    }

    fun assertSettingsMenuItemDisplayed(): MainScreen {
        // Overflow items live in a PopupWindow: match in the platform popup root.
        waitForView(Locators.settingsItem, isPlatformPopup())
        onView(Locators.settingsItem).inRoot(isPlatformPopup()).check(matches(isDisplayed()))
        return this
    }

    /** Taps "Settings" — currently a no-op (MainActivity consumes action_settings: no nav/dialog/toast), so callers can only assert the main screen remains. */
    fun tapSettings(): MainScreen {
        waitForView(Locators.settingsItem, isPlatformPopup())
        onView(Locators.settingsItem).inRoot(isPlatformPopup())
            .perform(click())
        return this
    }
}

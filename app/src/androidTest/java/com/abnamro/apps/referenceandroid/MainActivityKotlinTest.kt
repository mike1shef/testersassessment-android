package com.abnamro.apps.referenceandroid

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abnamro.apps.referenceandroid.support.BaseUiTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Kotlin UI tests (page-object/robot pattern): setup in [BaseUiTest], fluent calls, one behavior per
 * test, built-in sync + one targeted Snackbar wait (no sleeps). Requires API <= 33 (Espresso 3.2.0
 * event injection removed in API 34).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityKotlinTest : BaseUiTest() {

    /** Launch: greeting, toolbar, actionable FAB. */
    @Test
    fun launch_displaysCoreScreen() {
        mainScreen.assertCoreScreenDisplayed()
    }

    /** Primary action: FAB tap shows the Snackbar message. */
    @Test
    fun fab_displaysSnackbarMessage() {
        mainScreen
            .tapFab()
            .assertSnackbarMessageDisplayed()
            .waitForSnackbarDismissed()
    }

    /** Lifecycle: screen survives activity recreation. */
    @Test
    fun recreatingActivity_keepsCoreScreenUsable() {
        activityRule.scenario.recreate()
        mainScreen.assertCoreScreenDisplayed()
    }

    /** Overflow menu shows "Settings". */
    @Test
    fun openingOverflowMenu_displaysSettings() {
        mainScreen
            .openOptionsMenu()
            .assertSettingsMenuItemDisplayed()
    }

    /** Characterizes the Settings no-op (consumed, no nav/dialog/toast): main screen remains. */
    @Test
    fun tappingSettings_isNoOp_mainScreenRemains() {
        mainScreen
            .openOptionsMenu()
            .tapSettings()
        mainScreen.assertCoreScreenDisplayed()
    }

    /** Toolbar shows the app name. */
    @Test
    fun launch_showsAppTitleInToolbar() {
        mainScreen.assertAppTitleDisplayed()
    }

    /** Defect: Snackbar shows its message but no action button (null listener, `setAction("Action", null)`). */
    @Test
    fun tappingFab_snackbarHasNoActionButton() {
        mainScreen
            .tapFab()
            .assertSnackbarMessageDisplayed()
            .assertSnackbarActionNotDisplayed()
            .waitForSnackbarDismissed()
    }
}

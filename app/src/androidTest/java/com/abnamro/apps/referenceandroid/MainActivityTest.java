package com.abnamro.apps.referenceandroid;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.abnamro.apps.referenceandroid.screens.MainScreen;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

/**
 * Java UI tests parallel to {@link MainActivityKotlinTest}: same page-object pattern, fluent calls,
 * one behavior per test, built-in sync + one targeted Snackbar wait (no sleeps). Requires API
 * &lt;= 33 (Espresso 3.2.0 event injection removed in API 34).
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private final MainScreen mainScreen = new MainScreen();

    /** Launch: greeting, toolbar, actionable FAB. */
    @Test
    public void launch_displaysCoreScreen() {
        mainScreen.assertCoreScreenDisplayed();
    }

    /** Primary action: FAB tap shows the Snackbar message. */
    @Test
    public void tappingFab_displaysSnackbarMessage() {
        mainScreen
                .tapFab()
                .assertSnackbarMessageDisplayed()
                .waitForSnackbarDismissed();
    }

    /** Lifecycle: screen survives activity recreation. */
    @Test
    public void recreatingActivity_keepsCoreScreenUsable() {
        activityRule.getScenario().recreate();
        mainScreen.assertCoreScreenDisplayed();
    }

    /** Overflow menu shows "Settings". */
    @Test
    public void openingOverflowMenu_displaysSettings() {
        mainScreen
                .openOptionsMenu()
                .assertSettingsMenuItemDisplayed();
    }

    /** Characterizes the Settings no-op (consumed, no nav/dialog/toast): main screen remains. */
    @Test
    public void tappingSettings_mainScreenRemains() {
        mainScreen
                .openOptionsMenu()
                .tapSettings();
        mainScreen.assertCoreScreenDisplayed();
    }

    /** Toolbar shows the app name. */
    @Test
    public void showsAppTitleInToolbar() {
        mainScreen.assertAppTitleDisplayed();
    }

    /** Defect: Snackbar shows its message but no action button (null listener, {@code setAction("Action", null)}). */
    @Test
    public void snackbarHasNoActionButton() {
        mainScreen
                .tapFab()
                .assertSnackbarMessageDisplayed()
                .assertSnackbarActionNotDisplayed()
                .waitForSnackbarDismissed();
    }
}

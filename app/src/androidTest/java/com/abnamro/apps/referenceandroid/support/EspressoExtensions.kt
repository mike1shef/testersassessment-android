package com.abnamro.apps.referenceandroid.support

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.Root
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher

/**
 * Espresso auto-syncs with the main thread; these cover transient/animated views it doesn't track:
 *  - [waitForView]: targeted wait for a view to appear (optionally in a given window/root).
 *  - [waitUntilGone]: best-effort wait for a view to disappear (test-isolation cleanup).
 */

const val DEFAULT_TIMEOUT_MS = 5_000L
private const val RETRY_INTERVAL_MS = 100L

/** Polls until [viewMatcher] is displayed (optionally inside [windowMatcher], e.g. a popup), else fails after [timeoutMs]. */
fun waitForView(
    viewMatcher: Matcher<View>,
    windowMatcher: Matcher<Root>? = null,
    timeoutMs: Long = DEFAULT_TIMEOUT_MS
) {
    val endTime = System.currentTimeMillis() + timeoutMs
    var lastError: Throwable? = null
    while (System.currentTimeMillis() < endTime) {
        try {
            var interaction = onView(viewMatcher)
            if (windowMatcher != null) interaction = interaction.inRoot(windowMatcher)
            interaction.check(matches(isDisplayed()))
            return
        } catch (e: NoMatchingViewException) {
            lastError = e
        } catch (e: AssertionError) {
            lastError = e
        }
        Thread.sleep(RETRY_INTERVAL_MS)
    }
    throw AssertionError("View not displayed within ${timeoutMs}ms: $viewMatcher", lastError)
}

/** Best-effort wait until [viewMatcher] is gone (e.g. LENGTH_LONG Snackbar via process-wide SnackbarManager) so the next test starts clean; never fails. */
fun waitUntilGone(viewMatcher: Matcher<View>, timeoutMs: Long = DEFAULT_TIMEOUT_MS) {
    val endTime = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < endTime) {
        val present = try {
            onView(viewMatcher).check(matches(isDisplayed()))
            true
        } catch (e: NoMatchingViewException) {
            false
        } catch (e: AssertionError) {
            false
        }
        if (!present) return
        Thread.sleep(RETRY_INTERVAL_MS)
    }
}

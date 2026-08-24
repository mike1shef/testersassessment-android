# ReferenceAndroid — test automation

This repository contains my solution to the ABN AMRO mobile test assignment for the Android
reference app. The app is a single-screen starter project: it shows a greeting ("Hello World!"),
a toolbar, an overflow menu with a "Settings" item, and a floating action button that displays a
Snackbar. The tests are written with Espresso, the native Android UI-testing framework.

## Approach

The app has no business logic. Everything it does is standard Android framework wiring — inflating a
layout, showing a Snackbar, handling a menu item, surviving recreation. 
I focused on Espresso UI tests that verify the app's real, observable behaviour, and I kept the suite small and risk-based instead of chasing a
coverage percentage. (The iOS app in the sibling repository does have real logic — currency
formatting — so there I added XCTest unit tests as well.)

## Test results

All 15 instrumented tests pass — 15 passed, 0 failed, 0 errors — verified on 2026-08-24 on an
Android 13 / API 33 emulator using JDK 8:

```
./gradlew :app:connectedDebugAndroidTest
# Starting 15 tests on Compatible(AVD) - 13
# BUILD SUCCESSFUL
```

The 15 tests are 7 behaviours written in Kotlin, the same 7 behaviours mirrored in Java (the
assignment explicitly accepts Java), and 1 accessibility test.

## What the tests cover

The behavioural tests (present in both Kotlin and Java) check that:

- The screen launches with the greeting, the toolbar, and a clickable floating action button.
- The toolbar shows the app name.
- Tapping the button shows a Snackbar with the message "Replace with your own action".
- The Snackbar shows no action button. The app calls `setAction("Action", null)`, so the action is
  never rendered; the test records this as a known defect.
- Opening the overflow menu shows the "Settings" item.
- Tapping "Settings" does nothing — it is consumed with no navigation, dialog, or toast — and the
  main screen stays visible.
- The screen still works after an activity recreation.

The accessibility test records that the floating action button has no `contentDescription`, so it
exposes no spoken label to screen readers.

## How the tests are organised

- `app/src/androidTest/.../MainActivityKotlinTest.kt` — the Kotlin UI tests.
- `app/src/androidTest/.../MainActivityTest.java` — the same behaviours in Java.
- `app/src/androidTest/.../AccessibilityTest.kt` — the accessibility check.
- `app/src/androidTest/.../screens/MainScreen.kt` — a page object that holds all the locators and
  actions, so the tests read like plain steps.
- `app/src/androidTest/.../support/BaseUiTest.kt` — shared setup that gives every test a fresh
  `ActivityScenarioRule`.
- `app/src/androidTest/.../support/EspressoExtensions.kt` — small wait helpers for the transient
  Snackbar and the overflow popup.

The tests prefer resource IDs and fall back to visible text, rely on Espresso's built-in
synchronisation rather than fixed sleeps, and start each test from a fresh activity so they are
independent and can run in any order.

## Changes I made to the template

- Added the Espresso test suite described above.
- Enabled `testOptions { animationsDisabled true }` in `app/build.gradle`. This is required: with animations on, opening the overflow popup prevents Espresso from reaching an idle state and the menu tests stall.
- Updated the root `build.gradle`: removed the Detekt and SonarQube plugins and replaced `jcenter()`
  (shut down in 2021) with `mavenCentral()` so the project builds. The original file is kept as `build.gradle.original` for reference.
- Added a `.gitignore` for build output, IDE files, and `local.properties`.
- The application code, resources, and `Jenkinsfile` are unchanged.

## Notes and observations

- The floating action button has no `contentDescription` (`activity_main.xml`).
- The Snackbar action uses a null listener, so it never appears (`MainActivity.kt`).
- The "Settings" menu item is a no-op (`MainActivity.kt`).
- The manifest sets `allowBackup="true"`.
- The toolchain is old: Gradle 6.1.1, Android Gradle Plugin 4.0.1, Kotlin 1.3.72.

## Out of scope

- The Jenkins pipeline only runs `./gradlew testDebug` (local JVM tests); it does not run the Espresso suite. Running the UI tests in CI would need a `connectedDebugAndroidTest` stage against a pinned emulator, which is a pipeline change beyond this assignment.
- Non-functional testing (startup and scrolling performance, security, release monitoring) is not
  meaningful for this stub, but would matter for a real application.

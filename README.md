# ReferenceAndroid — test automation

This repository contains my solution to the ABN AMRO mobile test assignment for the Android
reference app. The app is a single-screen starter project: it shows a greeting ("Hello World!"),
a toolbar, an overflow menu with a "Settings" item, and a floating action button that displays a
Snackbar. The tests are written with Espresso, the native Android UI-testing framework.

## Approach

The app has no business logic. Everything it does is standard Android framework wiring — inflating a
layout, showing a Snackbar, handling a menu item, surviving recreation. There is nothing to compute
or transform, so there is no meaningful layer to cover with local JVM unit tests; a unit test here
would only re-assert framework behaviour. I therefore concentrated on Espresso UI tests that verify
the app's real, observable behaviour, and I kept the suite small and risk-based instead of chasing a
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

## Running the tests

You will need:

- JDK 8. The project uses Gradle 6.1.1 and Android Gradle Plugin 4.0.1, which require Java 8.
- An emulator or device on API 33 or lower. Espresso 3.2.0 injects touch events through an API that
  Android 14 (API 34) removed, so the tests must run on API 33 or below.
- A `local.properties` file pointing at your Android SDK. This file is deliberately not committed, so
  create it locally (for example `sdk.dir=/Users/you/Library/Android/sdk`).

Then run:

```
./gradlew :app:connectedDebugAndroidTest
```

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
exposes no spoken label to screen readers. Fixing this requires a change to the app itself, which is
outside the scope of a tests-only submission.

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
- Enabled `testOptions { animationsDisabled true }` in `app/build.gradle`. This is required: with
  animations on, opening the overflow popup prevents Espresso from reaching an idle state and the
  menu tests stall.
- Updated the root `build.gradle`: removed the Detekt and SonarQube plugins and replaced `jcenter()`
  (shut down in 2021) with `mavenCentral()` so the project builds. The original file is kept as
  `build.gradle.original` for reference.
- Added a `.gitignore` for build output, IDE files, and `local.properties`.
- The application code, resources, and `Jenkinsfile` are unchanged.

## Notes and observations

- The floating action button has no `contentDescription` (`activity_main.xml`).
- The Snackbar action uses a null listener, so it never appears (`MainActivity.kt`).
- The "Settings" menu item is a no-op (`MainActivity.kt`).
- The manifest sets `allowBackup="true"`.
- The toolchain is old: Gradle 6.1.1, Android Gradle Plugin 4.0.1, Kotlin 1.3.72.

## Out of scope and possible next steps

- The Jenkins pipeline only runs `./gradlew testDebug` (local JVM tests); it does not run the Espresso
  suite. Running the UI tests in CI would need a `connectedDebugAndroidTest` stage against a pinned
  emulator, which is a pipeline change beyond this assignment.
- The app has no network layer or API and does not request the INTERNET permission, so API tools such
  as Postman, WireMock, or Charles would have nothing to exercise here.
- Non-functional testing (startup and scrolling performance, security, release monitoring) is not
  meaningful for this stub, but would matter for a real application.

---

## Original assignment brief

We are looking for Automation Engineers that have the mindset "only the sky is the limit" and
"automation doesn't stop at testing, it's just a beginning!" ;)

The purpose of this test assignment is to assess the applicant's automation skills, allowing him/her
to show the best they can do and how fast they can learn. It is an open assignment. There is no the
right answer and there is no end goal other than proving yourself. Surprise us!

Make sure that you give detailed comments or descriptions of your tests. When the assignment is
complete, please push your solution to Github(Gitlab) and send us the link.

If you have any questions, please contact us back. Good luck.

PS. We don't expect you to spend weeks (and sleepless nights) on doing it. Lets see how far you can
get in 6-10 hours. We want to see how you approach and solve problems.

PSPS. Please use mobile native tools. (Tests written on Java are accepted too)

# Test assignment

We are looking for Automation Engineers that have the mindset "only the sky is the limit" and "automation doesn't stop at testing, it's just a beginning!" ;)

The purpose of this test assignment is to assess the applicant's automation skills, allowing him/her to show the best they can do and how fast they can learn.
It is an open assignment. There is no the right answer and there is no end goal other than proving yourself. Surprise us!

Make sure that you give detailed comments or descriptions of your tests.
When the assignment is complete, please push your solution to Github(Gitlab) and send us the link 

If you have any questions, please contact us back.

Good luck.

PS. We don't expect you to spend weeks (and sleepless nights) on doing it. Lets see how far you can get in 6-10 hours. We want to see how you approach and solve problems.

PSPS. Please use mobile native tools. (Tests written on Java are accepted too)

---

## QA solution

Risk-based **native Espresso** UI automation. **No unit tests by design**: the app has no domain
logic — behaviour is Android framework wiring (FAB→Snackbar, menu, lifecycle) only observable via the
UI, so JVM/Robolectric tests would just re-assert wiring. (iOS *has* real logic → it gets XCTest.)

### Test results (2026-08-24)

15 instrumented tests — **15 passed, 0 failed, 0 errors (100% pass rate)**. Run on the API 33
"Compatible" emulator (JDK 8) with `animationsDisabled true`:
`./gradlew :app:connectedDebugAndroidTest` → `Starting 15 tests on Compatible(AVD) - 13`,
`BUILD SUCCESSFUL`. Breakdown: 7 Kotlin + 7 Java (the same 7 behaviours in both languages) + 1
accessibility. No local JVM unit tests by design; the template `ExampleUnitTest` (2+2) is left
untouched and not counted.

### Changes vs the original template

- Added the instrumented suite under `app/src/androidTest/…`: `MainActivityKotlinTest.kt` (7 tests),
  `MainActivityTest.java` (Java mirror, 7), `AccessibilityTest.kt` (1), `screens/MainScreen.kt`
  (page object), `support/BaseUiTest.kt` (fixture), `support/EspressoExtensions.kt` (wait helpers).
- `app/build.gradle`: added `testOptions { animationsDisabled true }` (required — see below).
- Root `build.gradle`: removed the Detekt + SonarQube plugins and swapped `jcenter()` (shut down in
  2021) for `mavenCentral()` so the project builds offline; the untouched original is kept as
  `build.gradle.original`.
- Added `.gitignore` (build output, IDE files, `local.properties`) and this README section.
- Production code (`app/src/main`), `Jenkinsfile`, and app resources are unchanged.

### Implemented (`app/src/androidTest`; run on API ≤ 33 — Espresso 3.2.0 event injection removed in API 34)

- **7 behaviours ×2 languages** — Kotlin `MainActivityKotlinTest` + Java `MainActivityTest`
  ("Java accepted"): launch core screen (greeting+toolbar+actionable FAB), app title, FAB→Snackbar
  `Replace with your own action`, Snackbar has **no** action button (null-listener defect), overflow
  shows Settings, Settings no-op, activity recreation.
- **Accessibility** `AccessibilityTest`: FAB missing `contentDescription` characterized.
- **Structure**: `screens/MainScreen` (page object, id/text locators, fluent), `support/BaseUiTest`
  (fresh `ActivityScenarioRule`/test), `support/EspressoExtensions` (`waitForView`/`waitUntilGone`).
- **Determinism/isolation**: id>text selectors; Espresso sync; overflow matched in popup root;
  `animationsDisabled true` (**required** — else the overflow PopupWindow stalls Espresso idle ~62s);
  no fixed sleeps in tests (helpers poll 100 ms, 5 s cap); Snackbar-dismiss wait between tests.
- **Run**: `./gradlew :app:connectedDebugAndroidTest` (JDK 8, one API 33 emulator).

### Findings

FAB has no `contentDescription` (`activity_main.xml:26-32`); `setAction("Action", null)` hides the
action (`MainActivity.kt:19-20`); Settings is a consumed no-op (`MainActivity.kt:34-36`);
`allowBackup=true`; legacy toolchain (Gradle 6.1.1 / AGP 4.0.1 / Kotlin 1.3.72).

### Recommended — NOT implemented (out of tests-only / CI scope)

- **CI gap**: Jenkins runs only `testDebug` (JVM); it does **not** run Espresso — add a
  `connectedDebugAndroidTest` stage on a pinned emulator.
- **No backend/API** (no INTERNET permission) → Postman/WireMock/Charles would be artificial here.
- **Non-functional** (n/a to this stub): perf (startup/jank/memory), security (backup/pinning),
  release monitoring (crash-free/ANR).



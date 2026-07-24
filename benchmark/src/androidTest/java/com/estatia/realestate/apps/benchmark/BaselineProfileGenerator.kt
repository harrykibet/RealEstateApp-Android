package com.estatia.realestate.apps.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a Baseline Profile to improve the app's performance.
 *
 * Baseline Profiles are a list of methods and classes that are used during the critical user
 * journeys. Android uses these profiles to pre-compile the app on the device, reducing
 * the execution time of those methods.
 *
 * To generate the profile:
 * 1. Open the "Run" menu in Android Studio.
 * 2. Select "Edit Configurations...".
 * 3. Create a new "Android Instrumented Tests" configuration.
 * 4. Choose the `benchmark` module.
 * 5. Set the "Instrumentation arguments" to: `androidx.benchmark.enabledRules=BaselineProfile`.
 * 6. Run the test on a physical device or an emulator that supports profiles.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = "com.estatia.realestate.apps",
        // Check if there are any activities that need to be skipped or handled differently
        includeInStartupProfile = true
    ) {
        // This is the critical user journey for the app
        pressHome()
        startActivityAndWait()

        // After the app starts, we could interact with the UI to capture more methods.
        // For example, scrolling through the feed:
        // device.findObject(By.res("feed_list")).scroll(Direction.DOWN, 1.0f)
    }
}

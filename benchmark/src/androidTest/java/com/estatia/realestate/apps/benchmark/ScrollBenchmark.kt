package com.estatia.realestate.apps.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Measures the scrolling performance of the property feed.
 *
 * This benchmark ensures that as we add more complex UI and features (like video playback),
 * the scrolling remains smooth (no "jank").
 */
@RunWith(AndroidJUnit4::class)
class ScrollBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollFeed() = benchmarkRule.measureRepeated(
        packageName = "com.estatia.realestate.apps",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {
        // Wait for the feed to load. 
        // We look for a common UI element that indicates the feed is present.
        device.wait(Until.hasObject(By.scrollable(true)), 5000)

        val feedList = device.findObject(By.scrollable(true))
        
        // If a scrollable list is found, perform scrolls to measure frame timing
        if (feedList != null) {
            feedList.setGestureMargin(device.displayWidth / 5)
            feedList.fling(Direction.DOWN)
            device.waitForIdle()
            feedList.fling(Direction.UP)
        }
    }
}

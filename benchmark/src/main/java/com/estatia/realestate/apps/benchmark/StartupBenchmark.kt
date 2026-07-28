package com.estatia.realestate.apps.benchmark

import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Measures the startup time of the application.
 *
 * MEASURED COLD START: The app is not in the system memory.
 * MEASURED WARM START: The process is already running, but the activity is being recreated.
 * MEASURED HOT START: The activity is already in memory and brought to the foreground.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupCold() = startup(StartupMode.COLD)

    @Test
    fun startupWarm() = startup(StartupMode.WARM)

    @Test
    fun startupHot() = startup(StartupMode.HOT)

    private fun startup(startupMode: StartupMode) = benchmarkRule.measureRepeated(
        packageName = InstrumentationRegistry.getInstrumentation().targetContext.packageName,
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = startupMode,
        setupBlock = {
            pressHome()
        }
    ) {
        startActivityAndWait()
    }
}

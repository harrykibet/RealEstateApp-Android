package com.estatia.realestate.apps.benchmark

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertNotNull

/**
 * Instrumented tests for the benchmark module (run on device/emulator).
 * Benchmark runs are configured separately; this ensures the test source set exists.
 */
@RunWith(AndroidJUnit4::class)
class BenchmarkInstrumentedTest {

    @Test
    fun instrumentationContext_isAvailable() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull(context)
    }
}

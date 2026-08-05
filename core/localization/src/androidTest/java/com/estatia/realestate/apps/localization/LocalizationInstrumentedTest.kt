package com.estatia.realestate.apps.localization

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertNotNull

/**
 * Instrumented tests for the localization module (run on device/emulator).
 * Add tests for context-dependent locale or resource behavior when needed.
 */
@RunWith(AndroidJUnit4::class)
class LocalizationInstrumentedTest {

    @Test
    fun instrumentationContext_isAvailable() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull(context)
    }
}

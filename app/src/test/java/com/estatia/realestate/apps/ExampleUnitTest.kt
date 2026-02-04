package com.estatia.realestate.apps

import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * Unit tests for the app module (run on JVM).
 * Add tests for ViewModels, use cases, or app-level logic here.
 */
class ExampleUnitTest {

    @Test
    fun themeSettings_darkTheme_valuesArePreserved() {
        val settings = ThemeSettings(
            darkTheme = true,
            androidTheme = false,
            disableDynamicTheming = true
        )
        assertEquals(true, settings.darkTheme)
        assertEquals(false, settings.androidTheme)
        assertEquals(true, settings.disableDynamicTheming)
    }

    @Test
    fun themeSettings_lightTheme_valuesArePreserved() {
        val settings = ThemeSettings(
            darkTheme = false,
            androidTheme = true,
            disableDynamicTheming = false
        )
        assertEquals(false, settings.darkTheme)
        assertEquals(true, settings.androidTheme)
        assertEquals(false, settings.disableDynamicTheming)
    }
}

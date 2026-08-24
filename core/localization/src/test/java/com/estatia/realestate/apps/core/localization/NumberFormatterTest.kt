package com.estatia.realestate.apps.core.localization

import com.estatia.realestate.apps.core.localization.implementation.AndroidNumberFormatter
import com.estatia.realestate.apps.core.testing.chaos.input.InputBehavior
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class NumberFormatterTest {

    private val formatter = AndroidNumberFormatter()

    @Test
    fun `formatCompactNumber formats millions correctly`() {
        Locale.setDefault(Locale.US)
        assertEquals("1.5M", formatter.formatCompactNumber(1_500_000))
        assertEquals("1M", formatter.formatCompactNumber(1_000_000))
    }

    @Test
    fun `formatCompactNumber handles extreme value chaos`() {
        // 🧪 Chaos Scenario: Maximum and Negative Values
        println("Testing behavior: ${InputBehavior.MaximumValues}")
        assertEquals("2.1B", formatter.formatCompactNumber(Int.MAX_VALUE))
        
        println("Testing behavior: ${InputBehavior.NegativeValue}")
        // Implementation dependent, but should not crash
        formatter.formatCompactNumber(-100)
    }

    @Test
    fun `formatCompactNumber formats thousands correctly`() {
        Locale.setDefault(Locale.US)
        assertEquals("1.2k", formatter.formatCompactNumber(1_200))
        assertEquals("1k", formatter.formatCompactNumber(1_000))
    }

    @Test
    fun `formatCompactNumber returns small numbers as string`() {
        assertEquals("999", formatter.formatCompactNumber(999))
    }
}

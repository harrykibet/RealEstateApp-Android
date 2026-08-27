package com.estatia.realestate.apps.core.testing.chaos.input

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChaosInputGeneratorTest {

    @Test
    fun generateString_handlesVariousBehaviors() {
        assertNull(ChaosInputGenerator.generateString(InputBehavior.NullInput))
        assertEquals("", ChaosInputGenerator.generateString(InputBehavior.EmptyInput))
        assertEquals("   ", ChaosInputGenerator.generateString(InputBehavior.BlankInput))
        assertEquals("malformed_!@#$%^&*", ChaosInputGenerator.generateString(InputBehavior.MalformedInput))
        
        val oversized = ChaosInputGenerator.generateString(InputBehavior.OversizedInput)
        assertTrue(oversized != null && oversized.length > 10_000)
        
        assertEquals("Unicode \uD83D\uDCA3 \u2623 \uD83D\uDD25", ChaosInputGenerator.generateString(InputBehavior.UnicodeChaos))
    }

    @Test
    fun generateInt_handlesVariousBehaviors() {
        assertEquals(-1, ChaosInputGenerator.generateInt(InputBehavior.NegativeValue))
        assertEquals(0, ChaosInputGenerator.generateInt(InputBehavior.ZeroValue))
        assertEquals(Int.MAX_VALUE, ChaosInputGenerator.generateInt(InputBehavior.MaximumValues))
        assertEquals(10, ChaosInputGenerator.generateInt(InputBehavior.Success))
    }
}

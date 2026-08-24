package com.estatia.realestate.apps.core.intelligence

import android.content.Context
import com.estatia.realestate.apps.core.model.engagement.SafetyResult
import com.estatia.realestate.apps.core.testing.chaos.input.InputBehavior
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MlKitContentSafetyServiceTest {

    private lateinit var service: MlKitContentSafetyService
    private val context: Context = mockk(relaxed = true)

    @Before
    fun setup() {
        service = MlKitContentSafetyService(context)
    }

    @Test
    fun `validateText flags abusive content based on heuristic patterns`() = runTest {
        // 🧪 Adversarial Scenario: Malformed/Toxic Input
        val behavior = InputBehavior.MalformedInput
        
        val toxicText = "This is a very abusive_term1 message."
        val result = service.validateText(toxicText)
        
        assertTrue("Abusive content should be flagged", result is SafetyResult.Flagged)
    }

    @Test
    fun `detectSensitiveData identifies phone numbers and emails`() = runTest {
        val text = "Call me at +254712345678 or email test@estatia.com"
        val entities = service.detectSensitiveData(text)
        
        assertTrue("Should detect phone number", entities.any { it.type == "PHONE" })
        assertTrue("Should detect email", entities.any { it.type == "EMAIL" })
    }

    @Test
    fun `validateText handles empty input chaos safely`() = runTest {
        // 🧪 Chaos Scenario: Empty Input
        val behavior = InputBehavior.EmptyInput
        
        val result = service.validateText("")
        assertTrue("Empty text should be safe (or handled via validation)", result is SafetyResult.Safe)
    }
}

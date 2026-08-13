package com.estatia.realestate.apps.core.intelligence

import android.content.Context
import com.estatia.realestate.apps.core.model.engagement.SafetyResult
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
    fun `validateText passes clean content`() = runTest {
        val cleanText = "This is a wonderful property!"
        val result = service.validateText(cleanText)
        
        assertTrue("Clean content should be safe", result is SafetyResult.Safe)
    }
}

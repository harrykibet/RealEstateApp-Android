package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import androidx.core.net.toUri

class CacheKeyRegressionTest {

    private lateinit var factory: DefaultCacheKeyFactory

    @Before
    @Suppress("UseKtx")
    fun setup() {
        mockkStatic(Uri::class)
        val mockUri = mockk<Uri>(relaxed = true)
        every { Uri.parse(any()) } returns mockUri
        mockkStatic("androidx.core.net.UriKt")

        factory = DefaultCacheKeyFactory()
    }

    @Test
    fun `resolveStableKey uses providedId as primary key`() {
        val uri = mockk<Uri>(relaxed = true)
        val propertyId = "prop_999"
        
        val key = factory.resolveStableKey(uri, propertyId)
        
        assertEquals("prop_999", key)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `resolveStableKey throws if no ID provided`() {
        val uri = mockk<Uri>(relaxed = true)
        factory.resolveStableKey(uri, null)
    }
}

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

    @Test
    fun `resolveStableKey falls back to URI stripping query if no ID provided`() {
        val uri = mockk<Uri>(relaxed = true)
        val builder = mockk<Uri.Builder>(relaxed = true)
        val cleanedUri = mockk<Uri>(relaxed = true)
        
        every { uri.buildUpon() } returns builder
        every { builder.clearQuery() } returns builder
        every { builder.build() } returns cleanedUri
        every { cleanedUri.toString() } returns "https://cdn.com/video.mp4"
        
        val key = factory.resolveStableKey(uri, null)
        
        assertEquals("https://cdn.com/video.mp4", key)
    }
}

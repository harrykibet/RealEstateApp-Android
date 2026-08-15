package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

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
    fun `resolveStableKey incorporates path fingerprint`() {
        val uri1 = mockk<Uri>(relaxed = true) { every { path } returns "/video1.mp4" }
        val uri2 = mockk<Uri>(relaxed = true) { every { path } returns "/video2.mp4" }
        val propertyId = "prop_999"

        val key1 = factory.resolveStableKey(uri1, propertyId)
        val key2 = factory.resolveStableKey(uri2, propertyId)

        assert(key1 != key2)
        assert(key1.startsWith(propertyId))
        assert(key2.startsWith(propertyId))
    }

    @Test
    fun `resolveStableKey is resilient to query parameter changes`() {
        val uri1 = mockk<Uri>(relaxed = true)
        every { uri1.path } returns "/video.mp4"
        every { uri1.toString() } returns "https://cdn.com/video.mp4?token=1"

        val uri2 = mockk<Uri>(relaxed = true)
        every { uri2.path } returns "/video.mp4"
        every { uri2.toString() } returns "https://cdn.com/video.mp4?token=2"

        val propertyId = "prop_999"

        val key1 = factory.resolveStableKey(uri1, propertyId)
        val key2 = factory.resolveStableKey(uri2, propertyId)

        assertEquals("Keys should be identical regardless of query tokens", key1, key2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `resolveStableKey throws if no ID provided`() {
        val uri = mockk<Uri>(relaxed = true)
        factory.resolveStableKey(uri, null)
    }
}

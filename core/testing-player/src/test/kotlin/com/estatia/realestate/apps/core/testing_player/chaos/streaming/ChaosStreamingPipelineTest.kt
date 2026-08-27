package com.estatia.realestate.apps.core.testing_player.chaos.streaming

import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.WarmPriority
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

@UnstableApi
class ChaosStreamingPipelineTest {

    private lateinit var delegate: IStreamingPipeline
    private lateinit var chaosPipeline: ChaosStreamingPipeline

    @Before
    fun setUp() {
        delegate = mockk(relaxed = true)
        chaosPipeline = ChaosStreamingPipeline(delegate)
    }

    @Test
    fun `warm calls delegate when setFailSegments is false`() {
        val mediaId = "id"
        val uri = MediaReference("uri")
        val priority = WarmPriority.VISIBLE
        val qualityHint = "hint"

        chaosPipeline.setFailSegments(false)
        chaosPipeline.warm(mediaId, uri, priority, qualityHint)

        verify { delegate.warm(mediaId, uri, priority, qualityHint) }
    }

    @Test
    fun `warm does not call delegate when setFailSegments is true`() {
        val mediaId = "id"
        val uri = MediaReference("uri")
        val priority = WarmPriority.VISIBLE
        val qualityHint = "hint"

        chaosPipeline.setFailSegments(true)
        chaosPipeline.warm(mediaId, uri, priority, qualityHint)

        verify(exactly = 0) { delegate.warm(any(), any(), any(), any()) }
    }

    @Test
    fun `onBufferingStarted is delegated`() {
        chaosPipeline.onBufferingStarted()
        verify { delegate.onBufferingStarted() }
    }

    @Test
    fun `onBufferingEnded is delegated`() {
        chaosPipeline.onBufferingEnded()
        verify { delegate.onBufferingEnded() }
    }

    @Test
    fun `mediaSourceFactory is delegated`() {
        val factory = mockk<MediaSource.Factory>()
        every { delegate.mediaSourceFactory() } returns factory

        val result = chaosPipeline.mediaSourceFactory()

        assert(result == factory)
        verify { delegate.mediaSourceFactory() }
    }

    @Test
    fun `createMediaItem is delegated`() {
        val mediaId = "id"
        val uri = MediaReference("uri")
        val mediaType = MediaType.VOD
        val mediaItem = mockk<MediaItem>()

        every {
            delegate.createMediaItem(mediaId, uri, mediaType, any(), any(), any())
        } returns mediaItem

        val result = chaosPipeline.createMediaItem(mediaId, uri, mediaType)

        assert(result == mediaItem)
        verify { delegate.createMediaItem(mediaId, uri, mediaType, any(), any(), any()) }
    }
}

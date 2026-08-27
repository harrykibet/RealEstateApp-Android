package com.estatia.realestate.apps.core.player_ui.screens

import android.net.Uri
import android.view.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.core.IPlayerManager
import com.estatia.realestate.apps.core.player_ui.core.LocalEnvironmentState
import com.estatia.realestate.apps.core.player_ui.core.LocalPlayerManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Rule
import org.junit.Test
import androidx.core.net.toUri

class EngineVideoPlayerFlickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockPlayer1 = mockk<Player>(relaxed = true)
    private val mockPlayer2 = mockk<Player>(relaxed = true)
    private val mockPlayerManager = mockk<IPlayerManager>(relaxed = true)
    private val environmentState = EnvironmentState(
        isMetered = false,
        shouldThrottlePerformance = false,
        estimatedThroughputBps = 10_000_000L,
        isAppVisible = true,
        isInteractive = true,
    )

    @Test
    fun swapMediaId_noPoster_preservesSurfaceInstance() {
        var mediaId by mutableStateOf("id1")
        var uri by mutableStateOf("https://example.com/1.mp4".toUri())
        
        val surfacesCapturedByPlayer1 = mutableListOf<Surface>()
        val surfacesCapturedByPlayer2 = mutableListOf<Surface>()
        
        every { mockPlayer1.setVideoSurface(any()) } answers {
            firstArg<Surface?>()?.let { surfacesCapturedByPlayer1.add(it) }
        }
        every { mockPlayer2.setVideoSurface(any()) } answers {
            firstArg<Surface?>()?.let { surfacesCapturedByPlayer2.add(it) }
        }

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalPlayerManager provides mockPlayerManager,
                LocalEnvironmentState provides environmentState
            ) {
                EngineVideoPlayer(
                    mediaId = mediaId,
                    uri = uri,
                    mediaType = MediaType.VOD,
                    getPlayer = { id, _, _, _ -> if (id == "id1") mockPlayer1 else mockPlayer2 },
                    onPause = {},
                    isActive = true,
                    isMuted = false,
                    onMuteToggle = {}
                )
            }
        }

        composeTestRule.waitForIdle()
        
        // Initial state: Player 1 should have a surface
        assert(surfacesCapturedByPlayer1.isNotEmpty()) { "Player 1 never received a surface" }
        val initialSurface = surfacesCapturedByPlayer1.first()

        // Act: Swap mediaId to trigger recomposition and player swap
        mediaId = "id2"
        uri = "https://example.com/2.mp4".toUri()
        composeTestRule.waitForIdle()

        // Assert: Player 2 should receive the SAME surface instance
        assert(surfacesCapturedByPlayer2.isNotEmpty()) { "Player 2 never received a surface after swap" }
        val surfaceAfterSwap = surfacesCapturedByPlayer2.first()
        
        assert(initialSurface === surfaceAfterSwap) {
            "Surface was recreated (flicker detected)! Expected the same instance, but got different surfaces."
        }
        
        // Also verify that the surface node is still displayed
        composeTestRule.onNodeWithTag("EngineVideoPlayer_Surface").assertIsDisplayed()
    }

    @Test
    fun posterUri_transitionsCorrectly() {
        val posterUri = "https://example.com/poster.jpg".toUri()
        val listenerSlot = slot<Player.Listener>()
        
        every { mockPlayer1.addListener(capture(listenerSlot)) } returns Unit
        every { mockPlayer1.playbackState } returns Player.STATE_BUFFERING
        every { mockPlayer1.isPlaying } returns false

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalPlayerManager provides mockPlayerManager,
                LocalEnvironmentState provides environmentState
            ) {
                EngineVideoPlayer(
                    mediaId = "id1",
                    uri = Uri.EMPTY,
                    mediaType = MediaType.VOD,
                    getPlayer = { _, _, _, _ -> mockPlayer1 },
                    onPause = {},
                    isActive = true,
                    isMuted = false,
                    onMuteToggle = {},
                    posterUri = posterUri
                )
            }
        }

        // 1. Verify poster is shown during buffering
        composeTestRule.onNodeWithTag("EngineVideoPlayer_Poster").assertIsDisplayed()
        
        // 2. Mock playback state change to READY
        every { mockPlayer1.playbackState } returns Player.STATE_READY
        composeTestRule.runOnUiThread {
            listenerSlot.captured.onPlaybackStateChanged(Player.STATE_READY)
        }
        
        // 3. Wait for Crossfade animation (500ms)
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.mainClock.advanceTimeBy(600)
        
        // 4. Verify surface is now displayed and poster is gone
        composeTestRule.onNodeWithTag("EngineVideoPlayer_Surface").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EngineVideoPlayer_Poster").assertDoesNotExist()
    }
}

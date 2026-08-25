package com.estatia.realestate.apps.chaos

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.media3.common.util.UnstableApi
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.estatia.realestate.apps.MainActivity
import com.estatia.realestate.apps.core.player_engine.core.PlayerPool
import com.estatia.realestate.apps.core.player_engine.core.PlayerManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@UnstableApi
@HiltAndroidTest
class FeedGestureChaosTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var pool: PlayerPool

    @Inject
    lateinit var playerManager: PlayerManager

    private lateinit var device: UiDevice

    @Before
    fun setup() {
        hiltRule.inject()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun fastFlickThroughFeed() {
        val width = device.displayWidth
        val height = device.displayHeight
        val centerX = width / 2
        val startY = (height * 0.8).toInt()
        val endY = (height * 0.2).toInt()

        repeat(50) {
            device.swipe(centerX, startY, centerX, endY, (Math.random() * 5 + 5).toInt())
        }

        Thread.sleep(2000)

        val settledId = playerManager.debugActiveMediaId
        if (settledId != null) {
            assertTrue("Visible video $settledId was evicted during chaos!", pool.debugIsIdActive(settledId))
        }

        assertPoolInvariant()
    }

    @Test
    fun rapidReversalScroll() {
        val width = device.displayWidth
        val height = device.displayHeight
        val centerX = width / 2
        val startY = (height * 0.8).toInt()
        val endY = (height * 0.2).toInt()

        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 10000) {
            // Swipe Up
            device.swipe(centerX, startY, centerX, endY, 10)
            Thread.sleep(80)
            // Swipe Down
            device.swipe(centerX, endY, centerX, startY, 10)
            Thread.sleep(80)
        }

        Thread.sleep(2000)
        assertPoolInvariant()
    }

    private fun assertPoolInvariant() {
        val currentCount = pool.debugPlayerCount
        val maxAllowed = pool.debugMaxPoolSize + 2 
        assertTrue("Pool size ($currentCount) exceeded max allowed ($maxAllowed)", currentCount <= maxAllowed)
        assertFalse("Duplicate player instances detected in pool", pool.debugHasDuplicateInstances())
    }
}

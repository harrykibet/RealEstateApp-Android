package com.estatia.realestate.apps.core.player_ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class VideoProgressBarPersistenceTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dragProgressPersistsAcrossParentRecomposition() {
        var recomposeTrigger by mutableStateOf(0)
        var seekValue = -1f

        composeTestRule.setContent {
            // Access state to trigger recomposition
            if (recomposeTrigger >= 0) {
                Box(Modifier.testTag("ParentWrapper")) {
                    VideoProgressBar(
                        progress = 0.5f,
                        bufferedProgress = 0.7f,
                        onSeekRequest = { seekValue = it },
                        modifier = Modifier
                            .testTag("VideoProgressBar")
                            .width(400.dp)
                    )
                }
            }
        }

        // 1. Verify initial state (knob should not be visible)
        composeTestRule.onNodeWithTag("VideoProgressBar_Knob").assertDoesNotExist()

        // 2. Start drag gesture but do NOT release yet
        composeTestRule.onNodeWithTag("VideoProgressBar").performTouchInput {
            down(center)
            moveBy(Offset(100f, 0f))
        }

        // 3. Verify that the UI shows the dragging state (knob visible)
        composeTestRule.onNodeWithTag("VideoProgressBar_Knob").assertIsDisplayed()

        // 4. While the drag is active, trigger a recomposition of the wrapper
        recomposeTrigger++
        composeTestRule.waitForIdle()

        // 5. Verify that the UI STILL shows the dragging state
        composeTestRule.onNodeWithTag("VideoProgressBar_Knob").assertIsDisplayed()

        // 6. Release the drag
        composeTestRule.onNodeWithTag("VideoProgressBar").performTouchInput {
            up()
        }

        // 7. Verify onSeekRequest is called with the correct value
        assertTrue("Seek value should be greater than 0.5f, but was $seekValue", seekValue > 0.5f)
    }
}

package com.estatia.realestate.apps.core.player_engine.analytics

import com.estatia.realestate.apps.core.domain.analytics.IAnalyticsTracker
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import io.mockk.mockk
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertNotEquals
import org.junit.Test
import androidx.media3.common.util.UnstableApi

@androidx.annotation.OptIn(UnstableApi::class)
class AnalyticsRegressionTest {

    @Test
    fun `PlaybackAnalyticsListener generates unique session IDs on every construction`() {
        val client: IAnalyticsTracker = mockk()
        val engagement: IEngagementRepository = mockk()
        val metrics: IMetricsTracker = mockk()
        val scope = TestScope()

        val listener1 = PlaybackAnalyticsListener(client, engagement, metrics, scope)
        val listener2 = PlaybackAnalyticsListener(client, engagement, metrics, scope)
        
        assertNotEquals("Session IDs must be unique across instances", listener1.sessionId, listener2.sessionId)
    }
}

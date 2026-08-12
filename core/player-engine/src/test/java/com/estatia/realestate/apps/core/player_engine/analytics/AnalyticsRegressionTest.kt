package com.estatia.realestate.apps.core.player_engine.analytics

import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.domain.interfaces.IMetricsTracker
import io.mockk.mockk
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertNotEquals
import org.junit.Test

class AnalyticsRegressionTest {

    @Test
    fun `PlaybackAnalyticsListener generates unique session IDs on every construction`() {
        val client: IAnalyticsTracker = mockk()
        val metrics: IMetricsTracker = mockk()
        val scope = TestScope()

        val listener1 = PlaybackAnalyticsListener(client, metrics, scope)
        val listener2 = PlaybackAnalyticsListener(client, metrics, scope)
        
        assertNotEquals("Session IDs must be unique across instances", listener1.sessionId, listener2.sessionId)
    }
}

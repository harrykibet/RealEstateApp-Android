package com.estatia.realestate.apps.core.player_engine.streaming

import kotlin.time.Duration

interface ILatencyMeasurer {
    suspend fun measure(host: String, timeout: Duration): Long
}

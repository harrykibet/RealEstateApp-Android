package com.estatia.realestate.apps.core.player_engine.streaming

import kotlinx.coroutines.withTimeout
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class DefaultLatencyMeasurer @Inject constructor() : ILatencyMeasurer {
    override suspend fun measure(host: String, timeout: Duration): Long {
        val start = System.currentTimeMillis()
        return try {
            withTimeout(timeout) {
                val socket = Socket()
                socket.connect(InetSocketAddress(host, 80), timeout.inWholeMilliseconds.toInt())
                socket.close()
            }
            System.currentTimeMillis() - start
        } catch (e: Exception) {
            throw RuntimeException("Latency probe failed", e)
        }
    }
}

package com.estatia.realestate.apps.core.player_engine.streaming

import kotlinx.coroutines.withTimeout
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import androidx.core.net.toUri

@Singleton
class DefaultLatencyMeasurer @Inject constructor() : ILatencyMeasurer {
    override suspend fun measure(host: String, timeout: Duration): Long {
        val start = System.currentTimeMillis()
        return try {
            withTimeout(timeout) {
                val parsed = host.trim().toUri()
                val normalizedHost = parsed.host ?: host.trim()
                val port = parsed.port.takeIf { it > 0 } ?: if (parsed.scheme == "https") 443 else 80
                val socket = Socket()
                socket.connect(InetSocketAddress(normalizedHost, port), timeout.inWholeMilliseconds.toInt())
                socket.close()
            }
            System.currentTimeMillis() - start
        } catch (e: Exception) {
            throw RuntimeException("Latency probe failed", e)
        }
    }
}

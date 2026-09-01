package com.estatia.realestate.apps.core.testing_network.chaos.interceptors

import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.Source
import okio.buffer
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * OkHttp Interceptor that injects transport-level chaos (stream truncation, corruption).
 * Bridges the [NetworkChaosController] with the raw network stream.
 */
class ChaosInterceptor @Inject constructor(
    private val controller: NetworkChaosController
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val behavior = controller.popNext()

        // 1. Pre-request Protocol Chaos
        when (behavior) {
            NetworkBehavior.Offline -> throw IOException("No network (Chaos Interceptor)")
            NetworkBehavior.Timeout -> throw SocketTimeoutException("Connection timed out (Chaos Interceptor)")
            is NetworkBehavior.Delay -> runBlocking { delay(behavior.duration) }
            else -> Unit
        }

        val response = chain.proceed(chain.request())

        // 2. Post-request Transport Chaos
        return when (behavior) {
            NetworkBehavior.PartialResponse -> {
                val body = response.body ?: return response
                response.newBuilder()
                    .body(TruncatedResponseBody(body))
                    .message("Partial Response (Chaos)")
                    .build()
            }
            NetworkBehavior.MalformedResponse -> {
                val body = response.body ?: return response
                response.newBuilder()
                    .body(CorruptedResponseBody(body))
                    .message("Malformed Response (Chaos)")
                    .build()
            }
            is NetworkBehavior.HttpError -> {
                response.newBuilder()
                    .code(behavior.statusCode)
                    .message("HTTP ${behavior.statusCode} (Chaos)")
                    .build()
            }
            else -> response
        }
    }
}

/**
 * Truncates the response stream mid-way.
 */
private class TruncatedResponseBody(private val delegate: ResponseBody) : ResponseBody() {
    override fun contentType(): MediaType? = delegate.contentType()
    override fun contentLength(): Long = if (delegate.contentLength() != -1L) delegate.contentLength() / 2 else -1L
    override fun source(): BufferedSource {
        val originalSource = delegate.source()
        val limit = if (delegate.contentLength() != -1L) delegate.contentLength() / 2 else 1024L // fallback limit
        
        return object : Source by originalSource {
            private var totalRead = 0L
            override fun read(sink: Buffer, byteCount: Long): Long {
                if (totalRead >= limit) return -1
                val toRead = minOf(byteCount, limit - totalRead)
                val read = originalSource.read(sink, toRead)
                if (read != -1L) totalRead += read
                return read
            }
        }.buffer()
    }
}

/**
 * Corrupts the response stream by flipping bits in the first segment.
 */
private class CorruptedResponseBody(private val delegate: ResponseBody) : ResponseBody() {
    override fun contentType(): MediaType? = delegate.contentType()
    override fun contentLength(): Long = delegate.contentLength()
    override fun source(): BufferedSource {
        val originalSource = delegate.source()
        return object : Source {
            private var isFirstRead = true
            
            override fun read(sink: Buffer, byteCount: Long): Long {
                val temp = Buffer()
                val read = originalSource.read(temp, byteCount)
                if (read > 0) {
                    if (isFirstRead) {
                        isFirstRead = false
                        // Flip the very first byte of the stream
                        val firstByte = temp.readByte()
                        sink.writeByte(firstByte.toInt() xor 0xFF)
                    }
                    sink.writeAll(temp)
                }
                return read
            }

            override fun close() = originalSource.close()
            override fun timeout() = originalSource.timeout()
        }.buffer()
    }
}

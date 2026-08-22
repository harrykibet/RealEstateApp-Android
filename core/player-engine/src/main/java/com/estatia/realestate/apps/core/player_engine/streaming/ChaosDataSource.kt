package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import com.estatia.realestate.apps.core.domain.config.IChaosConfig
import com.estatia.realestate.apps.core.model.config.ChaosConfig
import java.io.IOException
import kotlin.random.Random

/**
 * A [DataSource] wrapper that injects artificial failures, stalls, and throttling
 * for testing and debugging purposes.
 */
@UnstableApi
class ChaosDataSource(
    private val delegate: DataSource,
    private val config: IChaosConfig
) : DataSource {

    private val chaos: ChaosConfig
        get() = config.chaosConfig

    override fun addTransferListener(transferListener: TransferListener) {
        delegate.addTransferListener(transferListener)
    }

    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        if (chaos.simulateStall) {
            Thread.sleep(chaos.stallDurationMs)
        }

        if (chaos.failureRate > 0f && Random.nextFloat() < chaos.failureRate) {
            throw IOException("Chaos: Injected failure during open()")
        }

        return delegate.open(dataSpec)
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (chaos.throttleBps > 0) {
            applyThrottle(length, chaos.throttleBps)
        }

        if (chaos.failureRate > 0f && Random.nextFloat() < (chaos.failureRate / 10f)) {
            throw IOException("Chaos: Injected failure during read()")
        }

        return delegate.read(buffer, offset, length)
    }

    override fun getUri(): Uri? = delegate.uri

    @Throws(IOException::class)
    override fun close() {
        delegate.close()
    }

    override fun getResponseHeaders(): Map<String, List<String>> = delegate.responseHeaders

    private fun applyThrottle(bytesToRead: Int, maxBps: Long) {
        val targetMs = (bytesToRead.toDouble() / maxBps * 1000).toLong()
        if (targetMs > 0) {
            Thread.sleep(targetMs)
        }
    }
}

/**
 * Factory for creating [ChaosDataSource] instances.
 */
@UnstableApi
class ChaosDataSourceFactory(
    private val delegateFactory: DataSource.Factory,
    private val config: IChaosConfig
) : DataSource.Factory {
    override fun createDataSource(): DataSource {
        return ChaosDataSource(delegateFactory.createDataSource(), config)
    }
}

package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import androidx.core.net.toUri
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import java.io.IOException

/**
 * A [DataSource] decorator that implements segment-level CDN failover.
 * It intercepts requests to Estatia internal domains and routes them to the healthiest CDN.
 * If a request fails, it reports the failure to [CdnHealthMonitor], selects an alternative CDN,
 * and retries the request transparently.
 */
@UnstableApi
class CdnFailoverDataSource(
    private val baseDataSource: DataSource,
    private val cdnSelector: CdnSelector,
    private val healthMonitor: CdnHealthMonitor,
    private val logger: ILogger
) : DataSource {

    override fun addTransferListener(transferListener: TransferListener) {
        baseDataSource.addTransferListener(transferListener)
    }

    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        val originalUri = dataSpec.uri
        
        if (!needsCdnResolution(originalUri)) {
            return baseDataSource.open(dataSpec)
        }

        val currentCdn = cdnSelector.select()
        val resolvedUri = resolve(originalUri, currentCdn)

        return try {
            baseDataSource.open(dataSpec.withUri(resolvedUri))
        } catch (e: IOException) {
            logger.w("CDN", "Segment load failure on ${currentCdn.baseUrl}. Attempting failover for $originalUri")
            
            // 1. Report failure to trigger circuit breaker
            healthMonitor.reportExternalFailureAsync(currentCdn.baseUrl)

            // 2. Select alternative
            val alternativeCdn = cdnSelector.select()
            if (alternativeCdn.baseUrl == currentCdn.baseUrl) {
                // No other healthy CDNs available, propagate error
                throw e
            }

            // 3. Retry once with alternative
            val retryUri = resolve(originalUri, alternativeCdn)
            logger.i("CDN", "Retrying with alternative CDN: ${alternativeCdn.baseUrl}")
            baseDataSource.open(dataSpec.withUri(retryUri))
        }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        return baseDataSource.read(buffer, offset, length)
    }

    override fun getUri(): Uri? {
        return baseDataSource.uri
    }

    @Throws(IOException::class)
    override fun close() {
        baseDataSource.close()
    }

    private fun needsCdnResolution(uri: Uri): Boolean {
        val host = uri.host ?: return false
        return host == "estatia.com" || host.endsWith(".estatia.com")
    }

    private fun resolve(uri: Uri, cdn: CdnEndpoint): Uri {
        val endpointUri = cdn.baseUrl.toUri()
        return uri.buildUpon()
            .scheme(endpointUri.scheme ?: uri.scheme)
            .authority(endpointUri.authority ?: uri.authority)
            .build()
    }
}

/**
 * Factory for [CdnFailoverDataSource].
 */
@UnstableApi
class CdnFailoverDataSourceFactory(
    private val baseFactory: DataSource.Factory,
    private val cdnSelector: CdnSelector,
    private val healthMonitor: CdnHealthMonitor,
    private val logger: ILogger
) : DataSource.Factory {
    override fun createDataSource(): DataSource {
        return CdnFailoverDataSource(
            baseFactory.createDataSource(),
            cdnSelector,
            healthMonitor,
            logger
        )
    }
}

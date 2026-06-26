package com.estatia.realestate.apps.core.player_engine.utils

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.system.BatteryState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@UnstableApi
class EnvironmentCoordinator @Inject constructor(
    private val networkUtils: INetworkUtils,
    private val batteryManager: IBatteryManager,
    private val bandwidthMeter: BandwidthMeter
) {

    private val _environment = MutableStateFlow(
        EnvironmentState(
            isMetered = false,
            shouldThrottlePerformance = false,
            estimatedThroughputBps = bandwidthMeter.bitrateEstimate
        )
    )

    val environment: StateFlow<EnvironmentState> = _environment.asStateFlow()

    fun start(scope: CoroutineScope) {
        scope.launch {
            combine(
                networkUtils.observeNetworkStatus()
                    .map { it.isMetered}
                    .distinctUntilChanged(),

                batteryManager.observeBatteryState()
                    .map { it is BatteryState.Throttled }
                    .distinctUntilChanged(),

                observeBandwidth()
                    .distinctUntilChanged()
            ) { isMetered, shouldThrottle, bandwidth ->
                EnvironmentState(
                    isMetered = isMetered,
                    shouldThrottlePerformance = shouldThrottle,
                    estimatedThroughputBps = bandwidth
                )
            }
                .distinctUntilChanged()
                .collect { _environment.value = it }
        }
    }

    private fun observeBandwidth(): Flow<Long> = callbackFlow {
        val listener = BandwidthMeter.EventListener { _, _, _ ->
            trySend(bandwidthMeter.bitrateEstimate)
        }

        bandwidthMeter.addEventListener(
            /* eventHandler = */ null,
            listener
        )

        // Emit initial value
        trySend(bandwidthMeter.bitrateEstimate)

        awaitClose {
            bandwidthMeter.removeEventListener(listener)
        }
    }
}
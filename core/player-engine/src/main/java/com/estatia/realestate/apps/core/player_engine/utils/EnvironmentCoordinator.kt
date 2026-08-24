package com.estatia.realestate.apps.core.player_engine.utils

import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.system.BatteryState
import com.estatia.realestate.apps.core.common.system.SystemResourcesMonitor
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import com.estatia.realestate.apps.core.network.core.NetworkState
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@UnstableApi
class EnvironmentCoordinator @Inject constructor(
    private val networkStateProvider: INetworkStateProvider,
    private val batteryManager: IBatteryManager,
    private val bandwidthMeter: BandwidthMeter,
    private val connectivityManager: ConnectivityManager,
    private val resourcesMonitor: SystemResourcesMonitor
) {

    private val _environment = MutableStateFlow(
        EnvironmentState(
            isMetered = false,
            shouldThrottlePerformance = false,
            estimatedThroughputBps = bandwidthMeter.bitrateEstimate,
            memoryTrimLevel = resourcesMonitor.memoryTrimLevel.value,
            isAppVisible = resourcesMonitor.isAppVisible.value,
            isInteractive = resourcesMonitor.isInteractive.value,
            thermalStatus = 0
        )
    )

    val environment: StateFlow<EnvironmentState> = _environment.asStateFlow()

    private val _meteredConnectionDetected = MutableSharedFlow<Unit>(replay = 0)
    val meteredConnectionDetected = _meteredConnectionDetected.asSharedFlow()

    private var job: Job? = null
    
    private var consecutiveLowBandwidthCount = 0
    private var isSustainedLowBandwidth = false
    private var wasMetered = false

    companion object {
        private const val LOW_BANDWIDTH_BPS = 500_000L
        private const val RECOVERY_BANDWIDTH_BPS = 800_000L
        private const val CONSECUTIVE_THRESHOLD = 3
    }

    fun start(scope: CoroutineScope) {
        job?.cancel()

        job = scope.launch {

            val throttles = combine(
                observeNetworkThrottle(),
                observeBatteryThrottle()
            ) { nt, bt -> nt || bt }

            val systemSignals = combine(
                resourcesMonitor.memoryTrimLevel,
                resourcesMonitor.isAppVisible,
                resourcesMonitor.isInteractive
            ) { trim, visible, interactive -> Triple(trim, visible, interactive) }

            combine(
                throttles,
                observeBandwidth(),
                systemSignals,
                batteryManager.observeBatteryState()
            ) { throttle, bandwidth, signals, battery ->
                val (memoryTrim, isVisible, isInteractive) = signals
                val isMetered = isNetworkMetered()

                // Detection: WiFi -> Cellular transition
                if (!wasMetered && isMetered) {
                    _meteredConnectionDetected.tryEmit(Unit)
                }
                wasMetered = isMetered

                // Update detection logic
                if (bandwidth < LOW_BANDWIDTH_BPS) {
                    consecutiveLowBandwidthCount++
                } else if (bandwidth > RECOVERY_BANDWIDTH_BPS) {
                    consecutiveLowBandwidthCount = 0
                }

                if (consecutiveLowBandwidthCount >= CONSECUTIVE_THRESHOLD) {
                    isSustainedLowBandwidth = true
                } else if (consecutiveLowBandwidthCount == 0) {
                    isSustainedLowBandwidth = false
                }

                EnvironmentState(
                    isMetered = isMetered,
                    shouldThrottlePerformance = throttle,
                    estimatedThroughputBps = bandwidth,
                    recentStallCount = 0,
                    memoryTrimLevel = memoryTrim,
                    isAppVisible = isVisible,
                    isInteractive = isInteractive,
                    isSustainedLowBandwidth = isSustainedLowBandwidth,
                    thermalStatus = battery.thermalStatus
                )
            }
                .distinctUntilChanged()
                .collect { state ->
                    _environment.value = state
                }
        }
    }

    /**
     * Maps network state into a simple throttle signal.
     * We intentionally do NOT expose raw network model here to keep this layer stable.
     */
    private fun observeNetworkThrottle(): Flow<Boolean> {
        return networkStateProvider.observe()
            .map { state ->
                state is NetworkState.PoorConnection ||
                        state is NetworkState.NoInternet
            }
            .distinctUntilChanged()
    }

    /**
     * Battery throttling signal is already domain-correct from IBatteryManager.
     */
    private fun observeBatteryThrottle(): Flow<Boolean> {
        return batteryManager.observeBatteryState()
            .map { it is BatteryState.Throttled }
            .distinctUntilChanged()
    }

    /**
     * Bandwidth estimation from Media3 engine.
     * Conflated to avoid UI / playback spam updates.
     */
    private fun observeBandwidth(): Flow<Long> = callbackFlow {

        val listener = BandwidthMeter.EventListener { _, _, _ ->
            trySend(bandwidthMeter.bitrateEstimate)
        }

        val handler = Handler(Looper.getMainLooper())

        bandwidthMeter.addEventListener(handler, listener)

        trySend(bandwidthMeter.bitrateEstimate)

        awaitClose {
            bandwidthMeter.removeEventListener(listener)
        }

    }.conflate()
        .distinctUntilChanged()

    /**
     * OS-level metered network detection (not derived from state model).
     * This avoids coupling network quality logic with system billing state.
     */
    private fun isNetworkMetered(): Boolean {
        return connectivityManager.isActiveNetworkMetered
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}

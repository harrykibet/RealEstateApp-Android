package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.interfaces.INetworkUtils
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.utils.DynamicBitrateController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class EnvironmentCoordinator @Inject constructor(
    private val networkUtils: INetworkUtils,
    private val batteryManager: IBatteryManager,
    private val dynamicBitrateController: DynamicBitrateController
) {

    fun observe(
        scope: CoroutineScope,
        activePlayerProvider: () -> Pair<ExoPlayer, MediaType>?
    ) {
        scope.launch {
            combine(
                networkUtils.observeNetworkStatus(),
                batteryManager.observeBatteryState()
            ) { network, battery ->
                network to battery
            }
                .distinctUntilChanged()
                .collect {
                    val active = activePlayerProvider() ?: return@collect
                    dynamicBitrateController.onEnvironmentChanged(
                        active.first,
                        active.second
                    )
                }
        }
    }

    fun attach(player: ExoPlayer, mediaType: MediaType) {
        dynamicBitrateController.attach(player, mediaType)
    }

    fun detach(player: ExoPlayer) {
        dynamicBitrateController.detach(player)
    }
}
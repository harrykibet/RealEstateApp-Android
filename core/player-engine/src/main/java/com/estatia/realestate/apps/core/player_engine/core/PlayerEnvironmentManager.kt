package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.player_engine.configuration.DynamicBitrateController
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.PlayerDispatcher
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates player pool adjustments and bitrate strategy based on environment changes.
 */
@UnstableApi
@Singleton
internal class PlayerEnvironmentManager @Inject constructor(
    private val pool: PlayerPool,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val sizingPolicy: IPlayerPoolSizingPolicy,
    private val dynamicBitrateController: DynamicBitrateController,
    @param:EngineScope private val engineScope: CoroutineScope,
    @param:PlayerDispatcher private val playerDispatcher: CoroutineDispatcher
) {
    private var activeMediaId: String? = null

    fun start() {
        environmentCoordinator.start(engineScope)
        engineScope.launch(playerDispatcher) {
            environmentCoordinator.environment.collect { env ->
                val newSize = sizingPolicy.calculateMaxPoolSize()
                pool.updateMaxPoolSize(newSize, activeMediaId)
                pool.forEachPlayer { player, mediaType ->
                    dynamicBitrateController.apply(player, mediaType, env)
                }
            }
        }
    }

    fun stop() {
        environmentCoordinator.stop()
    }

    fun updateActiveMediaId(mediaId: String?) {
        activeMediaId = mediaId
    }
}

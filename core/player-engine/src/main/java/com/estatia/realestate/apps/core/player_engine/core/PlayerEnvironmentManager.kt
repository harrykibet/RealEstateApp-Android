package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.player_engine.configuration.DynamicBitrateController
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.PlayerDispatcher
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

/**
 * Orchestrates player pool adjustments and bitrate strategy based on environment changes.
 */
@UnstableApi
@Singleton
class PlayerEnvironmentManager @Inject constructor(
    private val pool: PlayerPool,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val sizingPolicy: IPlayerPoolSizingPolicy,
    private val dynamicBitrateController: DynamicBitrateController,
    @param:EngineScope private val engineScope: CoroutineScope,
    @param:PlayerDispatcher private val playerDispatcher: CoroutineDispatcher
) {
    private var activeMediaId: String? = null
    private var graceJob: Job? = null
    private var wasEffectivelyBackgrounded = false

    fun start(
        onAppBackgrounded: suspend () -> Unit,
        onAppForegrounded: suspend () -> Unit
    ) {
        environmentCoordinator.start(engineScope)
        engineScope.launch(playerDispatcher) {
            environmentCoordinator.environment.collect { env ->
                // 1. React to app visibility and interactivity changes
                val isEffectivelyBackgrounded = !env.isAppVisible || !env.isInteractive
                
                if (isEffectivelyBackgrounded) {
                    // Immediate action: Stop playback and focus
                    onAppBackgrounded()
                    
                    // Delayed action: Reclaim hardware resources after 60s
                    if (graceJob == null) {
                        graceJob = launch {
                            delay(60000.milliseconds)
                            pool.releaseAll()
                        }
                    }
                    wasEffectivelyBackgrounded = true
                    return@collect
                } else {
                    // User returned: preserve warm resources or notify resume
                    graceJob?.cancel()
                    graceJob = null
                    
                    if (wasEffectivelyBackgrounded) {
                        onAppForegrounded()
                    }
                    wasEffectivelyBackgrounded = false
                }

                // 2. Dynamic pool sizing based on environment (memory, battery, etc.)
                val newSize = sizingPolicy.calculateMaxPoolSize(env)
                pool.updateMaxPoolSize(newSize, activeMediaId)
                
                // 3. Update bitrate for all active players
                pool.forEachPlayer { player, mediaType ->
                    val bufferSeconds = (player.bufferedPosition - player.currentPosition) / 1000.0
                    dynamicBitrateController.apply(
                        player = player,
                        mediaType = mediaType,
                        environment = env,
                        bufferSeconds = bufferSeconds
                    )
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

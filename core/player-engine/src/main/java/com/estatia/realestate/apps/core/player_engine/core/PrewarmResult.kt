package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Result of a player prewarm operation.
 */
// Justification: Required for low-level Media3 playback orchestration.
@UnstableApi
@Contract
sealed interface PrewarmResult {
    /**
     * Prewarm succeeded, and the player is ready in the pool.
     */
data class Success(val managed: ManagedPlayer) : PrewarmResult

    /**
     * Prewarm was rejected due to pool capacity constraints.
     */
data object Rejected : PrewarmResult

    /**
     * Prewarm failed due to an exception.
     */
data class Failure(val throwable: Throwable) : PrewarmResult
}

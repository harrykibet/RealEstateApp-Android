package com.estatia.realestate.apps.core.player_engine.utils

/**
 * Policy for determining the maximum number of player instances to keep in the pool.
 * Implementation may vary based on device performance, memory state, or network conditions.
 */
interface IPlayerPoolSizingPolicy {
    /**
     * Calculates the target maximum pool size based on current environment state.
     */
    fun calculateMaxPoolSize(environmentState: EnvironmentState): Int
}

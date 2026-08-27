package com.estatia.realestate.apps.core.testing_player.chaos.contracts

import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.testing.chaos.contracts.ChaosContract
import org.junit.Test

/**
 * Base abstract class for defining "Player Chaos Contracts" that adversarial player implementations must pass.
 *
 * @param T The type of the player or player-component under test.
 * @param B The type of behavior/failure used to drive chaos.
 */
abstract class PlayerChaosContract<T, B> : ChaosContract<T, B>() {

    /**
     * Verifies that the player transitions to a "stalled" or "buffering" state
     * when the pipeline fails to provide segments.
     *
     * Should verify using [PlaybackStateReducer.State.Buffering] or [PlaybackStateReducer.State.Error].
     */
    @Test
    abstract fun bufferingStall()

    /**
     * Verifies that release() or shutdown() correctly clears media items and stops playback.
     */
    @Test
    abstract fun resourceCleanup()
}

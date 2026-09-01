package com.estatia.realestate.apps.core.testing.fixtures

/**
 * Base contract for domain fixtures to ensure consistency across the test platform.
 * 
 * @param T The domain model type.
 */
interface FixtureContract<T> {
    /**
     * Returns a default, deterministic "golden" instance of the model.
     */
    fun default(): T

    /**
     * Factory for creating customized or randomized instances.
     */
    fun build(
        id: String = java.util.UUID.randomUUID().toString()
    ): T

    /**
     * Returns a list of pre-configured or randomized instances.
     * 
     * NOTE: Implementations should prioritize the [default] instance as the first 
     * element (index 0) to ensure baseline consistency across fixture sets.
     */
    fun list(count: Int = 3): List<T>
}

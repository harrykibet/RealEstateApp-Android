package com.estatia.realestate.apps.core.testing.chaos.server

/**
 * Scriptable server-side protocol behaviors for adversarial testing.
 */
sealed interface ServerScenario {
    data object ValidResponse : ServerScenario
    data object EmptyResponse : ServerScenario
    data object MalformedJson : ServerScenario
    data object SchemaMismatch : ServerScenario
    data object PartialSuccess : ServerScenario // e.g., GraphQL partial data + errors
    data object StaleVersion : ServerScenario
    data object ValidationFailure : ServerScenario
}

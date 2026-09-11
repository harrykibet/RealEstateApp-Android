package com.estatia.realestate.apps.core.architecture.annotations

import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * Data Roles: State containers and model definitions.
 */
object Data {
    /**
     * Marks a data class or sealed class as a Pure Domain Model.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class DomainModel

    /**
     * Marks a data class as an Infrastructure Entity (DB/Network).
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class EntityModel

    /**
     * Marks a data class or sealed class as a formal UI State container.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class UiState

    /**
     * Marks a component as a Battery State container.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class BatteryState

    /**
     * Marks a component as a Network State container.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class NetworkState

    /**
     * Marks a component as an Environment State container.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class EnvironmentState

    /**
     * Marks a component as an Analytics Event/State container.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class AnalyticsState

    /**
     * Marks a component as an Authentication State container.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthState

    /**
     * Marks a component as a Media Player State container.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class PlayerState

    /**
     * Marks a component as a UI Action or Intent container.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class UiAction

    /**
     * Marks a component as a UI Event or Effect container.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class UiEvent
}

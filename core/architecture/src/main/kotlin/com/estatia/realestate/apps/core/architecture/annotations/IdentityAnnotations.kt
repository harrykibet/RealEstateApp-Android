package com.estatia.realestate.apps.core.architecture.annotations

import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * Identity Roles: Structural components with defined architectural responsibilities.
 */
object Identity {
    /**
     * Marks a class as a Repository in the Data layer.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Repository

    /**
     * Marks a class as a Service (Domain or Infrastructure).
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Service

    /**
     * Marks a class as a UseCase in the Domain layer.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class UseCase

    /**
     * Marks a class as a ViewModel in the Presentation layer.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ViewModelMarker

    /**
     * Marks a class as an Orchestration Coordinator.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class Coordinator

    /**
     * Marks a class as a Functional Manager.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class Manager

    /**
     * Marks a class or interface as a Data Source (Network or Local).
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class DataSource

    /**
     * Marks an interface as a formal Architectural Contract.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class Contract

    /**
     * Marks a class as an Application Entry Point.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class AppEntryPoint
}

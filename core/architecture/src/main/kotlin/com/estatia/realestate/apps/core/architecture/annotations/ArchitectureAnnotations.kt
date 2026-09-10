package com.estatia.realestate.apps.core.architecture.annotations

import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * Marks a class as a Repository in the Data layer.
 * Enforced by KSP to ensure all public methods return Result types (LAW-009).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Repository

/**
 * Marks a class as a Service (Domain or Infrastructure).
 * Enforced by KSP to ensure all public methods return Result types (LAW-009).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Service

/**
 * Marks a class as a UseCase in the Domain layer.
 * Enforced by KSP to ensure all public methods return Result types (LAW-009).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class UseCase

/**
 * Marks a class as a ViewModel in the Presentation layer.
 * Enforced by KSP to ensure all public properties are read-only abstractions (LAW-016).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ViewModelMarker

/**
 * Marks a data class or sealed class as a formal UI State container.
 * Enforced by KSP to ensure ViewModels follow the Single Source of Truth pattern (LAW-018).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class UiState

/**
 * Explicitly allows a concrete type as a dependency in architectural components.
 * Use this to document and authorize unavoidable infrastructure dependencies.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class AllowedArchitectureDependency(
    val reason: String
)

/**
 * Marks a class as an Orchestration Coordinator.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@Helper
annotation class Coordinator

/**
 * Marks a class as a long-lived Functional Manager.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@Helper
annotation class Manager

/**
 * Marks a class as a Functional Helper.
 * Used for internal logic, formatters, or math utilities.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@Helper
annotation class Helper

/**
 * Marks a class as a Data Source (Network or Local).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@Helper
annotation class DataSource

/**
 * Marks a class as an Error Mapper.
 * Responsible for translating infrastructure exceptions into Domain results.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@Helper
annotation class ErrorMapper

/**
 * Marks a data class or sealed class as a Pure Domain Model.
 * Enforced to remain free of platform/framework imports (LAW-032).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@Helper
annotation class DomainModel

/**
 * Marks a data class as an Infrastructure Entity (DB/Network).
 * Must not leak into Domain or Presentation layers.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@Helper
annotation class EntityModel

/**
 * Marks a class as an Application Entry Point.
 * Used for Activities, Fragments (that aren't ViewModels), or Application classes.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@Helper
annotation class AppEntryPoint

/**
 * Marks an interface as a formal Architectural Contract.
 * Used to define the behavior of Repositories, Services, and UseCases.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Contract

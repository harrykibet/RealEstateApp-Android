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
 * Used exclusively for state owned by a ViewModel and projected to the UI.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class UiState

/**
 * Marks a component as a Battery State container.
 * Used for system-level battery and thermal status tracking.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class BatteryState

/**
 * Marks a component as a Network State container.
 * Used for system-level connectivity and bandwidth tracking.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class NetworkState

/**
 * Marks a component as an Environment State container.
 * Used for tracking device-wide conditions like thermal, memory, and visibility.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class EnvironmentState

/**
 * Marks a component as an Analytics Event/State container.
 * Used for structured tracking and observability data.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class AnalyticsState

/**
 * Marks a component as an Authentication State container.
 * Used for login, registration, and MFA verification flows.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class AuthState

/**
 * Marks a component as a Media Player State container.
 * Used for tracking playback progress, buffering, and hardware status.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class PlayerState

/**
 * Marks a component as a UI Action or Intent container.
 * Used for transient user interactions that trigger state changes.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class UiAction

/**
 * Marks a component as a UI Event or Effect container.
 * Used for transient one-off messages (e.g. snackbars, navigation) from ViewModel to UI.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class UiEvent

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
annotation class Coordinator

/**
 * Marks a class as a Functional Manager.
 * Responsible for managing high-risk system resources or multi-threaded state.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Manager

/**
 * Marks a class as a Passive Helper.
 * RESERVED for static constant holders, metadata registries, and logic-less objects.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Helper

/**
 * Marks a class as an Infrastructure Utility.
 * Used for pure functional logic, extensions, and math utilities that carry logic but no state.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Utility

/**
 * Marks a class as a Data Mapper.
 * Responsible for pure data-to-data transformation (Domain ↔ Entity).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Mapper

/**
 * Marks a class as a Strategic Policy.
 * Used for algorithmic rules like Retry strategies, Cache eviction, or Bitrate scaling.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Policy

/**
 * Marks a class as a Foundation Abstraction.
 * Used for system-level interfaces like Clocks, FileSystems, and Locale providers.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Foundation

/**
 * Marks a class as a low-level UI Primitive.
 * Used for custom gesture state, scrollbar logic, or canvas primitives.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class UiPrimitive

/**
 * Marks a class or interface as a Data Source (Network or Local).
 * Allowed to speak in infrastructure entities (LAW-008 exemption).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class DataSource

/**
 * Marks a class as an Error Mapper.
 * Responsible for translating infrastructure exceptions into Domain results.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class ErrorMapper

/**
 * Marks a data class or sealed class as a Pure Domain Model.
 * Enforced to remain free of platform/framework imports (LAW-032).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class DomainModel

/**
 * Marks a data class as an Infrastructure Entity (DB/Network).
 * Must not leak into Domain or Presentation layers.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class EntityModel

/**
 * Marks a class as an Application Entry Point.
 * Used for Activities, Fragments (that aren't ViewModels), or Application classes.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class AppEntryPoint

/**
 * Marks an interface as a formal Architectural Contract.
 * Used to define the behavior of pure Domain/Business components.
 * Enforced to remain free of infrastructure leakage (LAW-008).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Contract

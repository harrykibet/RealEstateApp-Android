package com.estatia.realestate.apps.core.common.annotations

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

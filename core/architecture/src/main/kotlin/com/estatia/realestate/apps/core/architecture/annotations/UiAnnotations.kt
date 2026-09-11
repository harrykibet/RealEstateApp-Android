package com.estatia.realestate.apps.core.architecture.annotations

import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * UI Roles: Composable functions and presentation primitives.
 */
object Ui {
    /**
     * Marks a Composable function as a top-level UI Screen.
     */
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.BINARY)
    annotation class UiScreen

    /**
     * Marks a Composable function as a reusable UI Component.
     */
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.BINARY)
    annotation class UiComponent

    /**
     * Marks a Composable function as a stateless UI Primitive (Design System element).
     */
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.BINARY)
    annotation class UiPrimitiveFunction

    /**
     * Marks a Composable function as a Navigation Route.
     */
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.BINARY)
    annotation class UiRoute

    /**
     * Marks a class as a low-level UI Primitive (State/Logic).
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class UiPrimitive
}

package com.estatia.realestate.apps.core.architecture.annotations

import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * Logic Roles: Functional components responsible for behavioral logic and transformation.
 */
object Logic {
    /**
     * Marks a class as an Infrastructure Utility.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class Utility

    /**
     * Marks a class as a Data Mapper.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class Mapper

    /**
     * Marks a class as a Strategic Policy.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class Policy

    /**
     * Marks a class as a Foundation Abstraction.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class Foundation

    /**
     * Marks a class as a Passive Helper (Constants only).
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class Helper

    /**
     * Marks a class as an Error Mapper.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class ErrorMapper
}

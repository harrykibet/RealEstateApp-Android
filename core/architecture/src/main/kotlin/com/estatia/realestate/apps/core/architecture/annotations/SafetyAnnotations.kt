package com.estatia.realestate.apps.core.architecture.annotations

import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * Safety & Suppression: Manual overrides for architectural laws.
 */
object Safety {
    /**
     * Explicitly allows a concrete type as a dependency in architectural components.
     * Use this to document and authorize unavoidable infrastructure dependencies.
     */
    @Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.SOURCE)
    annotation class AllowedArchitectureDependency(
        val reason: String
    )
}

package com.estatia.realestate.apps.core.architecture

/**
 * Defines structural requirements that a component must satisfy to claim a specific architectural identity.
 */
data class LawInvariant(
    val pathContains: String? = null,
    val mustInheritFrom: String? = null,
    val mustBeInterface: Boolean = false,
    val mustBeData: Boolean = false,
    val mustBeDataSealedOrValue: Boolean = false,
    val mustBeComposable: Boolean = false
)

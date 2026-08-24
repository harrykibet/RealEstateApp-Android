package com.estatia.realestate.apps.core.common.misc

// Global constants and objects
/**
 * Central registry for project-wide hardcoded values.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Prevent string-literal duplication across modules.
 * - Immutability: Compile-time constants.
 */
object Consts {
    const val EMPTY_STRING = ""
    const val PROPERTY = "property"
    const val USER_AUTHENTICATED = "USER_AUTHENTICATED"
    const val DATE_FORMAT = "dd MMM yyyy, hh:mm a"
    const val PRIORITY_NORMAL = 0
}

package com.estatia.realestate.apps.core.common.interfaces

/**
 * Abstraction for build-time environment flags.
 */
interface BuildEnvironment {
    /**
     * Whether the current build is a debug build.
     */
    val isDebug: Boolean
}

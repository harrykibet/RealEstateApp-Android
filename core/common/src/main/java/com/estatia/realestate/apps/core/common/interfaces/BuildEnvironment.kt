package com.estatia.realestate.apps.core.common.interfaces
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Abstraction for build-time environment flags.
 */
@Contract
interface BuildEnvironment {
    /**
     * Whether the current build is a debug build.
     */
    val isDebug: Boolean
}

package com.estatia.realestate.apps.core.testing.chaos.models

/**
 * Common failure model for chaos testing across all infrastructure domains.
 * This is used to script predictable failures in adversarial implementations.
 */
sealed interface TestFailure {

    data object Offline : TestFailure

    data object Timeout : TestFailure

    data class Http(val code: Int) : TestFailure

    data object ConnectionReset : TestFailure

    data object DnsFailure : TestFailure

    data object MalformedResponse : TestFailure

    data object Unauthorized : TestFailure

    data object RateLimited : TestFailure

    data object DatabaseLocked : TestFailure

    data object DiskFull : TestFailure

    data object PermissionDenied : TestFailure
}

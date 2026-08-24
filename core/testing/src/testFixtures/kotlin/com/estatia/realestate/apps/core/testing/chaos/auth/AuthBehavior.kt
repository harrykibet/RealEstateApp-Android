package com.estatia.realestate.apps.core.testing.chaos.auth

/**
 * Represents scriptable authentication behaviors for adversarial testing.
 */
sealed interface AuthBehavior {
    data object Authenticated : AuthBehavior
    data object LoggedOut : AuthBehavior
    data object TokenExpired : AuthBehavior
    data object TokenRevoked : AuthBehavior
    data object RefreshFails : AuthBehavior
    data object RefreshTimeout : AuthBehavior
    data object MultipleRefreshRequests : AuthBehavior
    data object LogoutDuringRefresh : AuthBehavior
    data object LogoutDuringRequest : AuthBehavior
    data object AccountDisabled : AuthBehavior
    data object PermissionsRevoked : AuthBehavior
    data object ProcessDeathDuringAuth : AuthBehavior
    data object NetworkLostDuringRefresh : AuthBehavior
    data object SessionRestorationFailure : AuthBehavior
}

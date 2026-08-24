package com.estatia.realestate.apps.core.testing.chaos.auth

/**
 * Represents scriptable authentication behaviors for adversarial testing.
 */
sealed interface AuthBehavior {
    data object Authenticated : AuthBehavior
    data object LoggedOut : AuthBehavior
    data object TokenExpired : AuthBehavior
    data object RefreshFails : AuthBehavior
    data object RefreshTimeout : AuthBehavior
    data object AccountDisabled : AuthBehavior
}

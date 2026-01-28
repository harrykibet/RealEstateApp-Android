package com.estatia.realestate.apps.feature.auth.events

sealed interface SignUpEvent {
    data object Success : SignUpEvent
}

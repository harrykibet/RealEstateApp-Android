package com.estatia.realestate.apps.feature.comments.actions

sealed interface CommentsAction {
    data class Load(val propertyId: String) : CommentsAction
    data class InputChanged(val value: String) : CommentsAction
    object SendComment : CommentsAction
    object Refresh : CommentsAction
}
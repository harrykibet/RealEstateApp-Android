package com.estatia.realestate.apps.feature.comments.actions

import com.estatia.realestate.apps.core.architecture.annotations.Helper

sealed interface CommentsAction {
@Helper
    data class Load(val propertyId: String) : CommentsAction
    data class InputChanged(val value: String) : CommentsAction
@Helper
    object SendComment : CommentsAction
@Helper
    object Refresh : CommentsAction
}

package com.estatia.realestate.apps.feature.comments.actions

import com.estatia.realestate.apps.core.architecture.annotations.UiAction
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface CommentsAction {
@UiAction
    data class Load(val propertyId: String) : CommentsAction
@UiAction
    data class InputChanged(val value: String) : CommentsAction
@UiAction
    object SendComment : CommentsAction
@UiAction
    object Refresh : CommentsAction
}

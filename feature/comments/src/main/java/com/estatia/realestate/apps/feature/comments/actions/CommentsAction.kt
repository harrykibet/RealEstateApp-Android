package com.estatia.realestate.apps.feature.comments.actions

import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface CommentsAction {
@Helper
    data class Load(val propertyId: String) : CommentsAction
@Helper
    data class InputChanged(val value: String) : CommentsAction
@Helper
    object SendComment : CommentsAction
@Helper
    object Refresh : CommentsAction
}

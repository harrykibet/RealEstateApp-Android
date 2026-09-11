package com.estatia.realestate.apps.feature.comments.events
import com.estatia.realestate.apps.core.architecture.annotations.Data.UiEvent
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

@Contract
sealed interface CommentsEvent {
@UiEvent
    data class ShowMessage(val message: String) : CommentsEvent
}

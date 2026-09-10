package com.estatia.realestate.apps.feature.comments.events
import com.estatia.realestate.apps.core.architecture.annotations.Helper

sealed interface CommentsEvent {
@Helper
    data class ShowMessage(val message: String) : CommentsEvent
}

package com.estatia.realestate.apps.feature.comments.events

sealed interface CommentsEvent {
    data class ShowMessage(val message: String) : CommentsEvent
}
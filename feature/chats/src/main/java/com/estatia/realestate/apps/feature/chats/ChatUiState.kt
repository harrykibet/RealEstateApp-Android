package com.estatia.realestate.apps.feature.chats

import com.estatia.realestate.apps.core.model.feature.Chat
import com.estatia.realestate.apps.core.model.feature.ChatUser
import com.estatia.realestate.apps.core.architecture.annotations.Data.UiState
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

@Contract
sealed interface ChatUiState {
@UiState
    data object Loading : ChatUiState
    
@UiState
    data class Success(
        val activeUsers: List<ChatUser>,
        val chats: List<Chat>
    ) : ChatUiState
    
@UiState
    data class Error(val message: String) : ChatUiState
}

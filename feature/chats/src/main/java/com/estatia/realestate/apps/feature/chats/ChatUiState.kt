package com.estatia.realestate.apps.feature.chats

import com.estatia.realestate.apps.core.model.feature.Chat
import com.estatia.realestate.apps.core.model.feature.ChatUser

sealed interface ChatUiState {
    data object Loading : ChatUiState
    
    data class Success(
        val activeUsers: List<ChatUser>,
        val chats: List<Chat>
    ) : ChatUiState
    
    data class Error(val message: String) : ChatUiState
}

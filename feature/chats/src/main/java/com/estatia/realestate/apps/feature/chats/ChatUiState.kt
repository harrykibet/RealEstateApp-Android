package com.estatia.realestate.apps.feature.chats

import com.estatia.realestate.apps.core.model.feature.Chat
import com.estatia.realestate.apps.core.model.feature.ChatUser
import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface ChatUiState {
@Helper
    data object Loading : ChatUiState
    
@Helper
    data class Success(
        val activeUsers: List<ChatUser>,
        val chats: List<Chat>
    ) : ChatUiState
    
@Helper
    data class Error(val message: String) : ChatUiState
}

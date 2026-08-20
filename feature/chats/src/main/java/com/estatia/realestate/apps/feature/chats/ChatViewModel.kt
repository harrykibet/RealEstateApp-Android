package com.estatia.realestate.apps.feature.chats

import androidx.lifecycle.ViewModel
import com.estatia.realestate.apps.core.model.feature.Chat
import com.estatia.realestate.apps.core.model.feature.ChatUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val now = Instant.fromEpochMilliseconds(System.currentTimeMillis())
        
        val activeUsers = listOf(
            ChatUser("1", "John Doe", null, true),
            ChatUser("2", "Jane Smith", null, true),
            ChatUser("3", "Agent K", null, true),
            ChatUser("4", "Real Estates", null, true),
            ChatUser("5", "Modern Living", null, true),
        )

        val chats = listOf(
            Chat(
                id = "c1",
                user = ChatUser("1", "John Doe", null, true),
                lastMessage = "Is the property still available?",
                lastMessageTimestamp = now.minus(5.minutes),
                unreadCount = 2
            ),
            Chat(
                id = "c2",
                user = ChatUser("2", "Jane Smith", null, true),
                lastMessage = "Thanks for the info!",
                lastMessageTimestamp = now.minus(30.minutes)
            ),
            Chat(
                id = "c3",
                user = ChatUser("4", "Real Estates", null, true),
                lastMessage = "We have new listings in Westlands.",
                lastMessageTimestamp = now.minus(2.minutes),
                unreadCount = 5
            )
        )

        _uiState.value = ChatUiState.Success(activeUsers, chats)
    }
}

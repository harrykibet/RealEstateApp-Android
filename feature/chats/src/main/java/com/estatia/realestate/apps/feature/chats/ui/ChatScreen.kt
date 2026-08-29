package com.estatia.realestate.apps.feature.chats.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.core.model.feature.Chat
import com.estatia.realestate.apps.core.model.feature.ChatUser
import com.estatia.realestate.apps.feature.chats.ChatUiState
import com.estatia.realestate.apps.feature.chats.ChatViewModel
import kotlin.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ChatsRoute(
    onChatClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChatScreen(
        uiState = uiState,
        onChatClick = onChatClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onChatClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    EstatiaText(
                        text = "Inbox",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = EstatiaIcons.Add, contentDescription = "New Message")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        when (uiState) {
            is ChatUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ChatUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        ActiveNowSection(users = uiState.activeUsers)
                    }
                    items(uiState.chats) { chat ->
                        ChatItem(chat = chat, onClick = { onChatClick(chat.id) })
                    }
                }
            }
            is ChatUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    EstatiaText(text = uiState.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun ActiveNowSection(users: List<ChatUser>) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        EstatiaText(
            text = "Active now",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(users) { user ->
                ActiveUserItem(user = user)
            }
        }
    }
}

@Composable
fun ActiveUserItem(user: ChatUser) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            UserAvatar(
                imageUrl = user.profilePictureUrl,
                size = 60.dp
            )
            if (user.isActive) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color.Green)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.Green)
                        )
                    }
                }
            }
        }
        EstatiaText(
            text = user.name.split(" ").first(),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            imageUrl = chat.user.profilePictureUrl,
            size = 56.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            EstatiaText(
                text = chat.user.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            EstatiaText(
                text = chat.lastMessage,
                fontSize = 14.sp,
                color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            EstatiaText(
                text = formatTimestamp(chat.lastMessageTimestamp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (chat.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    EstatiaText(
                        text = chat.unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun UserAvatar(imageUrl: String?, size: androidx.compose.ui.unit.Dp) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentScale = ContentScale.Crop
    )
}

private fun formatTimestamp(timestamp: Instant): String {
    val epochMillis = timestamp.toEpochMilliseconds()
    val javaInstant = java.time.Instant.ofEpochMilli(epochMillis)
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())
    return formatter.format(javaInstant)
}

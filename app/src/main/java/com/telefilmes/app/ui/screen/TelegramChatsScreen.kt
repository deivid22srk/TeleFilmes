package com.telefilmes.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.telefilmes.app.R
import com.telefilmes.app.telegram.TelegramAuthState
import com.telefilmes.app.telegram.TelegramChat
import com.telefilmes.app.ui.viewmodel.TelegramViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramChatsScreen(
    viewModel: TelegramViewModel,
    onBackClick: () -> Unit,
    onChatClick: (Long) -> Unit,
    onLoginRequired: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val chats by viewModel.chats.collectAsState()
    
    LaunchedEffect(authState) {
        when (authState) {
            is TelegramAuthState.Idle,
            is TelegramAuthState.WaitingForPhoneNumber -> {
                onLoginRequired()
            }
            is TelegramAuthState.Authenticated -> {
                viewModel.loadChats()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.chats_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (chats.isEmpty()) {
            EmptyChatsContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            ChatsListContent(
                chats = chats,
                onChatClick = onChatClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}

@Composable
private fun EmptyChatsContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.loading_chats),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ChatsListContent(
    chats: List<TelegramChat>,
    onChatClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(chats, key = { it.id }) { chat ->
            ChatListItem(
                chat = chat,
                onClick = { onChatClick(chat.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatListItem(
    chat: TelegramChat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                chat.lastMessage?.let { message ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

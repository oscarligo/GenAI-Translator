package com.example.translator.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.translator.di.AppGraph
import com.example.translator.domain.model.Conversation
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onOpenConversation: (String) -> Unit,
    modifier: Modifier = Modifier,
    vm: HomeViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                AppGraph.getConversationsFlow,
                AppGraph.createConversation
            ) as T
        }
    })
) {
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    val id = vm.createAndReturnId()
                    onOpenConversation(id)
                }
            }) { Icon(Icons.Default.Add, contentDescription = "Nueva conversación") }
        }
    ) { inner ->
        Box(modifier.padding(inner)) {
            if (state.isLoading && state.conversations.isEmpty()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            if (state.error != null) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            }
            ConversationList(state.conversations, onOpenConversation)
        }
    }
}

@Composable
private fun ConversationList(conversations: List<Conversation>, onOpen: (String) -> Unit) {
    if (conversations.isEmpty()) {
        Box(Modifier.fillMaxSize()) {
            Text(
                "No hay conversaciones. Crea una nueva con el botón +",
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(conversations, key = { it.id }) { conv ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().clickable { onOpen(conv.id) }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(conv.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Text("${conv.messageCount} mensajes", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}


package com.example.translator.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.translator.di.AppGraph
import com.example.translator.domain.model.Conversation
import com.example.translator.ui.common.getLanguageOrDefault
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
                AppGraph.createConversation,
                AppGraph.deleteConversation,
                AppGraph.renameConversation
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
            }) { Icon(Icons.Filled.Add, contentDescription = "Nueva conversación") }
        }
    ) { inner ->
        Box(modifier.padding(inner)) {
            if (state.isLoading && state.conversations.isEmpty()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            if (state.error != null) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            }
            ConversationList(state.conversations, onOpenConversation, onRename = { id, title -> vm.renameConversation(id, title) }, onDelete = { id -> vm.deleteConversation(id) })
        }
    }
}

@Composable
private fun ConversationList(
    conversations: List<Conversation>,
    onOpen: (String) -> Unit,
    onRename: (String, String) -> Unit,
    onDelete: (String) -> Unit
) {
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
            var menuExpanded by remember { mutableStateOf(false) }
            var showRename by remember { mutableStateOf(false) }
            var newTitle by remember(conv.id) { mutableStateOf(conv.title) }
            var showDeleteConfirm by remember { mutableStateOf(false) }

            if (showRename) {
                AlertDialog(
                    onDismissRequest = { showRename = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val title = newTitle.trim().ifEmpty { conv.title }
                            onRename(conv.id, title)
                            showRename = false
                        }) { Text("Guardar") }
                    },
                    dismissButton = { TextButton(onClick = { showRename = false }) { Text("Cancelar") } },
                    title = { Text("Renombrar conversación") },
                    text = {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            singleLine = true,
                            label = { Text("Título") }
                        )
                    }
                )
            }

            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    confirmButton = {
                        TextButton(onClick = {
                            onDelete(conv.id)
                            showDeleteConfirm = false
                        }) { Text("Eliminar") }
                    },
                    dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") } },
                    title = { Text("Eliminar conversación") },
                    text = { Text("¿Seguro que deseas eliminar esta conversación?") }
                )
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth().clickable { onOpen(conv.id) }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            conv.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        val lang = getLanguageOrDefault(conv.targetLang)
                        AssistChip(onClick = {}, enabled = false, label = { Text(text = "${lang.flag} ${lang.code.uppercase()}") })
                        IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones") }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Renombrar") },
                                onClick = { menuExpanded = false; newTitle = conv.title; showRename = true }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar") },
                                onClick = { menuExpanded = false; showDeleteConfirm = true }
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("${conv.messageCount} mensajes", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

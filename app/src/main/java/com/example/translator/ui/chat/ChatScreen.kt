package com.example.translator.ui.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.translator.di.AppGraph
import com.example.translator.ui.components.MessageItem
import com.example.translator.ui.common.getLanguageOrDefault
import com.example.translator.ui.common.supportedLanguages
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatScreen(
    conversationId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: ChatViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(
                AppGraph.getMessagesFlow,
                AppGraph.addUserMessage,
                AppGraph.getConversationFlow,
                AppGraph.setTargetLanguage,
                AppGraph.translateWithOpenAi,
                AppGraph.addAssistantMessage
            ) as T
        }
    })
) {
    val state by vm.state.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(conversationId) { vm.onEvent(ChatContract.Event.Load(conversationId)) }

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                is ChatContract.Effect.ScrollToBottom -> {
                    scope.launch { listState.animateScrollToItem(maxOf(state.messages.lastIndex, 0)) }
                }
                is ChatContract.Effect.ShowSnack -> { /* no-op placeholder */ }
            }
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        vm.onEvent(ChatContract.Event.PickImage(uri?.toString()))
    }

    var langMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                },
                actions = {
                    val lang = getLanguageOrDefault(state.targetLang)
                    Box {
                        AssistChip(onClick = { langMenuExpanded = true }, label = { Text(text = "${lang.flag} ${lang.code.uppercase()}") })
                        DropdownMenu(expanded = langMenuExpanded, onDismissRequest = { langMenuExpanded = false }) {
                            supportedLanguages.forEach { l ->
                                DropdownMenuItem(
                                    text = { Text("${l.flag} ${l.name} (${l.code.uppercase()})") },
                                    onClick = {
                                        langMenuExpanded = false
                                        if (!l.code.equals(state.targetLang, ignoreCase = true)) {
                                            vm.onEvent(ChatContract.Event.ChangeTargetLanguage(l.code))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { inner ->
        Column(modifier.padding(inner).fillMaxSize()) {
            if (state.isLoading && state.messages.isEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.messages, key = { it.id }) { msg ->
                    MessageItem(message = msg)
                }
            }

            // Mostrar resultado estructurado de la traducción
            state.translationResult?.let { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(text = "Traducción: ${result.translation}", style = MaterialTheme.typography.titleMedium)
                        if (result.usage.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(text = "Uso: ${result.usage}", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (result.notes.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(text = "Notas: ${result.notes}", style = MaterialTheme.typography.bodySmall)
                        }
                        if (result.synonyms.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(text = "Sinónimos: ${result.synonyms.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            if (state.selectedImageUri != null) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { vm.onEvent(ChatContract.Event.ClearImage) },
                        label = { Text("Imagen seleccionada") }
                    )
                    Spacer(Modifier.weight(1f))
                    FilledIconButton(onClick = { vm.onEvent(ChatContract.Event.SendImage) }) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar imagen")
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.inputText,
                    onValueChange = { vm.onEvent(ChatContract.Event.OnInputChange(it)) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje…") }
                )
                IconButton(onClick = { pickImageLauncher.launch("image/*") }) {
                    Icon(Icons.Filled.Image, contentDescription = "Elegir imagen")
                }
                IconButton(onClick = { vm.onEvent(ChatContract.Event.SendText) }) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar texto")
                }
            }
        }
    }
}

package com.example.translator.ui.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.translator.di.AppGraph
import com.example.translator.ui.components.MessageItem
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
                AppGraph.addUserMessage
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") }
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

            if (state.selectedImageUri != null) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { vm.onEvent(ChatContract.Event.ClearImage) },
                        label = { Text("Imagen seleccionada") }
                    )
                    Spacer(Modifier.weight(1f))
                    FilledIconButton(onClick = { vm.onEvent(ChatContract.Event.SendImage) }) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar imagen")
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
                    Icon(Icons.Default.Image, contentDescription = "Elegir imagen")
                }
                IconButton(onClick = { vm.onEvent(ChatContract.Event.SendText) }) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar texto")
                }
            }
        }
    }
}

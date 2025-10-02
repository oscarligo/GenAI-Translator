package com.example.translator.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translator.domain.usecase.AddUserMessageUseCase
import com.example.translator.domain.usecase.GetMessagesFlowUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getMessages: GetMessagesFlowUseCase,
    private val addUserMessage: AddUserMessageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChatContract.State())
    val state: StateFlow<ChatContract.State> = _state.asStateFlow()

    private val _effects = Channel<ChatContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: ChatContract.Event) {
        when (event) {
            is ChatContract.Event.Load -> load(event.conversationId)
            is ChatContract.Event.OnInputChange -> _state.update { it.copy(inputText = event.value) }
            ChatContract.Event.SendText -> sendText()
            is ChatContract.Event.PickImage -> _state.update { it.copy(selectedImageUri = event.uri) }
            ChatContract.Event.SendImage -> sendImage()
            ChatContract.Event.ClearImage -> _state.update { it.copy(selectedImageUri = null) }
        }
    }

    private fun load(conversationId: String) {
        if (state.value.conversationId == conversationId && state.value.messages.isNotEmpty()) return
        _state.update { it.copy(conversationId = conversationId) }
        viewModelScope.launch {
            getMessages(conversationId)
                .onStart { _state.update { it.copy(isLoading = true, error = null) } }
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { list ->
                    _state.update { it.copy(isLoading = false, messages = list) }
                    _effects.send(ChatContract.Effect.ScrollToBottom)
                }
        }
    }

    private fun sendText() {
        val text = state.value.inputText.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            addUserMessage(state.value.conversationId, text = text)
            _state.update { it.copy(inputText = "") }
            _effects.send(ChatContract.Effect.ScrollToBottom)
        }
    }

    private fun sendImage() {
        val uri = state.value.selectedImageUri ?: return
        viewModelScope.launch {
            addUserMessage(state.value.conversationId, imageUri = uri)
            _state.update { it.copy(selectedImageUri = null) }
            _effects.send(ChatContract.Effect.ScrollToBottom)
        }
    }
}

package com.example.translator.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translator.domain.usecase.AddUserMessageUseCase
import com.example.translator.domain.usecase.GetMessagesFlowUseCase
import com.example.translator.domain.usecase.GetConversationFlowUseCase
import com.example.translator.domain.usecase.SetTargetLanguageUseCase
import com.example.translator.domain.usecase.TranslateWithOpenAiUseCase
import com.example.translator.domain.usecase.AddAssistantMessageUseCase
import com.example.translator.ui.common.getUiLabelsFor
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
    private val addUserMessage: AddUserMessageUseCase,
    private val getConversationFlow: GetConversationFlowUseCase,
    private val setTargetLanguage: SetTargetLanguageUseCase,
    private val translateWithOpenAi: TranslateWithOpenAiUseCase,
    private val addAssistantMessage: AddAssistantMessageUseCase
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
            is ChatContract.Event.ChangeTargetLanguage -> changeLang(event.lang)
        }
    }

    private fun load(conversationId: String) {
        if (state.value.conversationId == conversationId && state.value.messages.isNotEmpty()) return
        _state.update { it.copy(conversationId = conversationId) }
        // Observe messages
        viewModelScope.launch {
            getMessages(conversationId)
                .onStart { _state.update { it.copy(isLoading = true, error = null) } }
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { list ->
                    _state.update { it.copy(isLoading = false, messages = list) }
                    _effects.send(ChatContract.Effect.ScrollToBottom)
                }
        }
        // Observe conversation (targetLang)
        viewModelScope.launch {
            getConversationFlow(conversationId)
                .catch { /* ignore */ }
                .collect { conv ->
                    conv?.let { _state.update { s -> s.copy(targetLang = it.targetLang) } }
                }
        }
    }

    private fun sendText() {
        val text = state.value.inputText.trim()
        if (text.isEmpty()) return
        val convId = state.value.conversationId
        viewModelScope.launch {
            addUserMessage(convId, text = text)
            _state.update { it.copy(inputText = "", translationResult = null) }
            _effects.send(ChatContract.Effect.ScrollToBottom)
            // Llamar a OpenAI para traducir
            try {
                val result = translateWithOpenAi(text, state.value.targetLang)
                if (result != null) {
                    val labels = getUiLabelsFor(state.value.targetLang)
                    val formatted = buildString {
                        appendLine("${labels.translation}: ${result.translation}")
                        if (result.usage.isNotBlank()) appendLine("${labels.usage}: ${result.usage}")
                        if (result.notes.isNotBlank()) appendLine("${labels.notes}: ${result.notes}")
                        if (result.synonyms.isNotEmpty()) append("${labels.synonyms}: ").append(result.synonyms.joinToString(", "))
                    }.trim()
                    addAssistantMessage(convId, formatted)
                    // Actualizar el estado con el resultado estructurado
                    _state.update { it.copy(translationResult = result) }
                } else {
                    _effects.send(ChatContract.Effect.ShowSnack("No se pudo obtener la traducción"))
                }
            } catch (e: Exception) {
                _effects.send(ChatContract.Effect.ShowSnack("Error de red: ${e.message}"))
            }
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

    private fun changeLang(lang: String) {
        val convId = state.value.conversationId
        if (convId.isBlank()) return
        viewModelScope.launch { setTargetLanguage(convId, lang) }
    }
}

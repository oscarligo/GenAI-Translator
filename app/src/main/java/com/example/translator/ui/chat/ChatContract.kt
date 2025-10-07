package com.example.translator.ui.chat

import com.example.translator.core.mvi.UiEffect
import com.example.translator.core.mvi.UiEvent
import com.example.translator.core.mvi.UiState
import com.example.translator.domain.model.Message

object ChatContract {
    data class State(
        val conversationId: String = "",
        val isLoading: Boolean = false,
        val messages: List<Message> = emptyList(),
        val inputText: String = "",
        val selectedImageUri: String? = null,
        val error: String? = null,
        // Nuevo: idioma objetivo de esta conversación
        val targetLang: String = "en",
        // Nuevo: resultado estructurado de la traducción
        val translationResult: com.example.translator.data.TranslationResult? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data class Load(val conversationId: String) : Event
        data class OnInputChange(val value: String) : Event
        data object SendText : Event
        data class PickImage(val uri: String?) : Event
        data object SendImage : Event
        data object ClearImage : Event
        // Nuevo: cambiar el idioma objetivo
        data class ChangeTargetLanguage(val lang: String) : Event
    }

    sealed interface Effect : UiEffect {
        data object ScrollToBottom : Effect
        data class ShowSnack(val message: String) : Effect
    }
}

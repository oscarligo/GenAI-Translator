package com.example.translator.ui.home

import com.example.translator.core.mvi.UiEffect
import com.example.translator.core.mvi.UiEvent
import com.example.translator.core.mvi.UiState
import com.example.translator.domain.model.Conversation

object HomeContract {
    data class State(
        val isLoading: Boolean = false,
        val conversations: List<Conversation> = emptyList(),
        val error: String? = null
    ) : UiState

    sealed interface Event : UiEvent {
        data object Load : Event
        data object CreateConversation : Event
    }

    sealed interface Effect : UiEffect
}


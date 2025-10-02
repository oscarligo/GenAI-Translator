package com.example.translator.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translator.domain.usecase.CreateConversationUseCase
import com.example.translator.domain.usecase.GetConversationsFlowUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getConversations: GetConversationsFlowUseCase,
    private val createConversation: CreateConversationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.State())
    val state: StateFlow<HomeContract.State> = _state.asStateFlow()

    private val _effects = Channel<HomeContract.Effect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        onEvent(HomeContract.Event.Load)
    }

    fun onEvent(event: HomeContract.Event) {
        when (event) {
            HomeContract.Event.Load -> observeConversations()
            HomeContract.Event.CreateConversation -> createNewConversation()
        }
    }

    private fun observeConversations() {
        viewModelScope.launch {
            getConversations()
                .onStart { _state.update { it.copy(isLoading = true, error = null) } }
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { list ->
                    _state.update { it.copy(isLoading = false, conversations = list) }
                }
        }
    }

    private fun createNewConversation() {
        viewModelScope.launch {
            // Create a conversation; navigation is handled in UI via createAndReturnId()
            createConversation()
        }
    }

    suspend fun createAndReturnId(): String {
        val conv = createConversation()
        return conv.id
    }
}

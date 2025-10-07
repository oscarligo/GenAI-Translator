package com.example.translator.domain.usecase

import com.example.translator.data.ConversationRepository
import com.example.translator.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

class GetConversationFlowUseCase(private val repository: ConversationRepository) {
    operator fun invoke(conversationId: String): Flow<Conversation?> = repository.getConversationFlow(conversationId)
}


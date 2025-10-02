package com.example.translator.domain.usecase

import com.example.translator.data.ConversationRepository
import com.example.translator.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

class GetConversationsFlowUseCase(private val repository: ConversationRepository) {
    operator fun invoke(): Flow<List<Conversation>> = repository.conversations
}


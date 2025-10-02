package com.example.translator.domain.usecase

import com.example.translator.data.ConversationRepository
import com.example.translator.domain.model.Message
import kotlinx.coroutines.flow.Flow

class GetMessagesFlowUseCase(private val repository: ConversationRepository) {
    operator fun invoke(conversationId: String): Flow<List<Message>> = repository.getMessages(conversationId)
}


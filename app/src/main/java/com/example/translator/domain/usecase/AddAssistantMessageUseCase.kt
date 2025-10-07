package com.example.translator.domain.usecase

import com.example.translator.data.ConversationRepository

class AddAssistantMessageUseCase(private val repository: ConversationRepository) {
    suspend operator fun invoke(conversationId: String, text: String) = repository.addAssistantMessage(conversationId, text)
}


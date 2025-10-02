package com.example.translator.domain.usecase

import com.example.translator.data.ConversationRepository
import com.example.translator.domain.model.Conversation

class CreateConversationUseCase(private val repository: ConversationRepository) {
    suspend operator fun invoke(title: String? = null): Conversation = repository.createConversation(title)
}


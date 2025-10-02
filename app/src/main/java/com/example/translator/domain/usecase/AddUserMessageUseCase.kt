package com.example.translator.domain.usecase

import com.example.translator.data.ConversationRepository

class AddUserMessageUseCase(private val repository: ConversationRepository) {
    suspend operator fun invoke(conversationId: String, text: String? = null, imageUri: String? = null) {
        repository.addUserMessage(conversationId, text, imageUri)
    }
}


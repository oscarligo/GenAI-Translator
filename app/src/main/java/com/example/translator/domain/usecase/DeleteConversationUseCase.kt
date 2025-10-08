package com.example.translator.domain.usecase

import com.example.translator.data.ConversationRepository

class DeleteConversationUseCase(private val repo: ConversationRepository) {
    suspend operator fun invoke(conversationId: String) = repo.deleteConversation(conversationId)
}


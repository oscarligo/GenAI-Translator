package com.example.translator.domain.usecase

import com.example.translator.data.ConversationRepository

class RenameConversationUseCase(private val repo: ConversationRepository) {
    suspend operator fun invoke(conversationId: String, title: String) = repo.renameConversation(conversationId, title)
}


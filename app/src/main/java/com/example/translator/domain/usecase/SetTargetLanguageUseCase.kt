package com.example.translator.domain.usecase

import com.example.translator.data.ConversationRepository

class SetTargetLanguageUseCase(private val repository: ConversationRepository) {
    suspend operator fun invoke(conversationId: String, lang: String) = repository.setTargetLanguage(conversationId, lang)
}


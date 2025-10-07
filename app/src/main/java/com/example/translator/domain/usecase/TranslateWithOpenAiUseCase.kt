package com.example.translator.domain.usecase

import com.example.translator.data.TranslationResult

class TranslateWithOpenAiUseCase(private val repo: com.example.translator.data.repository.OpenAiRepository) {
    suspend operator fun invoke(
        text: String,
        targetLang: String,
        sourceLang: String = "auto"
    ): TranslationResult? {
        return repo.translateWithDetails(text, targetLang, sourceLang)
    }
}

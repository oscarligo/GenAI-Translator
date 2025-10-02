package com.example.translator.data.ai

import com.example.translator.domain.model.ai.TranslateInput
import com.example.translator.domain.model.ai.TranslateOutput

interface TranslationApi {
    suspend fun translate(input: TranslateInput): TranslateOutput
}


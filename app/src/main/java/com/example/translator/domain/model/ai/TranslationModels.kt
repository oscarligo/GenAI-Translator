package com.example.translator.domain.model.ai

sealed class TranslateInput {
    data class Text(val text: String, val sourceLang: String? = null, val targetLang: String? = null) : TranslateInput()
    data class Image(val uri: String, val prompt: String? = null, val sourceLang: String? = null, val targetLang: String? = null) : TranslateInput()
}

sealed class TranslateOutput {
    data class Text(val translated: String, val detectedSourceLang: String? = null) : TranslateOutput()
    data class Error(val message: String, val code: Int? = null) : TranslateOutput()
}


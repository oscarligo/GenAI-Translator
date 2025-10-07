package com.example.translator.data.remote

import com.squareup.moshi.Json

// Modelo para la respuesta de OpenAI

data class OpenAiChoice(
    @Json(name = "message") val message: OpenAiMessage
)

data class OpenAiResponse(
    @Json(name = "choices") val choices: List<OpenAiChoice>
)


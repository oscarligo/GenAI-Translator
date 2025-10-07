package com.example.translator.data.remote

import com.squareup.moshi.Json

// Modelo para la petición a OpenAI
// Ejemplo para chat completions

data class OpenAiMessage(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

data class OpenAiRequest(
    @Json(name = "model") val model: String = "gpt-3.5-turbo",
    @Json(name = "messages") val messages: List<OpenAiMessage>,
    @Json(name = "temperature") val temperature: Double = 0.2
)


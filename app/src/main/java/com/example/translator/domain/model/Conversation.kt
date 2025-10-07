package com.example.translator.domain.model

import java.util.UUID

data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Nueva conversación",
    val lastUpdated: Long = System.currentTimeMillis(),
    val messageCount: Int = 0,
    // Nuevo: idioma objetivo para traducción (ISO 639-1, p.ej. "en", "es")
    val targetLang: String = "en"
)

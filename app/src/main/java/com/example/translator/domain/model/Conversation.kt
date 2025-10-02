package com.example.translator.domain.model

import java.util.UUID

data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Nueva conversación",
    val lastUpdated: Long = System.currentTimeMillis(),
    val messageCount: Int = 0
)


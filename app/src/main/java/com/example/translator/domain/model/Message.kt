package com.example.translator.domain.model

import java.util.UUID

enum class MessageType { TEXT, IMAGE }

enum class MessageRole { USER, ASSISTANT }

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType,
    val role: MessageRole,
    val text: String? = null,
    val imageUri: String? = null
)


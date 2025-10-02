package com.example.translator.data

import com.example.translator.domain.model.Conversation
import com.example.translator.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    val conversations: Flow<List<Conversation>>
    fun getMessages(conversationId: String): Flow<List<Message>>
    suspend fun createConversation(title: String? = null): Conversation
    suspend fun addUserMessage(
        conversationId: String,
        text: String? = null,
        imageUri: String? = null
    )
}

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
    // Nuevo: observar una conversación específica
    fun getConversationFlow(conversationId: String): Flow<Conversation?>
    // Nuevo: actualizar el idioma objetivo de una conversación
    suspend fun setTargetLanguage(conversationId: String, lang: String)
    // Nuevo: agregar mensaje del asistente
    suspend fun addAssistantMessage(conversationId: String, text: String)
}

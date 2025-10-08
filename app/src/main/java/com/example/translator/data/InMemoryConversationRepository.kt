package com.example.translator.data

import com.example.translator.domain.model.Conversation
import com.example.translator.domain.model.Message
import com.example.translator.domain.model.MessageRole
import com.example.translator.domain.model.MessageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class InMemoryConversationRepository : ConversationRepository {
    private val conversationsFlow = MutableStateFlow<List<Conversation>>(emptyList())
    private val messagesMap = mutableMapOf<String, MutableStateFlow<List<Message>>>()

    override val conversations: Flow<List<Conversation>> = conversationsFlow.asStateFlow()

    override fun getMessages(conversationId: String): Flow<List<Message>> =
        messagesMap.getOrPut(conversationId) { MutableStateFlow(emptyList()) }.asStateFlow()

    override suspend fun createConversation(title: String?): Conversation {
        val conv = Conversation(title = title ?: "Nueva conversación")
        messagesMap[conv.id] = MutableStateFlow(emptyList())
        conversationsFlow.value = (listOf(conv) + conversationsFlow.value).sortedByDescending { it.lastUpdated }
        return conv
    }

    override suspend fun addUserMessage(conversationId: String, text: String?, imageUri: String?) {
        val listFlow = messagesMap.getOrPut(conversationId) { MutableStateFlow(emptyList()) }
        val message = Message(
            conversationId = conversationId,
            type = if (imageUri != null) MessageType.IMAGE else MessageType.TEXT,
            role = MessageRole.USER,
            text = text,
            imageUri = imageUri
        )
        listFlow.value = listFlow.value + message
        // update conversation metadata
        conversationsFlow.value = conversationsFlow.value.map { c ->
            if (c.id == conversationId) c.copy(messageCount = c.messageCount + 1, lastUpdated = System.currentTimeMillis()) else c
        }.sortedByDescending { it.lastUpdated }
    }

    override fun getConversationFlow(conversationId: String): Flow<Conversation?> =
        conversationsFlow.map { list -> list.find { it.id == conversationId } }

    override suspend fun setTargetLanguage(conversationId: String, lang: String) {
        conversationsFlow.value = conversationsFlow.value.map { c ->
            if (c.id == conversationId) c.copy(targetLang = lang, lastUpdated = System.currentTimeMillis()) else c
        }.sortedByDescending { it.lastUpdated }
    }

    override suspend fun addAssistantMessage(conversationId: String, text: String) {
        val listFlow = messagesMap.getOrPut(conversationId) { MutableStateFlow(emptyList()) }
        val message = Message(
            conversationId = conversationId,
            type = MessageType.TEXT,
            role = MessageRole.ASSISTANT,
            text = text
        )
        listFlow.value = listFlow.value + message
        conversationsFlow.value = conversationsFlow.value.map { c ->
            if (c.id == conversationId) c.copy(messageCount = c.messageCount + 1, lastUpdated = System.currentTimeMillis()) else c
        }.sortedByDescending { it.lastUpdated }
    }

    override suspend fun deleteConversation(conversationId: String) {
        // Remove from list
        conversationsFlow.value = conversationsFlow.value.filterNot { it.id == conversationId }
        // Remove messages flow
        messagesMap.remove(conversationId)
    }

    override suspend fun renameConversation(conversationId: String, title: String) {
        conversationsFlow.value = conversationsFlow.value.map { c ->
            if (c.id == conversationId) c.copy(title = title, lastUpdated = System.currentTimeMillis()) else c
        }.sortedByDescending { it.lastUpdated }
    }
}

package com.example.translator.di

import com.example.translator.data.ConversationRepository
import com.example.translator.data.InMemoryConversationRepository
import com.example.translator.domain.usecase.AddUserMessageUseCase
import com.example.translator.domain.usecase.CreateConversationUseCase
import com.example.translator.domain.usecase.GetConversationsFlowUseCase
import com.example.translator.domain.usecase.GetMessagesFlowUseCase

object AppGraph {
    // Repository
    val conversationRepository: ConversationRepository by lazy { InMemoryConversationRepository() }

    // Use cases
    val createConversation by lazy { CreateConversationUseCase(conversationRepository) }
    val getConversationsFlow by lazy { GetConversationsFlowUseCase(conversationRepository) }
    val getMessagesFlow by lazy { GetMessagesFlowUseCase(conversationRepository) }
    val addUserMessage by lazy { AddUserMessageUseCase(conversationRepository) }
}


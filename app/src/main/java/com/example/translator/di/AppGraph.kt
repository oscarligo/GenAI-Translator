package com.example.translator.di

import com.example.translator.data.ConversationRepository
import com.example.translator.data.InMemoryConversationRepository
import com.example.translator.data.repository.OpenAiRepository
import com.example.translator.domain.usecase.AddAssistantMessageUseCase
import com.example.translator.domain.usecase.AddUserMessageUseCase
import com.example.translator.domain.usecase.CreateConversationUseCase
import com.example.translator.domain.usecase.DeleteConversationUseCase
import com.example.translator.domain.usecase.GetConversationsFlowUseCase
import com.example.translator.domain.usecase.GetConversationFlowUseCase
import com.example.translator.domain.usecase.GetMessagesFlowUseCase
import com.example.translator.domain.usecase.RenameConversationUseCase
import com.example.translator.domain.usecase.SetTargetLanguageUseCase
import com.example.translator.domain.usecase.TranslateWithOpenAiUseCase
import com.example.translator.BuildConfig

object AppGraph {
    // Repository
    val conversationRepository: ConversationRepository by lazy { InMemoryConversationRepository() }

    // OpenAI
    val openAiRepository by lazy { OpenAiRepository(BuildConfig.OPENAI_API_KEY) }

    // Use cases
    val createConversation by lazy { CreateConversationUseCase(conversationRepository) }
    val getConversationsFlow by lazy { GetConversationsFlowUseCase(conversationRepository) }
    val getMessagesFlow by lazy { GetMessagesFlowUseCase(conversationRepository) }
    val addUserMessage by lazy { AddUserMessageUseCase(conversationRepository) }
    val addAssistantMessage by lazy { AddAssistantMessageUseCase(conversationRepository) }
    // Nuevos
    val getConversationFlow by lazy { GetConversationFlowUseCase(conversationRepository) }
    val setTargetLanguage by lazy { SetTargetLanguageUseCase(conversationRepository) }
    val translateWithOpenAi by lazy { TranslateWithOpenAiUseCase(openAiRepository) }
    val deleteConversation by lazy { DeleteConversationUseCase(conversationRepository) }
    val renameConversation by lazy { RenameConversationUseCase(conversationRepository) }
}

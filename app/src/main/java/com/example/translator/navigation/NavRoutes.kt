package com.example.translator.navigation

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Chat : NavRoutes("chat/{conversationId}") {
        const val ARG_ID = "conversationId"
        fun route(conversationId: String) = "chat/$conversationId"
    }
}


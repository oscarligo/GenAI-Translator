package com.example.translator.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.translator.navigation.NavRoutes
import com.example.translator.ui.chat.ChatScreen
import com.example.translator.ui.home.HomeScreen

@Composable
fun TranslatorApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoutes.Home.route) {
        composable(NavRoutes.Home.route) {
            HomeScreen(onOpenConversation = { id -> navController.navigate(NavRoutes.Chat.route(id)) })
        }
        composable(
            route = NavRoutes.Chat.route,
            arguments = listOf(navArgument(NavRoutes.Chat.ARG_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val convId = backStackEntry.arguments?.getString(NavRoutes.Chat.ARG_ID).orEmpty()
            ChatScreen(conversationId = convId, onBack = { navController.popBackStack() })
        }
    }
}


package com.example.recieptsharingapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recieptsharingapp.ui.*
import com.example.recieptsharingapp.ui.theme.RecieptSharingAppTheme
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RecieptSharingAppTheme {
                AppUI { s -> switchLanguage(s) }
            }
        }
    }

    private fun switchLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate() // Перезапускаем Activity для применения изменений
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun AppUI(onLanguageSwitch: (String) -> Unit) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "login") {
            composable("profile") { ProfileScreen(navController) }
            composable("expenses") { MyDebtsScreen(navController) }
            composable("friends") { FriendsScreen(navController) }
            composable("add_expense") { AddExpenseScreen(navController) }
            composable("main") { MainScreen(navController) }
            composable("login") { LoginScreen(context = applicationContext, navController = navController, onLanguageSwitch = onLanguageSwitch)}
            composable("registration") { RegistrationScreen(navController) }
            composable("add_friend_screen") { AddFriendScreen(navController) }
            composable("my_groups") { GroupsScreen(navController) }
            composable("add_group_screen") { AddGroupScreen(navController) }
            composable("expenses_by_group/{groupId}/{groupName}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")?.toLong() ?: 0L
                val groupName = backStackEntry.arguments?.getString("groupName") ?: ""
                ExpensesByGroupScreen(groupId = groupId, groupName = groupName, navHostController = navController)
            }
        }
    }
}

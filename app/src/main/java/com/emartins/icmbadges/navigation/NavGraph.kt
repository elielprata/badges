package com.emartins.icmbadges.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emartins.icmbadges.data.UserPreferences
import com.emartins.icmbadges.screens.home.HomeScreen
import com.emartins.icmbadges.screens.login.LoginScreen

@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") {
            val prefs = UserPreferences(LocalContext.current)
            LoginScreen(
                onLoginSuccess = { codNivel ->
                    navController.navigate("home/$codNivel") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                prefs
            )
        }
        composable ("home/{codNivel}") { backStackEntry ->
            val codNivel = backStackEntry.arguments?.getString("codNivel") ?: ""
            HomeScreen(codNivel)
        }
    }
}
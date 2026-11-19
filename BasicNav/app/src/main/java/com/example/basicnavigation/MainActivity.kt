package com.example.basicnavigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basicnavigation.ui.theme.BasicNavigationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicNavigationTheme {
                BasicNavigationApp()
            }
        }
    }
}

@Composable
fun BasicNavigationApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        // Layar Home
        composable(Routes.Home.route) {
            HomeScreen(navController)
        }
        // Layar Profile
        composable(Routes.Profile.route) {
            ProfileScreen(navController)
        }
        // Layar Detail
        composable(Routes.Detail.route) {
            DetailScreen(navController)
        }
    }
}
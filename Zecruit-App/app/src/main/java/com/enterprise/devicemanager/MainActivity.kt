package com.enterprise.devicemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.enterprise.devicemanager.data.repository.ThemeRepository
import com.enterprise.devicemanager.ui.theme.EDMTheme
import com.enterprise.devicemanager.ui.theme.ThemeViewModel
import com.enterprise.devicemanager.ui.screens.dashboard.DashboardScreen
import com.enterprise.devicemanager.ui.screens.splash.SplashScreen
import com.enterprise.devicemanager.ui.screens.login.LoginScreen
import com.enterprise.devicemanager.worker.DeviceSyncWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = ThemeRepository(applicationContext)

        // Schedule periodic background sync (every 6 hours)
        DeviceSyncWorker.schedule(applicationContext)
        
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModel.Factory(repository)
            )
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            var currentScreen by remember { mutableStateOf("splash") }

            EDMTheme(darkTheme = isDarkTheme) {
                when (currentScreen) {
                    "splash" -> {
                        SplashScreen(onNavigateToLogin = {
                            currentScreen = "login"
                        })
                    }
                    "login" -> {
                        LoginScreen(
                            isDark = isDarkTheme,
                            onThemeToggle = { themeViewModel.toggleTheme() },
                            onLoginSuccess = {
                                currentScreen = "dashboard"
                            }
                        )
                    }
                    "dashboard" -> {
                        DashboardScreen(
                            isDark = isDarkTheme,
                            onThemeToggle = { themeViewModel.toggleTheme() },
                            onLogout = { currentScreen = "login" }
                        )
                    }
                }
            }
        }
    }
}

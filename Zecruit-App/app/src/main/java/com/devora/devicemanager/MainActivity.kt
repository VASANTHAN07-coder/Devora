package com.devora.devicemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devora.devicemanager.ui.navigation.AppNavigation
import com.devora.devicemanager.ui.theme.DevoraTheme
import com.devora.devicemanager.ui.theme.ThemeViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeVm: ThemeViewModel = viewModel()
            val isDark = themeVm.isDark

            DevoraTheme(isDark = isDark) {
                val systemUiController = rememberSystemUiController()
                val statusBarColor = if (isDark) {
                    Color(0xFF08080F)  // DarkBgBase
                } else {
                    Color(0xFFFAF9FF)  // BgBase
                }

                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = statusBarColor,
                        darkIcons = !isDark
                    )
                    systemUiController.setNavigationBarColor(
                        color = statusBarColor,
                        darkIcons = !isDark
                    )
                }

                AppNavigation(
                    isDark = isDark,
                    onThemeToggle = themeVm::toggle
                )
            }
        }
    }
}

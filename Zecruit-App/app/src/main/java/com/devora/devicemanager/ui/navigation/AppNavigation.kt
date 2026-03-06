package com.devora.devicemanager.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devora.devicemanager.ui.screens.dashboard.DashboardScreen
import com.devora.devicemanager.ui.screens.devices.DeviceListScreen
import com.devora.devicemanager.ui.screens.enrollment.EnrollmentScreen
import com.devora.devicemanager.ui.screens.login.LoginScreen
import com.devora.devicemanager.ui.screens.settings.SettingsScreen
import com.devora.devicemanager.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation(
    isDark: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()

    val navigateTo: (String) -> Unit = { route ->
        if (navController.currentDestination?.route != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash → Login: fadeIn/fadeOut 500ms
        composable(
            route = "splash",
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // Login → Dashboard: fadeIn 600ms
        composable(
            route = "login",
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(600)) }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                isDark = isDark,
                onThemeToggle = onThemeToggle
            )
        }

        // Dashboard
        composable(
            route = "dashboard",
            enterTransition = { fadeIn(animationSpec = tween(600)) },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            DashboardScreen(
                onNavigate = navigateTo,
                isDark = isDark,
                onThemeToggle = onThemeToggle
            )
        }

        // Device List
        composable(
            route = "device_list",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            DeviceListScreen(
                onDeviceClick = { deviceName ->
                    // Device detail navigation placeholder
                },
                onEnrollClick = {
                    navController.navigate("enrollment")
                },
                onNavigate = navigateTo,
                isDark = isDark
            )
        }

        // Enrollment
        composable(
            route = "enrollment",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            EnrollmentScreen(
                onClose = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                onNavigate = navigateTo,
                isDark = isDark
            )
        }

        // Settings
        composable(
            route = "settings",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            SettingsScreen(
                isDark = isDark,
                onThemeToggle = onThemeToggle,
                onSignOut = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigate = navigateTo
            )
        }
    }
}

// ══════════════════════════════════════
// TRANSITION HELPERS
// ══════════════════════════════════════

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInFromRight(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInFromLeft(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutToLeft(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutToRight(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    )
}

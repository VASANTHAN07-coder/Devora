package com.devora.devicemanager.ui.screens.employeedashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// Placeholder — full implementation in File 3
@Composable
fun EmployeeDashboardScreen(
    onSignOut: () -> Unit,
    isDark: Boolean,
    onThemeToggle: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Employee Dashboard (coming soon)")
    }
}

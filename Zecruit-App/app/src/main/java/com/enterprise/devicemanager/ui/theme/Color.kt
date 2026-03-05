package com.enterprise.devicemanager.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Colors
val MintGreen = Color(0xFF2ECC8F)
val Teal = Color(0xFF4ADECD)

// Light Theme Colors
val LightBackground = Color(0xFFF0F4F8)
val LightSurface = Color(0xFFFFFFFF)
val CardShadow = Color(0xFFC8D5E8)
val LightTextPrimary = Color(0xFF1A2332)
val LightTextSecondary = Color(0xFF8A9BB0)
val LightError = Color(0xFFFF6B6B)
val LightWarning = Color(0xFFFFB347)
val LightSuccess = Color(0xFF2ECC8F)

// Dark Theme Colors
val DarkBackground = Color(0xFF111827)
val DarkSurface = Color(0xFF1F2937)
val DarkTextPrimary = Color(0xFFF9FAFB)
val DarkTextSecondary = Color(0xFF9CA3AF)
val DarkError = Color(0xFFEF4444)
val DarkWarning = Color(0xFFF59E0B)
val DarkSuccess = Color(0xFF2ECC8F)

// Gradients
val MintGradient = Brush.horizontalGradient(
    colors = listOf(MintGreen, Teal)
)

val LightBackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFF0F4F8), Color(0xFFE8EEF5))
)

val DarkBackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF111827), Color(0xFF0D1321))
)

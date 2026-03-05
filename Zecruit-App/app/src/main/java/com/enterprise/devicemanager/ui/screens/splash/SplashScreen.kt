package com.enterprise.devicemanager.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enterprise.devicemanager.ui.components.GlassCard
import com.enterprise.devicemanager.ui.theme.MintGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF111827)
    
    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF111827), Color(0xFF0D1321)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF0F4F8), Color(0xFFE2EAF4)))
    }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )

    val fadeAnim = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        fadeAnim.animateTo(1f, animationSpec = tween(1000))
        delay(2000) // Total 3 seconds including fade
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(fadeAnim.value)
        ) {
            // Animated Shield/Lock Icon
            ShieldLockIcon(
                modifier = Modifier
                    .size(120.dp)
                    .scale(pulseScale)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // EDM Text
            Text(
                text = "EDM",
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = MintGreen,
                letterSpacing = 6.sp,
                fontFamily = FontFamily.SansSerif
            )

            Text(
                text = "Enterprise Device Manager",
                fontSize = 14.sp,
                color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF8A9BB0),
                fontFamily = FontFamily.SansSerif
            )
        }

        // Bottom Loading Card
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .padding(horizontal = 32.dp)
                .alpha(fadeAnim.value)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Initializing secure connection...",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = MintGreen,
                        trackColor = MintGreen.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun ShieldLockIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Draw Shield
        val shieldPath = Path().apply {
            moveTo(width * 0.5f, 0f)
            lineTo(width, height * 0.2f)
            lineTo(width, height * 0.6f)
            quadraticTo(width, height * 0.9f, width * 0.5f, height)
            quadraticTo(0f, height * 0.9f, 0f, height * 0.6f)
            lineTo(0f, height * 0.2f)
            close()
        }
        
        drawPath(
            path = shieldPath,
            color = MintGreen.copy(alpha = 0.2f),
            style = Stroke(width = 20f)
        )
        
        drawPath(
            path = shieldPath,
            color = MintGreen,
            style = Fill
        )

        // Draw Lock Detail
        val lockShackle = Path().apply {
            moveTo(width * 0.35f, height * 0.45f)
            lineTo(width * 0.35f, height * 0.35f)
            quadraticTo(width * 0.5f, height * 0.25f, width * 0.65f, height * 0.35f)
            lineTo(width * 0.65f, height * 0.45f)
        }
        
        drawPath(
            path = lockShackle,
            color = Color.White,
            style = Stroke(width = 8f)
        )
        
        drawRect(
            color = Color.White,
            topLeft = androidx.compose.ui.geometry.Offset(width * 0.35f, height * 0.45f),
            size = androidx.compose.ui.geometry.Size(width * 0.3f, height * 0.2f)
        )
    }
}

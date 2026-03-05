package com.enterprise.devicemanager.ui.screens.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enterprise.devicemanager.ui.components.MintGradientButton
import com.enterprise.devicemanager.ui.theme.MintGreen
import com.enterprise.devicemanager.ui.theme.Teal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    isDark: Boolean,
    onThemeToggle: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var adminId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    // Shake Animation for Error
    val shakeOffset = remember { Animatable(0f) }
    
    val backgroundGradient = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF111827), Color(0xFF0D1321)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF0F4F8), Color(0xFFE8F4EF)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Decorative Blurred Circles (15% Opacity)
        DecorativeCircle(
            color = MintGreen.copy(alpha = 0.15f),
            size = 320.dp,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 100.dp, y = (-50).dp)
        )
        DecorativeCircle(
            color = Teal.copy(alpha = 0.15f),
            size = 280.dp,
            modifier = Modifier.align(Alignment.BottomStart).offset(x = (-80).dp, y = 80.dp)
        )
        DecorativeCircle(
            color = MintGreen.copy(alpha = 0.15f),
            size = 220.dp,
            modifier = Modifier.align(Alignment.Center).offset(x = 50.dp, y = 100.dp)
        )

        // Theme Toggle Icon in Top Right
        IconButton(
            onClick = onThemeToggle,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
                .statusBarsPadding()
        ) {
            Icon(
                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = if (isDark) Color.White else Color(0xFF1A2332),
                modifier = Modifier.size(28.dp)
            )
        }

        // Frosted Glass Card (Center)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .imePadding()
                .offset(x = shakeOffset.value.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 30.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = if (isDark) Color.Black else Color(0xFFC8D5E8).copy(alpha = 0.8f)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        if (isDark) Color(0xFF1F2937).copy(alpha = 0.85f)
                        else Color.White.copy(alpha = 0.85f)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.1f))
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(28.dp)
                    .widthIn(max = 420.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Small shield icon in mint green circle
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MintGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MintGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Welcome Back",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF1A2332)
                    )
                    
                    Text(
                        text = "Sign in to your admin account",
                        fontSize = 13.sp,
                        color = Color(0xFF8A9BB0)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Admin ID field
                    LoginTextField(
                        value = adminId,
                        onValueChange = { 
                            adminId = it
                            isError = false 
                        },
                        placeholder = "Admin ID",
                        icon = Icons.Default.Person,
                        isDark = isDark,
                        isError = isError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field
                    LoginTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            isError = false
                        },
                        placeholder = "Password",
                        icon = Icons.Default.Lock,
                        isDark = isDark,
                        isPassword = true,
                        isError = isError
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Sign In pill button
                    MintGradientButton(
                        text = "Sign In",
                        isLoading = isLoading,
                        onClick = {
                            if (adminId.isBlank() || password.isBlank()) {
                                isError = true
                                scope.launch {
                                    repeat(4) {
                                        shakeOffset.animateTo(8f, tween(50))
                                        shakeOffset.animateTo(-8f, tween(50))
                                    }
                                    shakeOffset.animateTo(0f, tween(50))
                                }
                            } else {
                                isLoading = true
                                scope.launch {
                                    delay(1500)
                                    isLoading = false
                                    onLoginSuccess()
                                }
                            }
                        }
                    )
                }
            }
        }

        // Bottom Footer
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFF8A9BB0),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Secured by Enterprise MDM",
                fontSize = 12.sp,
                color = Color(0xFF8A9BB0)
            )
        }
    }
}

@Composable
fun DecorativeCircle(
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .blur(70.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isDark: Boolean,
    isPassword: Boolean = false,
    isError: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                color = if (isError) Color.Red.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            ),
        placeholder = { Text(placeholder, color = Color(0xFF8A9BB0), fontSize = 14.sp) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MintGreen, modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF8A9BB0)
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = if (isDark) Color(0xFF374151) else Color(0xFFF5F7FA),
            unfocusedContainerColor = if (isDark) Color(0xFF374151) else Color(0xFFF5F7FA),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MintGreen
        ),
        singleLine = true
    )
}

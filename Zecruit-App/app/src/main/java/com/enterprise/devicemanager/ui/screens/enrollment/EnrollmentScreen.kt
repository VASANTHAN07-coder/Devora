package com.enterprise.devicemanager.ui.screens.enrollment

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enterprise.devicemanager.admin.DeviceOwnerHelper
import com.enterprise.devicemanager.data.model.EnrollRequest
import com.enterprise.devicemanager.data.network.RetrofitClient
import com.enterprise.devicemanager.ui.components.GlassCard
import com.enterprise.devicemanager.ui.components.MintGradientButton
import com.enterprise.devicemanager.ui.screens.login.LoginTextField
import com.enterprise.devicemanager.ui.theme.MintGreen
import com.enterprise.devicemanager.ui.theme.PillShape
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun EnrollmentScreen(isDark: Boolean) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = QR, 1 = Manual
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Device Enrollment",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Device Owner Status
        val ownerStatus = remember { DeviceOwnerHelper.getDeviceOwnerStatus(context) }
        GlassCard(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = null,
                    tint = if (DeviceOwnerHelper.isDeviceOwner(context)) MintGreen else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Provisioning Status", fontSize = 12.sp, color = Color.Gray)
                    Text(ownerStatus, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Segmented Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(PillShape)
                .background(if (isDark) Color(0xFF1F2937) else Color.White)
                .padding(4.dp)
        ) {
            EnrollmentTabItem(
                text = "QR Code",
                isSelected = selectedTab == 0,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = 0 }
            )
            EnrollmentTabItem(
                text = "Manual Entry",
                isSelected = selectedTab == 1,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = 1 }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "EnrollmentTabTransition"
        ) { targetTab ->
            if (targetTab == 0) {
                QrEnrollmentContent(isDark)
            } else {
                ManualEnrollmentContent(isDark)
            }
        }
    }
}

@Composable
fun EnrollmentTabItem(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(PillShape)
            .background(if (isSelected) MintGreen else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun QrEnrollmentContent(isDark: Boolean) {
    val provisioningJson = remember {
        DeviceOwnerHelper.getProvisioningQrJson()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(32.dp))
                .background(if (isDark) Color.Black.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.1f))
                .border(2.dp, MintGreen.copy(alpha = 0.2f), RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Scanner View Finder UI
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .border(4.dp, MintGreen, RoundedCornerShape(24.dp))
            )
            
            Icon(
                imageVector = Icons.Outlined.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MintGreen.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MintGreen)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "QR Code Provisioning",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1. Factory reset the target device\n" +
                                "2. Tap 6 times on the welcome screen\n" +
                                "3. Connect to WiFi\n" +
                                "4. Scan the enrollment QR code",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF1F2937) else Color.White)
            ) {
                Icon(Icons.Default.FlashlightOn, contentDescription = null, tint = MintGreen)
            }
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF1F2937) else Color.White)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = MintGreen)
            }
        }
    }
}

@Composable
fun ManualEnrollmentContent(isDark: Boolean) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var enrollmentToken by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    // Auto-generate device ID from Android ID
    val deviceId = remember {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: UUID.randomUUID().toString()
        "EDM-$androidId"
    }

    Column {
        Text(
            text = "Enter details manually",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Device ID (auto-generated, read-only display)
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text("Device ID (auto-generated)", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(deviceId, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LoginTextField(
            value = enrollmentToken,
            onValueChange = { enrollmentToken = it },
            placeholder = "Enrollment Token (optional)",
            icon = Icons.Default.Fingerprint,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(32.dp))

        MintGradientButton(
            text = if (isLoading) "Enrolling..." else "Enroll Device",
            isLoading = isLoading,
            onClick = {
                scope.launch {
                    isLoading = true
                    resultMessage = null
                    try {
                        val request = EnrollRequest(
                            deviceId = deviceId,
                            enrollmentToken = enrollmentToken.ifBlank { null },
                            enrollmentMethod = if (enrollmentToken.isNotBlank()) "TOKEN" else "MANUAL"
                        )
                        val response = RetrofitClient.apiService.enrollDevice(request)
                        if (response.isSuccessful) {
                            val body = response.body()
                            isSuccess = true
                            resultMessage = "Enrolled successfully! Status: ${body?.status}"
                        } else {
                            isSuccess = false
                            resultMessage = "Enrollment failed: ${response.code()} ${response.message()}"
                        }
                    } catch (e: Exception) {
                        isSuccess = false
                        resultMessage = "Network error: ${e.localizedMessage}"
                    } finally {
                        isLoading = false
                    }
                }
            }
        )

        // Result feedback
        resultMessage?.let { msg ->
            Spacer(modifier = Modifier.height(16.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), showAccent = true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (isSuccess) MintGreen else Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(msg, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "By proceeding, you agree to the Enterprise Device Management terms and privacy policy.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

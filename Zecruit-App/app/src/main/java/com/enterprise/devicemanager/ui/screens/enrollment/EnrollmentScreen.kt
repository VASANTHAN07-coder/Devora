package com.enterprise.devicemanager.ui.screens.enrollment

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enterprise.devicemanager.ui.components.GlassCard
import com.enterprise.devicemanager.ui.components.MintGradientButton
import com.enterprise.devicemanager.ui.screens.login.LoginTextField
import com.enterprise.devicemanager.ui.theme.MintGreen
import com.enterprise.devicemanager.ui.theme.PillShape

@Composable
fun EnrollmentScreen(isDark: Boolean) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = QR, 1 = Manual

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
                Text(
                    text = "Point your camera at the enrollment QR code provided by your organization.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
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
    var enrollmentId by remember { mutableStateOf("") }
    var orgCode by remember { mutableStateOf("") }

    Column {
        Text(
            text = "Enter details manually",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LoginTextField(
            value = enrollmentId,
            onValueChange = { enrollmentId = it },
            placeholder = "Enrollment ID",
            icon = Icons.Default.Fingerprint,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginTextField(
            value = orgCode,
            onValueChange = { orgCode = it },
            placeholder = "Organization Code",
            icon = Icons.Default.Business,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(32.dp))

        MintGradientButton(
            text = "Proceed to Enroll",
            onClick = { /* TODO */ }
        )

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

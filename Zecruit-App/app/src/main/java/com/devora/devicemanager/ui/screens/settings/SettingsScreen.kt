package com.devora.devicemanager.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devora.devicemanager.ui.components.ButtonVariant
import com.devora.devicemanager.ui.components.DevoraBottomNav
import com.devora.devicemanager.ui.components.DevoraButton
import com.devora.devicemanager.ui.components.DevoraCard
import com.devora.devicemanager.ui.components.SectionHeader
import com.devora.devicemanager.ui.theme.BadgeShape
import com.devora.devicemanager.ui.theme.BgBase
import com.devora.devicemanager.ui.theme.BgElevated
import com.devora.devicemanager.ui.theme.DMSans
import com.devora.devicemanager.ui.theme.Danger
import com.devora.devicemanager.ui.theme.DarkBgBase
import com.devora.devicemanager.ui.theme.DarkBgElevated
import com.devora.devicemanager.ui.theme.DarkTextPrimary
import com.devora.devicemanager.ui.theme.InputShape
import com.devora.devicemanager.ui.theme.JetBrainsMono
import com.devora.devicemanager.ui.theme.PlusJakartaSans
import com.devora.devicemanager.ui.theme.PurpleBorder
import com.devora.devicemanager.ui.theme.PurpleCore
import com.devora.devicemanager.ui.theme.PurpleDeep
import com.devora.devicemanager.ui.theme.Success
import com.devora.devicemanager.ui.theme.TextMuted
import com.devora.devicemanager.ui.theme.TextPrimary

// ══════════════════════════════════════
// POLICY ITEM DATA
// ══════════════════════════════════════

private data class PolicyItem(
    val icon: ImageVector,
    val title: String,
    val desc: String,
    val key: String
)

// ══════════════════════════════════════
// SETTINGS SCREEN
// ══════════════════════════════════════

@Composable
fun SettingsScreen(
    isDark: Boolean,
    onThemeToggle: () -> Unit,
    onSignOut: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var darkMode by remember { mutableStateOf(isDark) }
    var factoryReset by remember { mutableStateOf(true) }
    var screenLock by remember { mutableStateOf(true) }
    var appRestrict by remember { mutableStateOf(false) }
    var cameraDisable by remember { mutableStateOf(false) }
    var bgSync by remember { mutableStateOf(true) }
    var wifiOnly by remember { mutableStateOf(false) }
    var serverUrl by remember { mutableStateOf("https://mdm.enterprise.com/api") }
    var syncInterval by remember { mutableStateOf("30m") }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var connectionStatus by remember { mutableStateOf<String?>(null) }

    val bgColor = if (isDark) DarkBgBase else BgBase
    val textColor = if (isDark) DarkTextPrimary else TextPrimary
    val inputBg = if (isDark) DarkBgElevated else BgElevated
    val dividerColor = if (isDark) PurpleCore.copy(alpha = 0.10f) else BgElevated

    val switchColors = SwitchDefaults.colors(
        checkedTrackColor = PurpleCore,
        uncheckedTrackColor = if (isDark) DarkBgElevated else BgElevated,
        checkedThumbColor = Color.White,
        uncheckedThumbColor = Color.White
    )

    val policies = listOf(
        PolicyItem(Icons.Filled.Shield, "Factory Reset Protection", "Prevent unauthorized reset", "factoryReset"),
        PolicyItem(Icons.Filled.Lock, "Screen Lock Enforcement", "Require PIN/pattern", "screenLock"),
        PolicyItem(Icons.Filled.Block, "App Install Restriction", "Control app installation", "appRestrict"),
        PolicyItem(Icons.Filled.CameraAlt, "Camera Disable", "Disable device camera", "cameraDisable")
    )

    Scaffold(
        bottomBar = {
            DevoraBottomNav(
                currentRoute = "settings",
                onNavigate = onNavigate,
                isDark = isDark
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── PROFILE CARD ──
            item {
                DevoraCard(showTopAccent = true, isDark = isDark) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(PurpleCore, PurpleDeep)
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "A",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Administrator",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = textColor
                            )
                            Text(
                                text = "Device Owner",
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp,
                                color = PurpleCore
                            )
                            Text(
                                text = "admin@enterprise.com",
                                fontFamily = DMSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                        TextButton(onClick = { }) {
                            Text(
                                text = "Edit",
                                fontFamily = DMSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = PurpleCore
                            )
                        }
                    }
                }
            }

            // ── APPEARANCE ──
            item {
                SectionHeader(title = "▸ APPEARANCE", isDark = isDark)
            }
            item {
                DevoraCard(isDark = isDark) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (darkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                                contentDescription = null,
                                tint = PurpleCore,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Dark Mode",
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                                Text(
                                    text = "Switch appearance",
                                    fontFamily = DMSans,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                        Switch(
                            checked = darkMode,
                            onCheckedChange = {
                                darkMode = it
                                onThemeToggle()
                            },
                            colors = switchColors
                        )
                    }
                }
            }

            // ── DEVICE POLICIES ──
            item {
                SectionHeader(title = "▸ DEVICE POLICIES", isDark = isDark)
            }
            item {
                DevoraCard(isDark = isDark) {
                    Column {
                        policies.forEachIndexed { index, policy ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = policy.icon,
                                    contentDescription = null,
                                    tint = PurpleCore,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = policy.title,
                                        fontFamily = PlusJakartaSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = textColor
                                    )
                                    Text(
                                        text = policy.desc,
                                        fontFamily = DMSans,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }
                                val checked = when (policy.key) {
                                    "factoryReset" -> factoryReset
                                    "screenLock" -> screenLock
                                    "appRestrict" -> appRestrict
                                    "cameraDisable" -> cameraDisable
                                    else -> false
                                }
                                Switch(
                                    checked = checked,
                                    onCheckedChange = { value ->
                                        when (policy.key) {
                                            "factoryReset" -> factoryReset = value
                                            "screenLock" -> screenLock = value
                                            "appRestrict" -> appRestrict = value
                                            "cameraDisable" -> cameraDisable = value
                                        }
                                    },
                                    colors = switchColors
                                )
                            }
                            if (index < policies.size - 1) {
                                Divider(thickness = 1.dp, color = dividerColor)
                            }
                        }
                    }
                }
            }

            // ── BACKEND CONFIGURATION ──
            item {
                SectionHeader(title = "▸ BACKEND CONFIGURATION", isDark = isDark)
            }
            item {
                DevoraCard(isDark = isDark) {
                    Column {
                        Text(
                            text = "Server URL",
                            fontFamily = DMSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = PurpleCore
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(inputBg, InputShape)
                                .border(1.dp, PurpleCore.copy(alpha = 0.30f), InputShape)
                                .padding(12.dp)
                        ) {
                            BasicTextField(
                                value = serverUrl,
                                onValueChange = { serverUrl = it },
                                textStyle = TextStyle(
                                    fontFamily = JetBrainsMono,
                                    fontSize = 13.sp,
                                    color = textColor
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DevoraButton(
                                text = "Test Connection",
                                onClick = { connectionStatus = "connected" },
                                variant = ButtonVariant.OUTLINE,
                                isDark = isDark
                            )
                            connectionStatus?.let { status ->
                                Text(
                                    text = if (status == "connected") "● Connected 12ms" else "● Failed",
                                    fontFamily = JetBrainsMono,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = if (status == "connected") Success else Danger
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Sync Interval",
                            fontFamily = DMSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = PurpleCore
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("15m", "30m", "1h").forEach { interval ->
                                val isSelected = syncInterval == interval
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) PurpleCore else Color.Transparent,
                                            BadgeShape
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) PurpleCore else PurpleBorder,
                                            BadgeShape
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = { syncInterval = interval }
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = interval,
                                        fontFamily = PlusJakartaSans,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else PurpleCore
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── SYNC STATUS ──
            item {
                SectionHeader(title = "▸ SYNC STATUS", isDark = isDark)
            }
            item {
                DevoraCard(isDark = isDark) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.AccessTime,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Today 10:27 AM",
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = textColor
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 1.dp,
                            color = dividerColor
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Background Sync",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = textColor
                            )
                            Switch(
                                checked = bgSync,
                                onCheckedChange = { bgSync = it },
                                colors = switchColors
                            )
                        }

                        Divider(
                            thickness = 1.dp,
                            color = dividerColor
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "WiFi Only",
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = textColor
                            )
                            Switch(
                                checked = wifiOnly,
                                onCheckedChange = { wifiOnly = it },
                                colors = switchColors
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 1.dp,
                            color = dividerColor
                        )

                        DevoraButton(
                            text = "Sync Now",
                            onClick = { },
                            variant = ButtonVariant.OUTLINE,
                            modifier = Modifier.fillMaxWidth(),
                            isDark = isDark
                        )
                    }
                }
            }

            // ── SIGN OUT ──
            item {
                DevoraButton(
                    text = "Sign Out",
                    onClick = { showSignOutDialog = true },
                    variant = ButtonVariant.DANGER,
                    modifier = Modifier.fillMaxWidth(),
                    isDark = isDark
                )
            }

            // ── VERSION ──
            item {
                Text(
                    text = "DEVORA v1.0.0 · Enterprise MDM",
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Sign Out Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = {
                Text(
                    text = "Sign Out?",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            },
            text = {
                Text(
                    text = "You will be signed out.",
                    fontFamily = DMSans,
                    color = TextMuted
                )
            },
            confirmButton = {
                DevoraButton(
                    text = "Sign Out",
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    },
                    variant = ButtonVariant.DANGER
                )
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(
                        text = "Cancel",
                        fontFamily = DMSans,
                        color = PurpleCore
                    )
                }
            }
        )
    }
}

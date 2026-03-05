package com.enterprise.devicemanager.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.enterprise.devicemanager.ui.components.GlassCard
import com.enterprise.devicemanager.ui.theme.MintGradient
import com.enterprise.devicemanager.ui.theme.MintGreen
import com.enterprise.devicemanager.ui.theme.PillShape
import com.enterprise.devicemanager.worker.DeviceSyncWorker

@Composable
fun SettingsScreen(
    isDark: Boolean,
    onThemeToggle: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var serverUrl by remember { mutableStateOf("https://mdm.enterprise.com/api/v1") }
    var connectionStatus by remember { mutableStateOf<Boolean?>(null) } // null = idle, true = success, false = failed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(20.dp))

        // 1. Admin profile GlassCard
        AdminProfileCard()

        Spacer(modifier = Modifier.height(24.dp))

        // 2. APPEARANCE Section
        SettingsSectionTitle("APPEARANCE")
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = MintGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Dark Mode",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Switch between light and dark",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                Switch(
                    checked = isDark,
                    onCheckedChange = { onThemeToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MintGreen,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.LightGray.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. DEVICE POLICIES Section
        SettingsSectionTitle("DEVICE POLICIES")
        DevicePoliciesSection()

        Spacer(modifier = Modifier.height(24.dp))

        // 4. SERVER CONFIG Section
        SettingsSectionTitle("SERVER CONFIG")
        ServerConfigSection(
            url = serverUrl,
            onUrlChange = { serverUrl = it },
            connectionStatus = connectionStatus,
            onTestConnection = {
                // Mock test
                connectionStatus = serverUrl.contains("enterprise.com")
            },
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5. SYNC Section
        SettingsSectionTitle("SYNC")
        SyncSection()

        Spacer(modifier = Modifier.height(32.dp))

        // 6. Logout button
        OutlinedButton(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = PillShape,
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Red.copy(alpha = 0.5f))),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
        ) {
            Text("Sign Out", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 7. App version
        Text(
            text = "Version 1.0.42 (Stable)",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Sign Out", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out of the administrator account?") },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun AdminProfileCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MintGradient),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AD",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Admin User", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Super Administrator", fontSize = 13.sp, color = MintGreen, fontWeight = FontWeight.Medium)
                Text(text = "admin@enterprise.com", fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = "Edit Profile",
                fontSize = 12.sp,
                color = MintGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { }
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
    )
}

@Composable
fun DevicePoliciesSection() {
    val policies = listOf(
        Triple(Icons.Default.Refresh, "Factory Reset Protection", "Prevent unauthorized resets"),
        Triple(Icons.Default.ScreenLockPortrait, "Screen Lock Enforcement", "Mandatory pin/biometric"),
        Triple(Icons.Default.AppRegistration, "App Install Restriction", "Whitelist only installations"),
        Triple(Icons.Default.CameraAlt, "Camera Disable Policy", "Disable camera globally")
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        policies.forEachIndexed { index, policy ->
            var checked by remember { mutableStateOf(index < 2) }
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(policy.first, contentDescription = null, tint = MintGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = policy.second, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = policy.third, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Switch(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = MintGreen)
                    )
                }
            }
        }
    }
}

@Composable
fun ServerConfigSection(
    url: String,
    onUrlChange: (String) -> Unit,
    connectionStatus: Boolean?,
    onTestConnection: () -> Unit,
    isDark: Boolean
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            TextField(
                value = url,
                onValueChange = onUrlChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = if (isDark) Color(0xFF374151) else Color(0xFFF5F7FA),
                    unfocusedContainerColor = if (isDark) Color(0xFF374151) else Color(0xFFF5F7FA),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onTestConnection,
                    shape = PillShape,
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(MintGreen)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MintGreen)
                ) {
                    Text("Test Connection", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                
                if (connectionStatus != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (connectionStatus) MintGreen else Color.Red)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (connectionStatus) "Connected" else "Failed",
                            color = if (connectionStatus) MintGreen else Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SyncSection() {
    val context = LocalContext.current
    var bgSync by remember { mutableStateOf(true) }
    var wifiOnly by remember { mutableStateOf(true) }
    var selectedInterval by remember { mutableStateOf("6h") }
    var syncTriggered by remember { mutableStateOf(false) }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Background Sync", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Switch(checked = bgSync, onCheckedChange = {
                    bgSync = it
                    if (it) DeviceSyncWorker.schedule(context)
                    else DeviceSyncWorker.cancel(context)
                }, colors = SwitchDefaults.colors(checkedTrackColor = MintGreen))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("WiFi only", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Switch(checked = wifiOnly, onCheckedChange = { wifiOnly = it }, colors = SwitchDefaults.colors(checkedTrackColor = MintGreen))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Sync Interval", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("1h", "6h", "12h").forEach { interval ->
                    val isSelected = selectedInterval == interval
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(PillShape)
                            .background(if (isSelected) MintGreen else Color.Transparent)
                            .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray.copy(alpha = 0.5f), PillShape)
                            .clickable { selectedInterval = interval }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = interval,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.Gray
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Sync Now button
            Button(
                onClick = {
                    DeviceSyncWorker.syncNow(context)
                    syncTriggered = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
            ) {
                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (syncTriggered) "Sync Queued ✓" else "Sync Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}

package com.enterprise.devicemanager.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enterprise.devicemanager.ui.theme.MintGreen
import com.enterprise.devicemanager.ui.theme.PillShape
import com.enterprise.devicemanager.util.PermissionHelper

/**
 * A composable card that checks for required permissions and prompts
 * the user to grant them. Shows a success indicator when all permissions
 * are granted.
 *
 * @param onPermissionsGranted Callback when all permissions are granted.
 */
@Composable
fun PermissionRequestCard(
    onPermissionsGranted: () -> Unit = {}
) {
    val context = LocalContext.current
    var allGranted by remember {
        mutableStateOf(PermissionHelper.hasDeviceInfoPermissions(context))
    }
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        allGranted = result.values.all { it }
        permissionDenied = !allGranted
        if (allGranted) {
            onPermissionsGranted()
        }
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (allGranted) Icons.Default.Security else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (allGranted) MintGreen else Color(0xFFFF9800),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Device Permissions",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (allGranted) "All permissions granted"
                        else "Required for device info collection",
                        fontSize = 12.sp,
                        color = if (allGranted) MintGreen else Color.Gray
                    )
                }
            }

            if (!allGranted) {
                Spacer(modifier = Modifier.height(12.dp))

                // Show which permissions are needed
                val missing = PermissionHelper.getMissingPermissions(
                    context,
                    PermissionHelper.DEVICE_INFO_PERMISSIONS
                )
                missing.forEach { perm ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFFF9800), RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = PermissionHelper.getPermissionDescription(perm),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                if (permissionDenied) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Permission denied. Grant it in Settings > Apps > Permissions.",
                        fontSize = 11.sp,
                        color = Color.Red.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        permissionLauncher.launch(PermissionHelper.DEVICE_INFO_PERMISSIONS)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                ) {
                    Text("Grant Permissions", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

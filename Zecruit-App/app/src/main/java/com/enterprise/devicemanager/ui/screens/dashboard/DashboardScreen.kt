package com.enterprise.devicemanager.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enterprise.devicemanager.data.model.DashboardStats
import com.enterprise.devicemanager.data.repository.DeviceRepository
import com.enterprise.devicemanager.ui.components.GlassCard
import com.enterprise.devicemanager.ui.theme.MintGreen
import com.enterprise.devicemanager.ui.theme.LightBackgroundGradient
import com.enterprise.devicemanager.ui.theme.DarkBackgroundGradient
import com.enterprise.devicemanager.ui.screens.devices.DevicesScreen
import com.enterprise.devicemanager.ui.screens.enrollment.EnrollmentScreen
import com.enterprise.devicemanager.ui.screens.settings.SettingsScreen
import com.enterprise.devicemanager.ui.screens.appinventory.AppInventoryScreen
import kotlinx.coroutines.launch

data class ActivityItem(
    val icon: ImageVector,
    val text: String,
    val time: String,
    val statusColor: Color
)

@Composable
fun DashboardScreen(
    isDark: Boolean,
    onThemeToggle: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val backgroundBrush = if (isDark) DarkBackgroundGradient else LightBackgroundGradient

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            DashboardBottomBar(selectedTab) { selectedTab = it }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> MainDashboardContent(isDark, onThemeToggle)
                1 -> DevicesScreen(isDark)
                2 -> EnrollmentScreen(isDark)
                3 -> AppInventoryScreen(isDark)
                4 -> SettingsScreen(isDark, onThemeToggle, onLogout)
            }
        }
    }
}

@Composable
fun MainDashboardContent(isDark: Boolean, onThemeToggle: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { DeviceRepository(context) }

    var stats by remember { mutableStateOf<DashboardStats?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch dashboard stats from backend
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            val result = repo.fetchDashboardStats()
            result.onSuccess { stats = it }
                .onFailure { errorMessage = it.localizedMessage }
            isLoading = false
        }
    }

    val activities = remember {
        listOf(
            ActivityItem(Icons.Default.Sync, "Device synchronized", "2m ago", MintGreen),
            ActivityItem(Icons.Default.Lock, "Remote lock applied", "15m ago", Color.Gray),
            ActivityItem(Icons.Default.Warning, "Security violation detected", "1h ago", Color.Red),
            ActivityItem(Icons.Default.CheckCircle, "New device enrollment complete", "3h ago", MintGreen)
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DashboardTopBar(isDark, onThemeToggle)
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { 
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MintGreen)
                    }
                } else {
                    StatsRow(stats)
                }
            }
            item { DeviceHealthCard(stats) }

            // Error message if backend unreachable
            errorMessage?.let { msg ->
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Offline mode: $msg", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            item { RecentActivityHeader() }
            items(activities) { item ->
                ActivityRow(item)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun DashboardTopBar(isDark: Boolean, onThemeToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Good Morning 👋", fontSize = 13.sp, color = Color.Gray)
            Text(text = "Administrator", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = MintGreen,
                    modifier = Modifier.size(26.dp)
                )
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(imageVector = Icons.Outlined.Notifications, contentDescription = null, tint = MintGreen, modifier = Modifier.size(26.dp))
            }
        }
    }
}

@Composable
fun StatsRow(stats: DashboardStats?) {
    val total = stats?.totalDevices?.toString() ?: "--"
    val active = stats?.activeDevices?.toString() ?: "--"
    val inactive = stats?.inactiveDevices?.toString() ?: "--"
    val apps = stats?.totalApps?.toString() ?: "--"

    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(total, "Total Devices", Icons.Default.Laptop, MintGreen)
        StatCard(active, "Active Now", Icons.Default.Circle, MintGreen, isAnimated = true)
        StatCard(inactive, "Inactive", Icons.Default.Warning, Color.Red)
        StatCard(apps, "Total Apps", Icons.Default.Apps, Color(0xFFFFB347))
    }
}

@Composable
fun StatCard(value: String, label: String, icon: ImageVector, iconColor: Color, isAnimated: Boolean = false) {
    GlassCard(modifier = Modifier.size(width = 160.dp, height = 110.dp)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.align(Alignment.TopEnd).size(32.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (isAnimated) {
                    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
                    val alpha by infiniteTransition.animateFloat(0.4f, 1f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "Alpha")
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor.copy(alpha = alpha), modifier = Modifier.size(14.dp))
                } else {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                }
            }
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = iconColor)
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DeviceHealthCard(stats: DashboardStats?) {
    val total = stats?.totalDevices ?: 0L
    val active = stats?.activeDevices ?: 0L
    val onlinePct = if (total > 0) (active.toFloat() / total) else 0f
    val offlinePct = if (total > 0) 1f - onlinePct else 0f

    Column {
        Text(text = "Device Health Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    HealthLabel("Online", "${(onlinePct * 100).toInt()}%", MintGreen)
                    HealthLabel("Offline", "${(offlinePct * 100).toInt()}%", Color.Gray)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Canvas(modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape)) {
                    drawRect(color = Color.LightGray.copy(alpha = 0.3f), size = size)
                    drawRect(color = MintGreen, size = size.copy(width = size.width * onlinePct))
                }
            }
        }
    }
}

@Composable
fun HealthLabel(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "$label: ", fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RecentActivityHeader() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = "See All", color = MintGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { })
    }
}

@Composable
fun ActivityRow(item: ActivityItem) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(MintGreen.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(item.icon, contentDescription = null, tint = MintGreen, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(item.statusColor))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = item.text, fontSize = 14.sp)
            }
            Text(text = item.time, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 14.dp))
        }
    }
}

@Composable
fun DashboardBottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp, modifier = Modifier.shadow(12.dp)) {
        val items = listOf(
            Triple("Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
            Triple("Devices", Icons.Default.Devices, Icons.Outlined.Devices),
            Triple("Enroll", Icons.Default.AddBox, Icons.Outlined.AddBox),
            Triple("Apps", Icons.Default.Apps, Icons.Outlined.Apps),
            Triple("Settings", Icons.Default.Settings, Icons.Outlined.Settings)
        )
        items.forEachIndexed { index, (label, sel, unsel) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = if (selectedTab == index) sel else unsel, contentDescription = label, tint = if (selectedTab == index) MintGreen else Color.Gray)
                        if (selectedTab == index) Box(modifier = Modifier.padding(top = 4.dp).size(4.dp).clip(CircleShape).background(MintGreen))
                    }
                },
                label = { Text(text = label, color = if (selectedTab == index) MintGreen else Color.Gray, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
        }
    }
}

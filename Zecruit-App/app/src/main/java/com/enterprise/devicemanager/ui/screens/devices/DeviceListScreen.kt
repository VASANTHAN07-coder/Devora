package com.enterprise.devicemanager.ui.screens.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enterprise.devicemanager.ui.components.GlassCard
import com.enterprise.devicemanager.ui.theme.MintGradient
import com.enterprise.devicemanager.ui.theme.MintGreen
import com.enterprise.devicemanager.ui.theme.PillShape

data class DeviceItem(
    val name: String,
    val model: String,
    val manufacturer: String,
    val status: String,
    val isOnline: Boolean,
    val androidVersion: String,
    val lastSeen: String
)

@Composable
fun DeviceListScreen(isDark: Boolean) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val mockDevices = remember {
        listOf(
            DeviceItem("Admin-MBP", "MacBook Pro M3", "Apple", "Online", true, "14.2", "2 mins ago"),
            DeviceItem("Pixel-8-Pro", "Pixel 8 Pro", "Google", "Online", true, "14.0", "5 mins ago"),
            DeviceItem("Manager-Tablet", "iPad Air 5", "Apple", "Offline", false, "17.1", "1 hour ago"),
            DeviceItem("Sales-Phone-01", "S23 Ultra", "Samsung", "Online", true, "13.0", "10 mins ago"),
            DeviceItem("Warehouse-Scanner", "TC52X", "Zebra", "Flagged", false, "11.0", "2 hours ago"),
            DeviceItem("Exec-Laptop", "XPS 13", "Dell", "Online", true, "11", "Just now"),
            DeviceItem("HR-Phone", "iPhone 15", "Apple", "Offline", false, "17.0", "5 hours ago"),
            DeviceItem("Dev-Tablet", "Galaxy Tab S9", "Samsung", "Online", true, "14", "1 min ago")
        )
    }

    val filteredDevices = mockDevices.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || it.model.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Active" -> it.isOnline
            "Offline" -> !it.isOnline
            "Flagged" -> it.status == "Flagged"
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search Bar
            SearchBar(searchQuery, onQueryChange = { searchQuery = it }, isDark = isDark)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Filter Chips
            FilterChips(selectedFilter, onFilterSelected = { selectedFilter = it }, isDark = isDark)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (filteredDevices.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(filteredDevices) { device ->
                        DeviceListCard(device, isDark)
                    }
                }
            }
        }

        // FAB
        EnrollFAB(modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp))
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, isDark: Boolean) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, PillShape)
            .clip(PillShape),
        placeholder = { Text("Search devices...", color = Color(0xFF8A9BB0), fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MintGreen) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = if (isDark) Color(0xFF1F2937) else Color.White,
            unfocusedContainerColor = if (isDark) Color(0xFF1F2937) else Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun FilterChips(selected: String, onFilterSelected: (String) -> Unit, isDark: Boolean) {
    val filters = listOf("All", "Active", "Offline", "Flagged")
    val gradient = MintGradient
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(filters) { filter ->
            val isSelected = selected == filter
            Box(
                modifier = Modifier
                    .clip(PillShape)
                    .background(if (isSelected) gradient else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = if (isSelected) Color.Transparent else Color(0xFFC8D5E8).copy(alpha = 0.5f),
                        shape = PillShape
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) Color.White else Color(0xFF8A9BB0),
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun DeviceListCard(device: DeviceItem, isDark: Boolean) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Initial Circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MintGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = device.name.first().toString(),
                    color = MintGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Center Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1A2332)
                )
                Text(
                    text = "${device.model} • ${device.manufacturer}",
                    fontSize = 12.sp,
                    color = Color(0xFF8A9BB0)
                )
                Text(
                    text = "Last seen ${device.lastSeen}",
                    fontSize = 11.sp,
                    color = Color(0xFF8A9BB0).copy(alpha = 0.7f)
                )
            }
            
            // Right Side
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (device.isOnline) "● Online" else "● Offline",
                    fontSize = 11.sp,
                    color = if (device.isOnline) MintGreen else Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFF0F4F8),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "Android ${device.androidVersion}",
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        color = if (isDark) Color.LightGray else Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF8A9BB0),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Simple Placeholder for Illustration
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.LightGray
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No devices found",
            fontSize = 16.sp,
            color = Color(0xFF8A9BB0)
        )
    }
}

@Composable
fun EnrollFAB(modifier: Modifier = Modifier) {
    val gradient = MintGradient
    ExtendedFloatingActionButton(
        onClick = { /* TODO */ },
        modifier = modifier
            .shadow(8.dp, PillShape),
        containerColor = Color.Transparent,
        contentColor = Color.White,
        shape = PillShape
    ) {
        Box(
            modifier = Modifier
                .background(brush = gradient, shape = PillShape)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enroll Device", fontWeight = FontWeight.Bold)
            }
        }
    }
}

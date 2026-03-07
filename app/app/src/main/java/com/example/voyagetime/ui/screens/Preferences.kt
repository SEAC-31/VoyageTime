package com.example.voyagetime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Preferences(
    modifier: Modifier = Modifier,
    onNavigateToAboutUs: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var locationAccess by remember { mutableStateOf(true) }
    var offlineMode by remember { mutableStateOf(false) }
    var autoSync by remember { mutableStateOf(true) }
    var showPrices by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Preferences",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // ── APPEARANCE ──────────────────────────────────────
        PreferenceCategory(title = "Appearance") {

            PreferenceToggleItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                subtitle = "Switch between light and dark theme",
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

            PreferenceButtonItem(
                icon = Icons.Default.Palette,
                title = "App Theme",
                subtitle = "Ocean Blue",
                onClick = {}
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

            PreferenceButtonItem(
                icon = Icons.Default.Language,
                title = "Language",
                subtitle = "English",
                onClick = {}
            )
        }

        // ── NOTIFICATIONS ────────────────────────────────────
        PreferenceCategory(title = "Notifications") {

            PreferenceToggleItem(
                icon = Icons.Default.Notifications,
                title = "Push Notifications",
                subtitle = "Receive alerts about your trips",
                checked = notifications,
                onCheckedChange = { notifications = it }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

            PreferenceToggleItem(
                icon = Icons.Default.LocationOn,
                title = "Location Access",
                subtitle = "Allow app to use your location",
                checked = locationAccess,
                onCheckedChange = { locationAccess = it }
            )
        }

        // ── DATA & STORAGE ───────────────────────────────────
        PreferenceCategory(title = "Data & Storage") {

            PreferenceToggleItem(
                icon = Icons.Default.WifiOff,
                title = "Offline Mode",
                subtitle = "Save trips for offline access",
                checked = offlineMode,
                onCheckedChange = { offlineMode = it }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

            PreferenceToggleItem(
                icon = Icons.Default.Sync,
                title = "Auto Sync",
                subtitle = "Sync data automatically in background",
                checked = autoSync,
                onCheckedChange = { autoSync = it }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

            PreferenceButtonItem(
                icon = Icons.Default.Delete,
                title = "Clear Cache",
                subtitle = "Free up storage space",
                onClick = {}
            )
        }

        // ── DISPLAY ──────────────────────────────────────────
        PreferenceCategory(title = "Display") {

            PreferenceToggleItem(
                icon = Icons.Default.AttachMoney,
                title = "Show Prices",
                subtitle = "Display estimated costs in trips",
                checked = showPrices,
                onCheckedChange = { showPrices = it }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

            PreferenceButtonItem(
                icon = Icons.Default.Flag,
                title = "Currency",
                subtitle = "EUR — Euro",
                onClick = {}
            )
        }

        // ── ACCOUNT ──────────────────────────────────────────
        PreferenceCategory(title = "Account") {

            PreferenceButtonItem(
                icon = Icons.Default.Person,
                title = "Edit Profile",
                subtitle = "Change your personal information",
                onClick = {}
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

            PreferenceButtonItem(
                icon = Icons.Default.Lock,
                title = "Privacy & Security",
                subtitle = "Manage your data and permissions",
                onClick = {}
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

            PreferenceButtonItem(
                icon = Icons.Default.Info,
                title = "About VoyageTime",
                subtitle = "Version 1.0",
                onClick = onNavigateToAboutUs
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

            PreferenceButtonItem(
                icon = Icons.Default.Logout,
                title = "Log Out",
                subtitle = "",
                onClick = {},
                isDestructive = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── COMPONENTS ───────────────────────────────────────────────

@Composable
fun PreferenceCategory(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun PreferenceToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun PreferenceButtonItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val contentColor = if (isDestructive)
        MaterialTheme.colorScheme.error
    else
        MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isDestructive)
                        MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDestructive)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
    }
}
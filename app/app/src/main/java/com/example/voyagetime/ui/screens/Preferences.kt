package com.example.voyagetime.ui.screens

import android.app.Activity
import android.content.Intent
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.voyagetime.AppScreen
import com.example.voyagetime.EXTRA_START_AFTER_SPLASH
import com.example.voyagetime.MainActivity
import com.example.voyagetime.R

private const val TAG = "Preferences"

@Composable
fun Preferences(
    modifier: Modifier = Modifier,
    onNavigateToAboutUs: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var locationAccess by remember { mutableStateOf(true) }
    var offlineMode by remember { mutableStateOf(false) }
    var autoSync by remember { mutableStateOf(true) }
    var showPrices by remember { mutableStateOf(true) }

    var currentLanguage by remember {
        mutableStateOf(LanguageManager.getSavedLanguage(context))
    }
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguagePickerDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { langCode ->
                if (langCode != currentLanguage) {
                    Log.i(TAG, "Language changed: $currentLanguage -> $langCode")
                    LanguageManager.saveLanguage(context, langCode)
                    currentLanguage = langCode
                    // Restart with splash → preferences (skip Terms)
                    val intent = Intent(context, MainActivity::class.java).apply {
                        putExtra(EXTRA_START_AFTER_SPLASH, AppScreen.MAIN.name)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                } else {
                    Log.d(TAG, "Language selected but unchanged: $langCode")
                }
                showLanguageDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.preferences_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // ── APPEARANCE ──────────────────────────────────────
        PreferenceCategory(title = stringResource(R.string.pref_section_appearance)) {
            PreferenceToggleItem(
                icon = Icons.Default.DarkMode,
                title = stringResource(R.string.pref_dark_mode),
                subtitle = stringResource(R.string.pref_dark_mode_sub),
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.pref_theme),
                subtitle = stringResource(R.string.pref_theme_sub),
                onClick = {}
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(
                icon = Icons.Default.Language,
                title = stringResource(R.string.pref_language),
                subtitle = languageDisplayName(currentLanguage),
                onClick = { showLanguageDialog = true }
            )
        }

        // ── NOTIFICATIONS ────────────────────────────────────
        PreferenceCategory(title = stringResource(R.string.pref_section_notifications)) {
            PreferenceToggleItem(
                icon = Icons.Default.Notifications,
                title = stringResource(R.string.pref_notifications),
                subtitle = stringResource(R.string.pref_notifications_sub),
                checked = notifications,
                onCheckedChange = { notifications = it }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceToggleItem(
                icon = Icons.Default.LocationOn,
                title = stringResource(R.string.pref_location),
                subtitle = stringResource(R.string.pref_location_sub),
                checked = locationAccess,
                onCheckedChange = { locationAccess = it }
            )
        }

        // ── DATA & STORAGE ───────────────────────────────────
        PreferenceCategory(title = stringResource(R.string.pref_section_data)) {
            PreferenceToggleItem(
                icon = Icons.Default.WifiOff,
                title = stringResource(R.string.pref_offline),
                subtitle = stringResource(R.string.pref_offline_sub),
                checked = offlineMode,
                onCheckedChange = { offlineMode = it }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceToggleItem(
                icon = Icons.Default.Sync,
                title = stringResource(R.string.pref_autosync),
                subtitle = stringResource(R.string.pref_autosync_sub),
                checked = autoSync,
                onCheckedChange = { autoSync = it }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(
                icon = Icons.Default.Delete,
                title = stringResource(R.string.pref_clear_cache),
                subtitle = stringResource(R.string.pref_clear_cache_sub),
                onClick = {}
            )
        }

        // ── DISPLAY ──────────────────────────────────────────
        PreferenceCategory(title = stringResource(R.string.pref_section_display)) {
            PreferenceToggleItem(
                icon = Icons.Default.AttachMoney,
                title = stringResource(R.string.pref_show_prices),
                subtitle = stringResource(R.string.pref_show_prices_sub),
                checked = showPrices,
                onCheckedChange = { showPrices = it }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(
                icon = Icons.Default.Flag,
                title = stringResource(R.string.pref_currency),
                subtitle = stringResource(R.string.pref_currency_sub),
                onClick = {}
            )
        }

        // ── ACCOUNT ──────────────────────────────────────────
        PreferenceCategory(title = stringResource(R.string.pref_section_account)) {
            PreferenceButtonItem(
                icon = Icons.Default.Person,
                title = stringResource(R.string.pref_edit_profile),
                subtitle = stringResource(R.string.pref_edit_profile_sub),
                onClick = {}
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.pref_privacy),
                subtitle = stringResource(R.string.pref_privacy_sub),
                onClick = {}
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.pref_about),
                subtitle = stringResource(R.string.pref_about_sub),
                onClick = onNavigateToAboutUs
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(
                icon = Icons.Default.Description,
                title = stringResource(R.string.pref_terms),
                subtitle = stringResource(R.string.pref_terms_sub),
                onClick = onNavigateToTerms
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(
                icon = Icons.Default.Logout,
                title = stringResource(R.string.pref_logout),
                subtitle = "",
                onClick = {},
                isDestructive = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── LANGUAGE PICKER DIALOG ───────────────────────────────────

@Composable
fun LanguagePickerDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    var selected by remember { mutableStateOf(currentLanguage) }

    val options = listOf(
        LanguageManager.LANG_EN to "English",
        LanguageManager.LANG_ES to "Castellano",
        LanguageManager.LANG_CA to "Català"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.lang_dialog_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                options.forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selected = code }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == code,
                            onClick = { selected = code },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = name, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.lang_dialog_cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onLanguageSelected(selected) }) {
                        Text(stringResource(R.string.lang_dialog_accept))
                    }
                }
            }
        }
    }
}

fun languageDisplayName(code: String): String = when (code) {
    LanguageManager.LANG_ES -> "Castellano"
    LanguageManager.LANG_CA -> "Català"
    else -> "English"
}

// ── COMPONENTS ───────────────────────────────────────────────

@Composable
fun PreferenceCategory(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.2.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) { Column(modifier = Modifier.fillMaxWidth()) { content() } }
    }
}

@Composable
fun PreferenceToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String, subtitle: String, checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = title,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle.isNotEmpty())
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun PreferenceButtonItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String, subtitle: String, onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val contentColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
            .background(if (isDestructive) MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = title,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = contentColor)
            if (subtitle.isNotEmpty())
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
    }
}
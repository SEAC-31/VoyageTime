package com.example.voyagetime.ui.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.example.voyagetime.LocalDarkMode
import com.example.voyagetime.LocalOnDarkModeChange
import com.example.voyagetime.R
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.ZoneOffset
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private const val TAG = "Preferences"
private val DOB_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

// ── VALIDATION HELPERS ────────────────────────────────────────

private fun validateUsername(
    value: String,
    emptyMessage: String,
    minMessage: String
): String? {
    if (value.isBlank()) return emptyMessage
    if (value.trim().length < 2) return minMessage
    return null
}

private fun validateDateOfBirth(
    value: String,
    futureMessage: String,
    invalidMessage: String
): String? {
    if (value.isBlank()) return null

    return try {
        val date = LocalDate.parse(value.trim(), DOB_FORMATTER)
        if (date.isAfter(LocalDate.now())) {
            futureMessage
        } else {
            null
        }
    } catch (_: DateTimeParseException) {
        invalidMessage
    }
}
// ── MAIN SCREEN ───────────────────────────────────────────────

@Composable
fun Preferences(
    modifier: Modifier = Modifier,
    onNavigateToAboutUs: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val darkMode = LocalDarkMode.current
    val onDarkModeChange = LocalOnDarkModeChange.current

    var notifications by remember { mutableStateOf(true) }
    var locationAccess by remember { mutableStateOf(true) }
    var offlineMode by remember { mutableStateOf(false) }
    var autoSync by remember { mutableStateOf(true) }
    var showPrices by remember { mutableStateOf(PreferencesManager.getShowPrices(context)) }
    var currency by remember { mutableStateOf(PreferencesManager.getCurrency(context)) }
    var currentLanguage by remember { mutableStateOf(LanguageManager.getSavedLanguage(context)) }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguagePickerDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { langCode ->
                if (langCode != currentLanguage) {
                    Log.i(TAG, "Language changed: $currentLanguage -> $langCode")
                    LanguageManager.saveLanguage(context, langCode)
                    currentLanguage = langCode
                    showLanguageDialog = false

                    (context as? Activity)?.finish()
                }
                showLanguageDialog = false
            }
        )
    }

    if (showProfileDialog) {
        EditProfileDialog(onDismiss = { showProfileDialog = false })
    }

    if (showCurrencyDialog) {
        CurrencyPickerDialog(
            currentCurrency = currency,
            onDismiss = { showCurrencyDialog = false },
            onCurrencySelected = { selected ->
                currency = selected
                PreferencesManager.saveCurrency(context, selected)
                showCurrencyDialog = false
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.preferences_title),
            fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        PreferenceCategory(title = stringResource(R.string.pref_section_appearance)) {
            PreferenceToggleItem(
                icon = Icons.Default.DarkMode,
                title = stringResource(R.string.pref_dark_mode),
                subtitle = stringResource(R.string.pref_dark_mode_sub),
                checked = darkMode,
                onCheckedChange = { onDarkModeChange(it) }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(icon = Icons.Default.Palette,
                title = stringResource(R.string.pref_theme),
                subtitle = stringResource(R.string.pref_theme_sub), onClick = {})
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(icon = Icons.Default.Language,
                title = stringResource(R.string.pref_language),
                subtitle = languageDisplayName(currentLanguage),
                onClick = { showLanguageDialog = true })
        }

        PreferenceCategory(title = stringResource(R.string.pref_section_notifications)) {
            PreferenceToggleItem(icon = Icons.Default.Notifications,
                title = stringResource(R.string.pref_notifications),
                subtitle = stringResource(R.string.pref_notifications_sub),
                checked = notifications, onCheckedChange = { notifications = it })
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceToggleItem(icon = Icons.Default.LocationOn,
                title = stringResource(R.string.pref_location),
                subtitle = stringResource(R.string.pref_location_sub),
                checked = locationAccess, onCheckedChange = { locationAccess = it })
        }

        PreferenceCategory(title = stringResource(R.string.pref_section_data)) {
            PreferenceToggleItem(icon = Icons.Default.WifiOff,
                title = stringResource(R.string.pref_offline),
                subtitle = stringResource(R.string.pref_offline_sub),
                checked = offlineMode, onCheckedChange = { offlineMode = it })
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceToggleItem(icon = Icons.Default.Sync,
                title = stringResource(R.string.pref_autosync),
                subtitle = stringResource(R.string.pref_autosync_sub),
                checked = autoSync, onCheckedChange = { autoSync = it })
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(icon = Icons.Default.Delete,
                title = stringResource(R.string.pref_clear_cache),
                subtitle = stringResource(R.string.pref_clear_cache_sub), onClick = {})
        }

        PreferenceCategory(title = stringResource(R.string.pref_section_display)) {
            PreferenceToggleItem(icon = Icons.Default.AttachMoney,
                title = stringResource(R.string.pref_show_prices),
                subtitle = stringResource(R.string.pref_show_prices_sub),
                checked = showPrices,
                onCheckedChange = { showPrices = it; PreferencesManager.saveShowPrices(context, it) })
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(icon = Icons.Default.Flag,
                title = stringResource(R.string.pref_currency),
                subtitle = currency, onClick = { showCurrencyDialog = true })
        }

        PreferenceCategory(title = stringResource(R.string.pref_section_account)) {
            PreferenceButtonItem(icon = Icons.Default.Person,
                title = stringResource(R.string.pref_edit_profile),
                subtitle = stringResource(R.string.pref_edit_profile_sub),
                onClick = { showProfileDialog = true })
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(icon = Icons.Default.Lock,
                title = stringResource(R.string.pref_privacy),
                subtitle = stringResource(R.string.pref_privacy_sub), onClick = {})
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(icon = Icons.Default.Info,
                title = stringResource(R.string.pref_about),
                subtitle = stringResource(R.string.pref_about_sub),
                onClick = onNavigateToAboutUs)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(icon = Icons.Default.Description,
                title = stringResource(R.string.pref_terms),
                subtitle = stringResource(R.string.pref_terms_sub),
                onClick = onNavigateToTerms)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            PreferenceButtonItem(icon = Icons.AutoMirrored.Filled.Logout,
                title = stringResource(R.string.pref_logout),
                subtitle = "", onClick = {}, isDestructive = true)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── EDIT PROFILE DIALOG ───────────────────────────────────────

@Composable
fun EditProfileDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    var username by remember { mutableStateOf(PreferencesManager.getUsername(context)) }
    var dateOfBirth by remember { mutableStateOf(PreferencesManager.getDateOfBirth(context)) }

    // Error messages — null means valid
    var usernameError by remember { mutableStateOf<String?>(null) }
    var dobError by remember { mutableStateOf<String?>(null) }

    val usernameEmptyError = stringResource(R.string.pref_username_empty)
    val usernameMinError = stringResource(R.string.pref_username_min)
    val dobFutureError = stringResource(R.string.pref_dob_future)
    val dobInvalidError = stringResource(R.string.pref_dob_invalid)

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(6.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {

                Text(stringResource(R.string.pref_edit_profile), fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; usernameError = null },
                    label = { Text(stringResource(R.string.profile_username)) },
                    singleLine = true,
                    isError = usernameError != null,
                    supportingText = {
                        if (usernameError != null)
                            Text(usernameError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date of birth field
                DateOfBirthPickerField(
                    value = dateOfBirth,
                    errorMessage = dobError,
                    onDateSelected = {
                        dateOfBirth = it
                        dobError = null
                    }
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.lang_dialog_cancel))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        // Validate all fields before saving
                        usernameError = validateUsername(
                            username,
                            usernameEmptyError,
                            usernameMinError
                        )

                        dobError = validateDateOfBirth(
                            dateOfBirth,
                            dobFutureError,
                            dobInvalidError
                        )

                        if (usernameError == null && dobError == null) {
                            PreferencesManager.saveUsername(context, username.trim())
                            PreferencesManager.saveDateOfBirth(context, dateOfBirth.trim())
                            Log.i(TAG, "Profile saved — username=${username.trim()}")
                            onDismiss()
                        } else {
                            Log.w(TAG, "Profile validation failed — usernameError=$usernameError dobError=$dobError")
                        }
                    }) {
                        Text(stringResource(R.string.lang_dialog_accept))
                    }
                }
            }
        }
    }
}

// ── CURRENCY DIALOG ───────────────────────────────────────────

@Composable
fun CurrencyPickerDialog(currentCurrency: String, onDismiss: () -> Unit, onCurrencySelected: (String) -> Unit) {
    var selected by remember { mutableStateOf(currentCurrency) }
    val options = listOf(
        "EUR" to stringResource(R.string.currency_eur),
        "USD" to stringResource(R.string.currency_usd),
        "GBP" to stringResource(R.string.currency_gbp),
        "JPY" to stringResource(R.string.currency_jpy)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(6.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.pref_currency), fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                options.forEach { (code, label) ->
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .clickable { selected = code }.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selected == code, onClick = { selected = code },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Spacer(Modifier.width(8.dp))
                        Text(label, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.lang_dialog_cancel)) }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onCurrencySelected(selected) }) { Text(stringResource(R.string.lang_dialog_accept)) }
                }
            }
        }
    }
}

// ── LANGUAGE DIALOG ───────────────────────────────────────────

@Composable
fun LanguagePickerDialog(currentLanguage: String, onDismiss: () -> Unit, onLanguageSelected: (String) -> Unit) {
    var selected by remember { mutableStateOf(currentLanguage) }
    val options = listOf(
        LanguageManager.LANG_EN to stringResource(R.string.lang_english),
        LanguageManager.LANG_ES to stringResource(R.string.lang_spanish),
        LanguageManager.LANG_CA to stringResource(R.string.lang_catalan)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(6.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.lang_dialog_title), fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                options.forEach { (code, name) ->
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .clickable { selected = code }.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selected == code, onClick = { selected = code },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Spacer(Modifier.width(8.dp))
                        Text(name, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.lang_dialog_cancel)) }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onLanguageSelected(selected) }) { Text(stringResource(R.string.lang_dialog_accept)) }
                }
            }
        }
    }
}

@Composable
fun languageDisplayName(code: String): String = when (code) {
    LanguageManager.LANG_ES -> stringResource(R.string.lang_spanish)
    LanguageManager.LANG_CA -> stringResource(R.string.lang_catalan)
    else -> stringResource(R.string.lang_english)
}

// ── REUSABLE COMPONENTS ───────────────────────────────────────

@Composable
fun PreferenceCategory(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(text = title.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp, color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(0.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) { content() }
        }
    }
}

@Composable
fun PreferenceToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String, subtitle: String, checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }
        .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = title,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground)
            if (subtitle.isNotEmpty())
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }
        .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
            .background(if (isDestructive) MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = title,
                tint = if (isDestructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                color = if (isDestructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onBackground)
            if (subtitle.isNotEmpty())
                Text(subtitle, fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

private fun LocalDate.toUtcMillis(): Long {
    return atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
}

private fun utcMillisToLocalDate(millis: Long): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateOfBirthPickerField(
    value: String,
    errorMessage: String?,
    onDateSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        label = { Text(stringResource(R.string.profile_dob)) },
        placeholder = { Text(stringResource(R.string.pref_dob_placeholder)) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        isError = errorMessage != null,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = stringResource(R.string.pref_select_dob)
            )
        },
        supportingText = {
            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    )

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = value
                .takeIf { it.isNotBlank() }
                ?.let {
                    runCatching { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE).toUtcMillis() }
                        .getOrNull()
                }
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            val selectedDate = utcMillisToLocalDate(selectedMillis)
                            onDateSelected(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        }
                        showDialog = false
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text(stringResource(R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.lang_dialog_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
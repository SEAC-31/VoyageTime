package com.example.voyagetime.ui.screens

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voyagetime.R

private const val TAG = "DepartureCityScreen"

// ── VALIDATION ────────────────────────────────────────────────

private fun validateCityOrCountry(value: String, fieldName: String): String? {
    if (value.isBlank()) return "$fieldName cannot be empty"
    if (value.trim().length < 2) return "$fieldName must be at least 2 characters"
    if (!value.trim().all { it.isLetter() || it.isWhitespace() || it == '-' })
        return "$fieldName can only contain letters and hyphens"
    return null
}

private fun validateAirportArea(value: String): String? {
    if (value.isBlank()) return "Airport area cannot be empty"
    if (value.trim().length < 3) return "Airport area must be at least 3 characters"
    return null
}

private enum class DepartureCityEditField { CITY, COUNTRY, AIRPORT_AREA }

@Composable
fun DepartureCityScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    val defaultCity = stringResource(R.string.departure_default_city)
    val defaultCountry = stringResource(R.string.departure_default_country)
    val defaultAirport = stringResource(R.string.departure_default_airport)

    var city by remember { mutableStateOf(defaultCity) }
    var country by remember { mutableStateOf(defaultCountry) }
    var airportArea by remember { mutableStateOf(defaultAirport) }

    var editingField by remember { mutableStateOf<DepartureCityEditField?>(null) }
    var draftText by remember { mutableStateOf("") }
    var draftError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(text = stringResource(R.string.departure_title), fontSize = 28.sp,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp))

        InfoSection(title = stringResource(R.string.departure_section_current)) {
            DetailRow(icon = Icons.Default.LocationCity,
                title = stringResource(R.string.departure_field_city), subtitle = city)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            DetailRow(icon = Icons.Default.Public,
                title = stringResource(R.string.departure_field_country), subtitle = country)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            DetailRow(icon = Icons.Default.HomeWork,
                title = stringResource(R.string.departure_field_airport), subtitle = airportArea)
        }

        InfoSection(title = stringResource(R.string.departure_section_edit)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // ── EDIT CITY ──────────────────────────────────
                Button(onClick = {
                    editingField = DepartureCityEditField.CITY
                    draftText = city
                    draftError = null
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.departure_btn_edit_city))
                }

                if (editingField == DepartureCityEditField.CITY) {
                    DepartureInlineEditor(
                        value = draftText,
                        label = stringResource(R.string.departure_label_city),
                        error = draftError,
                        onValueChange = { draftText = it; draftError = null },
                        onCancel = { editingField = null; draftText = ""; draftError = null },
                        onSave = {
                            draftError = validateCityOrCountry(draftText, "City")
                            if (draftError == null) {
                                city = draftText.trim()
                                Log.i(TAG, "City saved: $city")
                                editingField = null; draftText = ""
                            }
                        },
                        saveLabel = stringResource(R.string.departure_btn_save),
                        cancelLabel = stringResource(R.string.departure_btn_cancel)
                    )
                }

                // ── EDIT COUNTRY ───────────────────────────────
                Button(onClick = {
                    editingField = DepartureCityEditField.COUNTRY
                    draftText = country
                    draftError = null
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.departure_btn_edit_country))
                }

                if (editingField == DepartureCityEditField.COUNTRY) {
                    DepartureInlineEditor(
                        value = draftText,
                        label = stringResource(R.string.departure_label_country),
                        error = draftError,
                        onValueChange = { draftText = it; draftError = null },
                        onCancel = { editingField = null; draftText = ""; draftError = null },
                        onSave = {
                            draftError = validateCityOrCountry(draftText, "Country")
                            if (draftError == null) {
                                country = draftText.trim()
                                Log.i(TAG, "Country saved: $country")
                                editingField = null; draftText = ""
                            }
                        },
                        saveLabel = stringResource(R.string.departure_btn_save),
                        cancelLabel = stringResource(R.string.departure_btn_cancel)
                    )
                }

                // ── EDIT AIRPORT ───────────────────────────────
                Button(onClick = {
                    editingField = DepartureCityEditField.AIRPORT_AREA
                    draftText = airportArea
                    draftError = null
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.departure_btn_edit_airport))
                }

                if (editingField == DepartureCityEditField.AIRPORT_AREA) {
                    DepartureInlineEditor(
                        value = draftText,
                        label = stringResource(R.string.departure_label_airport),
                        error = draftError,
                        onValueChange = { draftText = it; draftError = null },
                        onCancel = { editingField = null; draftText = ""; draftError = null },
                        onSave = {
                            draftError = validateAirportArea(draftText)
                            if (draftError == null) {
                                airportArea = draftText.trim()
                                Log.i(TAG, "Airport area saved: $airportArea")
                                editingField = null; draftText = ""
                            }
                        },
                        saveLabel = stringResource(R.string.departure_btn_save),
                        cancelLabel = stringResource(R.string.departure_btn_cancel)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── INLINE EDITOR WITH VALIDATION ────────────────────────────

@Composable
fun DepartureInlineEditor(
    value: String, label: String, error: String?,
    onValueChange: (String) -> Unit,
    onCancel: () -> Unit, onSave: () -> Unit,
    saveLabel: String, cancelLabel: String
) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = error != null,
            supportingText = {
                if (error != null)
                    Text(error, color = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) { Text(cancelLabel) }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onSave) { Text(saveLabel) }
        }
    }
}

// ── SHARED COMPONENTS ─────────────────────────────────────────

@Composable
fun InfoSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)).padding(10.dp),
            contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = title,
                tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
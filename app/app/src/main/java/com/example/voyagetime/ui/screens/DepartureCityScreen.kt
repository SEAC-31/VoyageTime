package com.example.voyagetime.ui.screens

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

private enum class DepartureCityEditField { CITY, COUNTRY, AIRPORT_AREA }

@Composable
fun DepartureCityScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var airportArea by remember { mutableStateOf("") }

    // Load default values from strings (so they are translated)
    val defaultCity = stringResource(R.string.departure_default_city)
    val defaultCountry = stringResource(R.string.departure_default_country)
    val defaultAirport = stringResource(R.string.departure_default_airport)

    LaunchedEffect(Unit) {
        city = defaultCity
        country = defaultCountry
        airportArea = defaultAirport
    }

    var editingField by remember { mutableStateOf<DepartureCityEditField?>(null) }
    var draftText by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(text = stringResource(R.string.departure_title), fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(vertical = 8.dp))

        InfoSection(title = stringResource(R.string.departure_section_current)) {
            DetailRow(icon = Icons.Default.LocationCity, title = stringResource(R.string.departure_field_city), subtitle = city)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            DetailRow(icon = Icons.Default.Public, title = stringResource(R.string.departure_field_country), subtitle = country)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            DetailRow(icon = Icons.Default.HomeWork, title = stringResource(R.string.departure_field_airport), subtitle = airportArea)
        }

        InfoSection(title = stringResource(R.string.departure_section_edit)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { editingField = DepartureCityEditField.CITY; draftText = city }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.departure_btn_edit_city))
                }
                if (editingField == DepartureCityEditField.CITY) {
                    DepartureInlineEditor(value = draftText, label = stringResource(R.string.departure_label_city),
                        onValueChange = { draftText = it },
                        onCancel = { editingField = null; draftText = "" },
                        onSave = { city = draftText; editingField = null; draftText = "" },
                        saveLabel = stringResource(R.string.departure_btn_save),
                        cancelLabel = stringResource(R.string.departure_btn_cancel))
                }

                Button(onClick = { editingField = DepartureCityEditField.COUNTRY; draftText = country }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.departure_btn_edit_country))
                }
                if (editingField == DepartureCityEditField.COUNTRY) {
                    DepartureInlineEditor(value = draftText, label = stringResource(R.string.departure_label_country),
                        onValueChange = { draftText = it },
                        onCancel = { editingField = null; draftText = "" },
                        onSave = { country = draftText; editingField = null; draftText = "" },
                        saveLabel = stringResource(R.string.departure_btn_save),
                        cancelLabel = stringResource(R.string.departure_btn_cancel))
                }

                Button(onClick = { editingField = DepartureCityEditField.AIRPORT_AREA; draftText = airportArea }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.departure_btn_edit_airport))
                }
                if (editingField == DepartureCityEditField.AIRPORT_AREA) {
                    DepartureInlineEditor(value = draftText, label = stringResource(R.string.departure_label_airport),
                        onValueChange = { draftText = it },
                        onCancel = { editingField = null; draftText = "" },
                        onSave = { airportArea = draftText; editingField = null; draftText = "" },
                        saveLabel = stringResource(R.string.departure_btn_save),
                        cancelLabel = stringResource(R.string.departure_btn_cancel))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DepartureInlineEditor(
    value: String, label: String,
    onValueChange: (String) -> Unit,
    onCancel: () -> Unit, onSave: () -> Unit,
    saveLabel: String, cancelLabel: String
) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) },
            modifier = Modifier.fillMaxWidth(), singleLine = false)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) { Text(cancelLabel) }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onSave) { Text(saveLabel) }
        }
    }
}

@Composable
fun InfoSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(text = title.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.2.sp,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) { content() }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)).padding(10.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}
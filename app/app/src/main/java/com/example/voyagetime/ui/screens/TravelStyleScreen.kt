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

private enum class TravelStyleEditField { SELECTED_STYLE, MAIN_INTEREST, SECONDARY_INTEREST }

@Composable
fun TravelStyleScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    val defaultStyle = stringResource(R.string.travel_style_default_style)
    val defaultMain = stringResource(R.string.travel_style_default_main)
    val defaultSecondary = stringResource(R.string.travel_style_default_secondary)

    var selectedStyle by remember { mutableStateOf("") }
    var mainInterest by remember { mutableStateOf("") }
    var secondaryInterest by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        selectedStyle = defaultStyle
        mainInterest = defaultMain
        secondaryInterest = defaultSecondary
    }

    var editingField by remember { mutableStateOf<TravelStyleEditField?>(null) }
    var draftText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)) {

        Text(text = stringResource(R.string.travel_style_title), fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(vertical = 8.dp))

        StyleSection(title = stringResource(R.string.travel_style_section_current)) {
            StyleDetailRow(icon = Icons.Default.Explore, title = stringResource(R.string.travel_style_field_style), subtitle = selectedStyle)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            StyleDetailRow(icon = Icons.Default.Museum, title = stringResource(R.string.travel_style_field_main), subtitle = mainInterest)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            StyleDetailRow(icon = Icons.Default.Fastfood, title = stringResource(R.string.travel_style_field_secondary), subtitle = secondaryInterest)
        }

        StyleSection(title = stringResource(R.string.travel_style_section_edit)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { editingField = TravelStyleEditField.SELECTED_STYLE; draftText = selectedStyle }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.travel_style_btn_edit_style))
                }
                if (editingField == TravelStyleEditField.SELECTED_STYLE) {
                    InlineEditor(value = draftText, label = stringResource(R.string.travel_style_label_style),
                        onValueChange = { draftText = it },
                        onCancel = { editingField = null; draftText = "" },
                        onSave = { selectedStyle = draftText; editingField = null; draftText = "" },
                        saveLabel = stringResource(R.string.travel_style_btn_save),
                        cancelLabel = stringResource(R.string.travel_style_btn_cancel))
                }
                Button(onClick = { editingField = TravelStyleEditField.MAIN_INTEREST; draftText = mainInterest }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.travel_style_btn_edit_main))
                }
                if (editingField == TravelStyleEditField.MAIN_INTEREST) {
                    InlineEditor(value = draftText, label = stringResource(R.string.travel_style_label_main),
                        onValueChange = { draftText = it },
                        onCancel = { editingField = null; draftText = "" },
                        onSave = { mainInterest = draftText; editingField = null; draftText = "" },
                        saveLabel = stringResource(R.string.travel_style_btn_save),
                        cancelLabel = stringResource(R.string.travel_style_btn_cancel))
                }
                Button(onClick = { editingField = TravelStyleEditField.SECONDARY_INTEREST; draftText = secondaryInterest }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.travel_style_btn_edit_secondary))
                }
                if (editingField == TravelStyleEditField.SECONDARY_INTEREST) {
                    InlineEditor(value = draftText, label = stringResource(R.string.travel_style_label_secondary),
                        onValueChange = { draftText = it },
                        onCancel = { editingField = null; draftText = "" },
                        onSave = { secondaryInterest = draftText; editingField = null; draftText = "" },
                        saveLabel = stringResource(R.string.travel_style_btn_save),
                        cancelLabel = stringResource(R.string.travel_style_btn_cancel))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun InlineEditor(value: String, label: String, onValueChange: (String) -> Unit,
                 onCancel: () -> Unit, onSave: () -> Unit, saveLabel: String, cancelLabel: String) {
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
fun StyleSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
fun StyleDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
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
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

private const val TAG = "TravelStyleScreen"

// ── VALIDATION ────────────────────────────────────────────────

private fun validateStyleField(value: String, fieldName: String): String? {
    if (value.isBlank()) return "$fieldName cannot be empty"
    if (value.trim().length < 3) return "$fieldName must be at least 3 characters"
    if (value.trim().length > 100) return "$fieldName is too long (max 100 characters)"
    return null
}

private enum class TravelStyleEditField { SELECTED_STYLE, MAIN_INTEREST, SECONDARY_INTEREST }

@Composable
fun TravelStyleScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    val defaultStyle = stringResource(R.string.travel_style_default_style)
    val defaultMain = stringResource(R.string.travel_style_default_main)
    val defaultSecondary = stringResource(R.string.travel_style_default_secondary)

    var selectedStyle by remember { mutableStateOf(defaultStyle) }
    var mainInterest by remember { mutableStateOf(defaultMain) }
    var secondaryInterest by remember { mutableStateOf(defaultSecondary) }

    var editingField by remember { mutableStateOf<TravelStyleEditField?>(null) }
    var draftText by remember { mutableStateOf("") }
    var draftError by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize().verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)) {

        Text(text = stringResource(R.string.travel_style_title), fontSize = 28.sp,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp))

        StyleSection(title = stringResource(R.string.travel_style_section_current)) {
            StyleDetailRow(icon = Icons.Default.Explore,
                title = stringResource(R.string.travel_style_field_style), subtitle = selectedStyle)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            StyleDetailRow(icon = Icons.Default.Museum,
                title = stringResource(R.string.travel_style_field_main), subtitle = mainInterest)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            StyleDetailRow(icon = Icons.Default.Fastfood,
                title = stringResource(R.string.travel_style_field_secondary), subtitle = secondaryInterest)
        }

        StyleSection(title = stringResource(R.string.travel_style_section_edit)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // ── EDIT STYLE ─────────────────────────────────
                Button(onClick = {
                    editingField = TravelStyleEditField.SELECTED_STYLE
                    draftText = selectedStyle; draftError = null
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.travel_style_btn_edit_style))
                }
                if (editingField == TravelStyleEditField.SELECTED_STYLE) {
                    StyleInlineEditor(
                        value = draftText, label = stringResource(R.string.travel_style_label_style),
                        error = draftError,
                        onValueChange = { draftText = it; draftError = null },
                        onCancel = { editingField = null; draftText = ""; draftError = null },
                        onSave = {
                            draftError = validateStyleField(draftText, "Travel style")
                            if (draftError == null) {
                                selectedStyle = draftText.trim()
                                Log.i(TAG, "Style saved: $selectedStyle")
                                editingField = null; draftText = ""
                            }
                        },
                        saveLabel = stringResource(R.string.travel_style_btn_save),
                        cancelLabel = stringResource(R.string.travel_style_btn_cancel)
                    )
                }

                // ── EDIT MAIN INTEREST ─────────────────────────
                Button(onClick = {
                    editingField = TravelStyleEditField.MAIN_INTEREST
                    draftText = mainInterest; draftError = null
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.travel_style_btn_edit_main))
                }
                if (editingField == TravelStyleEditField.MAIN_INTEREST) {
                    StyleInlineEditor(
                        value = draftText, label = stringResource(R.string.travel_style_label_main),
                        error = draftError,
                        onValueChange = { draftText = it; draftError = null },
                        onCancel = { editingField = null; draftText = ""; draftError = null },
                        onSave = {
                            draftError = validateStyleField(draftText, "Main interest")
                            if (draftError == null) {
                                mainInterest = draftText.trim()
                                Log.i(TAG, "Main interest saved: $mainInterest")
                                editingField = null; draftText = ""
                            }
                        },
                        saveLabel = stringResource(R.string.travel_style_btn_save),
                        cancelLabel = stringResource(R.string.travel_style_btn_cancel)
                    )
                }

                // ── EDIT SECONDARY INTEREST ────────────────────
                Button(onClick = {
                    editingField = TravelStyleEditField.SECONDARY_INTEREST
                    draftText = secondaryInterest; draftError = null
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.travel_style_btn_edit_secondary))
                }
                if (editingField == TravelStyleEditField.SECONDARY_INTEREST) {
                    StyleInlineEditor(
                        value = draftText, label = stringResource(R.string.travel_style_label_secondary),
                        error = draftError,
                        onValueChange = { draftText = it; draftError = null },
                        onCancel = { editingField = null; draftText = ""; draftError = null },
                        onSave = {
                            draftError = validateStyleField(draftText, "Secondary interest")
                            if (draftError == null) {
                                secondaryInterest = draftText.trim()
                                Log.i(TAG, "Secondary interest saved: $secondaryInterest")
                                editingField = null; draftText = ""
                            }
                        },
                        saveLabel = stringResource(R.string.travel_style_btn_save),
                        cancelLabel = stringResource(R.string.travel_style_btn_cancel)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── INLINE EDITOR WITH VALIDATION ────────────────────────────

@Composable
fun StyleInlineEditor(
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
                else
                    Text("Min 3 characters, max 100",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = 2
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
fun StyleSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
fun StyleDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
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
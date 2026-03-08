package com.example.voyagetime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class TravelStyleEditField {
    SELECTED_STYLE,
    MAIN_INTEREST,
    SECONDARY_INTEREST
}

@Composable
fun TravelStyleScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    var selectedStyle by remember { mutableStateOf("City break, culture and food") }
    var mainInterest by remember { mutableStateOf("Museums, landmarks and cultural routes") }
    var secondaryInterest by remember { mutableStateOf("Local food and cafés") }

    var editingField by remember { mutableStateOf<TravelStyleEditField?>(null) }
    var draftText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Travel Style",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        StyleSection(title = "Current Style") {
            StyleDetailRow(
                icon = Icons.Default.Explore,
                title = "Selected style",
                subtitle = selectedStyle
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            StyleDetailRow(
                icon = Icons.Default.Museum,
                title = "Main interest",
                subtitle = mainInterest
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            StyleDetailRow(
                icon = Icons.Default.Fastfood,
                title = "Secondary interest",
                subtitle = secondaryInterest
            )
        }

        StyleSection(title = "Edit Options") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        editingField = TravelStyleEditField.SELECTED_STYLE
                        draftText = selectedStyle
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Selected Style")
                }

                if (editingField == TravelStyleEditField.SELECTED_STYLE) {
                    InlineEditor(
                        value = draftText,
                        label = "Selected style",
                        onValueChange = { draftText = it },
                        onCancel = {
                            editingField = null
                            draftText = ""
                        },
                        onSave = {
                            selectedStyle = draftText
                            editingField = null
                            draftText = ""
                        }
                    )
                }

                Button(
                    onClick = {
                        editingField = TravelStyleEditField.MAIN_INTEREST
                        draftText = mainInterest
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Main Interest")
                }

                if (editingField == TravelStyleEditField.MAIN_INTEREST) {
                    InlineEditor(
                        value = draftText,
                        label = "Main interest",
                        onValueChange = { draftText = it },
                        onCancel = {
                            editingField = null
                            draftText = ""
                        },
                        onSave = {
                            mainInterest = draftText
                            editingField = null
                            draftText = ""
                        }
                    )
                }

                Button(
                    onClick = {
                        editingField = TravelStyleEditField.SECONDARY_INTEREST
                        draftText = secondaryInterest
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Secondary Interest")
                }

                if (editingField == TravelStyleEditField.SECONDARY_INTEREST) {
                    InlineEditor(
                        value = draftText,
                        label = "Secondary interest",
                        onValueChange = { draftText = it },
                        onCancel = {
                            editingField = null
                            draftText = ""
                        },
                        onSave = {
                            secondaryInterest = draftText
                            editingField = null
                            draftText = ""
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun InlineEditor(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = onSave) {
                Text("Save")
            }
        }
    }
}

@Composable
fun StyleSection(
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
fun StyleDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
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
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
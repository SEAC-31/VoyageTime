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
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Public
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

private enum class DepartureCityEditField {
    CITY,
    COUNTRY,
    AIRPORT_AREA
}

@Composable
fun DepartureCityScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    var city by remember { mutableStateOf("Barcelona") }
    var country by remember { mutableStateOf("Spain") }
    var airportArea by remember { mutableStateOf("Barcelona metropolitan area") }

    var editingField by remember { mutableStateOf<DepartureCityEditField?>(null) }
    var draftText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Departure City",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        InfoSection(title = "Current City") {
            DetailRow(
                icon = Icons.Default.LocationCity,
                title = "Selected departure city",
                subtitle = city
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            DetailRow(
                icon = Icons.Default.Public,
                title = "Country",
                subtitle = country
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            DetailRow(
                icon = Icons.Default.HomeWork,
                title = "Preferred airport area",
                subtitle = airportArea
            )
        }

        InfoSection(title = "Edit Options") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        editingField = DepartureCityEditField.CITY
                        draftText = city
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Departure City")
                }

                if (editingField == DepartureCityEditField.CITY) {
                    DepartureInlineEditor(
                        value = draftText,
                        label = "Departure city",
                        onValueChange = { draftText = it },
                        onCancel = {
                            editingField = null
                            draftText = ""
                        },
                        onSave = {
                            city = draftText
                            editingField = null
                            draftText = ""
                        }
                    )
                }

                Button(
                    onClick = {
                        editingField = DepartureCityEditField.COUNTRY
                        draftText = country
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Country")
                }

                if (editingField == DepartureCityEditField.COUNTRY) {
                    DepartureInlineEditor(
                        value = draftText,
                        label = "Country",
                        onValueChange = { draftText = it },
                        onCancel = {
                            editingField = null
                            draftText = ""
                        },
                        onSave = {
                            country = draftText
                            editingField = null
                            draftText = ""
                        }
                    )
                }

                Button(
                    onClick = {
                        editingField = DepartureCityEditField.AIRPORT_AREA
                        draftText = airportArea
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Airport Area")
                }

                if (editingField == DepartureCityEditField.AIRPORT_AREA) {
                    DepartureInlineEditor(
                        value = draftText,
                        label = "Preferred airport area",
                        onValueChange = { draftText = it },
                        onCancel = {
                            editingField = null
                            draftText = ""
                        },
                        onSave = {
                            airportArea = draftText
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
fun DepartureInlineEditor(
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
fun InfoSection(
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
fun DetailRow(
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
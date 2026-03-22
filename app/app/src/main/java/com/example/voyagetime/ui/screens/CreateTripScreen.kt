package com.example.voyagetime.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voyagetime.R

@Composable
fun CreateTripScreen(modifier: Modifier = Modifier, onCancel: () -> Unit = {}) {
    val scrollState = rememberScrollState()
    val destination = remember { mutableStateOf("") }
    val country = remember { mutableStateOf("") }
    val startDate = remember { mutableStateOf("") }
    val endDate = remember { mutableStateOf("") }
    val budget = remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(text = stringResource(R.string.create_trip_title), fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(vertical = 8.dp))
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = destination.value, onValueChange = { destination.value = it },
                    label = { Text(stringResource(R.string.create_trip_field_destination)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = country.value, onValueChange = { country.value = it },
                    label = { Text(stringResource(R.string.create_trip_field_country)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = startDate.value, onValueChange = { startDate.value = it },
                    label = { Text(stringResource(R.string.create_trip_field_start)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = endDate.value, onValueChange = { endDate.value = it },
                    label = { Text(stringResource(R.string.create_trip_field_end)) }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = budget.value, onValueChange = { budget.value = it },
                    label = { Text(stringResource(R.string.create_trip_field_budget)) }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.create_trip_btn_create))
                }
                TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.create_trip_btn_cancel))
                }
            }
        }
    }
}
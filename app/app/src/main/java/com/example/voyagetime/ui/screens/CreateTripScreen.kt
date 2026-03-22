package com.example.voyagetime.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voyagetime.ui.viewmodels.CreateTripViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@Composable
fun CreateTripScreen(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onTripCreated: () -> Unit = {},
    viewModel: CreateTripViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    var destination by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    val destinationError = validateRequiredField(destination, "Destination")
    val countryError = validateRequiredField(country, "Country")
    val startDateError = validateSingleDateField(startDate, "Start date")
    val endDateError = validateSingleDateField(endDate, "End date")
    val dateOrderError = validateDateOrder(startDate, endDate)
    val budgetError = validateBudgetField(budget)

    val canCreate = destinationError == null &&
            countryError == null &&
            startDateError == null &&
            endDateError == null &&
            dateOrderError == null &&
            budgetError == null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Create Trip",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = destinationError != null,
                    supportingText = {
                        if (destinationError != null) {
                            Text(destinationError)
                        }
                    }
                )

                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = countryError != null,
                    supportingText = {
                        if (countryError != null) {
                            Text(countryError)
                        }
                    }
                )

                OutlinedTextField(
                    value = startDate,
                    onValueChange = { newValue ->
                        startDate = filterDateInput(newValue)
                    },
                    label = { Text("Start Date") },
                    placeholder = { Text("12 Jun 2026") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = startDateError != null,
                    supportingText = {
                        if (startDateError != null) {
                            Text(startDateError)
                        }
                    }
                )

                OutlinedTextField(
                    value = endDate,
                    onValueChange = { newValue ->
                        endDate = filterDateInput(newValue)
                    },
                    label = { Text("End Date") },
                    placeholder = { Text("18 Jun 2026") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = endDateError != null || dateOrderError != null,
                    supportingText = {
                        when {
                            endDateError != null -> Text(endDateError)
                            dateOrderError != null -> Text(dateOrderError)
                        }
                    }
                )

                OutlinedTextField(
                    value = budget,
                    onValueChange = { newValue ->
                        budget = newValue.filter { it.isDigit() }
                    },
                    label = { Text("Budget") },
                    placeholder = { Text("820") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = budgetError != null,
                    supportingText = {
                        if (budgetError != null) {
                            Text(budgetError)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.createTrip(
                            destination = destination,
                            country = country,
                            startDate = startDate,
                            endDate = endDate,
                            budget = budget
                        )
                        onTripCreated()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canCreate
                ) {
                    Text("Create Trip")
                }

                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

private fun validateRequiredField(value: String, fieldName: String): String? {
    return if (value.trim().isBlank()) {
        "$fieldName is required"
    } else {
        null
    }
}

private fun validateBudgetField(value: String): String? {
    if (value.isBlank()) return "Budget is required"
    if (!value.all { it.isDigit() }) return "Budget must contain numbers only"
    return null
}

private fun validateSingleDateField(value: String, fieldName: String): String? {
    if (value.trim().isBlank()) return "$fieldName is required"
    return if (parseCreateTripDate(value) == null) {
        "Use a real date like 12 Jun 2026"
    } else {
        null
    }
}

private fun validateDateOrder(startDate: String, endDate: String): String? {
    val start = parseCreateTripDate(startDate) ?: return null
    val end = parseCreateTripDate(endDate) ?: return null

    return if (end.isBefore(start)) {
        "End date cannot be before start date"
    } else {
        null
    }
}

private fun filterDateInput(value: String): String {
    return value.filter { char ->
        char.isDigit() || char.isLetter() || char == ' '
    }
}

private fun parseCreateTripDate(value: String): LocalDate? {
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
    return try {
        LocalDate.parse(value.trim(), formatter)
    } catch (_: DateTimeParseException) {
        null
    }
}
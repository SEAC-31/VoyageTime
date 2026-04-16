package com.example.voyagetime.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voyagetime.ui.viewmodels.CreateTripViewModel
import com.example.voyagetime.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private val CREATE_TRIP_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy")


@Composable
private fun localizedCreateTripDuration(rawDuration: String): String {
    val days = rawDuration.substringBefore(" ").trim().toIntOrNull() ?: return rawDuration
    return if (days == 1) {
        stringResource(R.string.duration_single_day)
    } else {
        stringResource(R.string.duration_multiple_days, days)
    }
}

@Composable
fun CreateTripScreen(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onTripCreated: () -> Unit = {},
    viewModel: CreateTripViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    var destination by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var dateRange by rememberSaveable { mutableStateOf("") }
    var budget by rememberSaveable { mutableStateOf("") }

    val destinationError = validateCreateTripRequiredField(destination, stringResource(R.string.validation_destination_required))
    val countryError = validateCreateTripRequiredField(country, stringResource(R.string.validation_country_required))
    val dateRangeError = validateCreateTripDateRange(
        dateRange,
        requiredMessage = stringResource(R.string.validation_date_range_required),
        exampleMessage = stringResource(R.string.validation_date_range_example),
        pastMessage = stringResource(R.string.validation_date_past),
        endBeforeStartMessage = stringResource(R.string.validation_end_before_start)
    )
    val computedDuration = calculateCreateTripDuration(dateRange)
    val budgetError = validateCreateTripBudgetField(
        budget,
        requiredMessage = stringResource(R.string.validation_budget_required),
        digitsOnlyMessage = stringResource(R.string.validation_budget_digits)
    )

    val canCreate = destinationError == null &&
            countryError == null &&
            dateRangeError == null &&
            computedDuration != null &&
            budgetError == null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.create_trip_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(20.dp)
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
                    label = { Text(stringResource(R.string.create_trip_field_destination)) },
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
                    label = { Text(stringResource(R.string.create_trip_field_country)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = countryError != null,
                    supportingText = {
                        if (countryError != null) {
                            Text(countryError)
                        }
                    }
                )

                CreateTripDateRangeField(
                    value = dateRange,
                    errorMessage = dateRangeError,
                    onValueChange = { dateRange = it }
                )

                CreateTripAutoDurationField(
                    value = computedDuration ?: "",
                    errorMessage = if (dateRangeError == null && computedDuration == null) {
                        stringResource(R.string.field_select_valid_date_range)
                    } else {
                        null
                    }
                )

                OutlinedTextField(
                    value = budget,
                    onValueChange = { newValue ->
                        budget = newValue.filter { it.isDigit() }
                    },
                    label = { Text(stringResource(R.string.create_trip_field_budget)) },
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
                        val parsedRange = parseCreateTripDateRange(dateRange)
                        if (parsedRange != null) {
                            viewModel.createTrip(
                                destination = destination,
                                country = country,
                                startDate = parsedRange.first.format(CREATE_TRIP_DATE_FORMATTER),
                                endDate = parsedRange.second.format(CREATE_TRIP_DATE_FORMATTER),
                                budget = budget
                            )
                            onTripCreated()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canCreate
                ) {
                    Text(stringResource(R.string.create_trip_btn_create))
                }

                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.create_trip_btn_cancel))
                }
            }
        }
    }
}

private fun validateCreateTripRequiredField(value: String, requiredMessage: String): String? {
    return if (value.trim().isBlank()) {
        requiredMessage
    } else {
        null
    }
}

private fun validateCreateTripBudgetField(
    value: String,
    requiredMessage: String,
    digitsOnlyMessage: String
): String? {
    if (value.isBlank()) return requiredMessage
    if (!value.all { it.isDigit() }) return digitsOnlyMessage
    return null
}

private fun parseCreateTripDate(value: String): LocalDate? {
    return try {
        LocalDate.parse(value.trim(), CREATE_TRIP_DATE_FORMATTER)
    } catch (_: Throwable) {
        null
    }
}

private fun parseCreateTripDateRange(value: String): Pair<LocalDate, LocalDate>? {
    val input = value.trim()
    val fullPattern = Regex("""^(\d{2}/\d{2}/\d{4})\s+-\s+(\d{2}/\d{2}/\d{4})$""")
    val match = fullPattern.matchEntire(input) ?: return null
    val startDate = parseCreateTripDate(match.groupValues[1]) ?: return null
    val endDate = parseCreateTripDate(match.groupValues[2]) ?: return null
    return startDate to endDate
}

private fun validateCreateTripDateRange(
    value: String,
    requiredMessage: String,
    exampleMessage: String,
    pastMessage: String,
    endBeforeStartMessage: String
): String? {
    val trimmed = value.trim()
    if (trimmed.isBlank()) return requiredMessage

    val parsed = parseCreateTripDateRange(trimmed)
        ?: return exampleMessage

    val today = LocalDate.now()
    val startDate = parsed.first
    val endDate = parsed.second

    if (startDate.isBefore(today) || endDate.isBefore(today)) {
        return pastMessage
    }

    if (endDate.isBefore(startDate)) {
        return endBeforeStartMessage
    }

    return null
}

private fun calculateCreateTripDuration(value: String): String? {
    val parsed = parseCreateTripDateRange(value) ?: return null
    val totalDays = ChronoUnit.DAYS.between(parsed.first, parsed.second).toInt() + 1
    if (totalDays <= 0) return null
    return if (totalDays == 1) "1 day" else "$totalDays days"
}

private data class CreateTripCalendarDayCell(
    val date: LocalDate?,
    val isFromCurrentMonth: Boolean
)

private fun buildCreateTripMonthGrid(month: YearMonth): List<List<CreateTripCalendarDayCell>> {
    val firstDayOfMonth = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()
    val leadingEmptyCells = firstDayOfMonth.dayOfWeek.value - DayOfWeek.MONDAY.value

    val cells = MutableList(42) { index ->
        val dayNumber = index - leadingEmptyCells + 1
        if (dayNumber in 1..daysInMonth) {
            CreateTripCalendarDayCell(
                date = month.atDay(dayNumber),
                isFromCurrentMonth = true
            )
        } else {
            CreateTripCalendarDayCell(
                date = null,
                isFromCurrentMonth = false
            )
        }
    }

    return cells.chunked(7)
}

private fun isCreateTripDateInRange(
    date: LocalDate,
    startDate: LocalDate?,
    endDate: LocalDate?
): Boolean {
    if (startDate == null || endDate == null) return false
    return !date.isBefore(startDate) && !date.isAfter(endDate)
}

private fun isSameCreateTripDay(date: LocalDate?, other: LocalDate?): Boolean {
    return date != null && other != null && date == other
}

private fun formatCreateTripYearMonthLabel(yearMonth: YearMonth): String {
    val monthName = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    return "${monthName.replaceFirstChar { it.uppercase() }} ${yearMonth.year}"
}

@Composable
private fun CreateTripAutoDurationField(
    value: String,
    errorMessage: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(R.string.trips_field_duration),
            fontSize = 12.sp,
            color = if (errorMessage != null) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ),
            border = BorderStroke(
                1.dp,
                if (errorMessage != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                }
            )
        ) {
            Text(
                text = if (value.isBlank()) stringResource(R.string.field_duration_auto_placeholder) else localizedCreateTripDuration(value),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                color = if (value.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun CreateTripYearPickerDialog(
    currentYear: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var startYear by remember(currentYear) { mutableStateOf(currentYear - 5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_close))
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { startYear -= 12 }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.date_picker_prev_years)
                    )
                }

                Text(
                    text = "${startYear} - ${startYear + 11}",
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick = { startYear += 12 }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.date_picker_next_years)
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(3) { column ->
                            val year = startYear + row * 3 + column
                            val isSelected = year == currentYear

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onYearSelected(year) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    }
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = year.toString(),
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun CreateTripDateRangeDialog(
    initialStartDate: LocalDate?,
    initialEndDate: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, LocalDate) -> Unit
) {
    var displayedMonth by remember(initialStartDate, initialEndDate) {
        mutableStateOf(
            YearMonth.from(initialStartDate ?: initialEndDate ?: LocalDate.now())
        )
    }
    var selectedStartDate by remember(initialStartDate) { mutableStateOf(initialStartDate) }
    var selectedEndDate by remember(initialEndDate) { mutableStateOf(initialEndDate) }
    var showYearDialog by remember { mutableStateOf(false) }
    val today = remember { LocalDate.now() }

    val monthGrid = remember(displayedMonth) {
        buildCreateTripMonthGrid(displayedMonth)
    }

    val weekdayLabels = remember {
        DayOfWeek.values().map { day ->
            day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val start = selectedStartDate
                    val end = selectedEndDate
                    if (start != null && end != null) {
                        onConfirm(start, end)
                    }
                },
                enabled = selectedStartDate != null && selectedEndDate != null
            ) {
                Text(stringResource(R.string.action_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.create_trip_btn_cancel))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { displayedMonth = displayedMonth.minusMonths(1) }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.date_picker_prev_month)
                        )
                    }

                    Text(
                        text = formatCreateTripYearMonthLabel(displayedMonth),
                        modifier = Modifier.clickable { showYearDialog = true },
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(onClick = { displayedMonth = displayedMonth.plusMonths(1) }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = stringResource(R.string.date_picker_next_month)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    weekdayLabels.forEach { label ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    monthGrid.forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            week.forEach { cell ->
                                val date = cell.date
                                val isDisabled = date == null || date.isBefore(today)
                                val isStart = isSameCreateTripDay(date, selectedStartDate)
                                val isEnd = isSameCreateTripDay(date, selectedEndDate)
                                val isSingleDayRange =
                                    selectedStartDate != null &&
                                            selectedEndDate != null &&
                                            selectedStartDate == selectedEndDate &&
                                            isStart && isEnd

                                val isBetween = if (date != null) {
                                    isCreateTripDateInRange(date, selectedStartDate, selectedEndDate) &&
                                            !isStart && !isEnd
                                } else {
                                    false
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            when {
                                                isDisabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                                isSingleDayRange -> MaterialTheme.colorScheme.primary
                                                isStart || isEnd -> MaterialTheme.colorScheme.primary
                                                isBetween -> MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                                                else -> MaterialTheme.colorScheme.surface
                                            }
                                        )
                                        .clickable(enabled = !isDisabled) {
                                            if (date == null || date.isBefore(today)) return@clickable

                                            when {
                                                selectedStartDate == null -> {
                                                    selectedStartDate = date
                                                    selectedEndDate = null
                                                }

                                                selectedEndDate == null -> {
                                                    if (date.isBefore(selectedStartDate)) {
                                                        selectedStartDate = date
                                                    } else {
                                                        selectedEndDate = date
                                                    }
                                                }

                                                else -> {
                                                    selectedStartDate = date
                                                    selectedEndDate = null
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = date?.dayOfMonth?.toString().orEmpty(),
                                        color = when {
                                            date == null -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0f)
                                            isDisabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                                            isStart || isEnd -> MaterialTheme.colorScheme.onPrimary
                                            else -> MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (isStart || isEnd) {
                                            FontWeight.SemiBold
                                        } else {
                                            FontWeight.Normal
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = when {
                        selectedStartDate != null && selectedEndDate != null ->
                            "${selectedStartDate!!.format(CREATE_TRIP_DATE_FORMATTER)} - ${selectedEndDate!!.format(CREATE_TRIP_DATE_FORMATTER)}"
                        selectedStartDate != null ->
                            selectedStartDate!!.format(CREATE_TRIP_DATE_FORMATTER)
                        else ->
                            stringResource(R.string.date_picker_select_start_end)
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )

    if (showYearDialog) {
        CreateTripYearPickerDialog(
            currentYear = displayedMonth.year,
            onYearSelected = { selectedYear ->
                displayedMonth = YearMonth.of(selectedYear, displayedMonth.month)
                showYearDialog = false
            },
            onDismiss = { showYearDialog = false }
        )
    }
}

@Composable
private fun CreateTripDateRangeField(
    value: String,
    errorMessage: String?,
    onValueChange: (String) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(R.string.trips_field_date_range),
            fontSize = 12.sp,
            color = if (errorMessage != null) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                if (errorMessage != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (value.isBlank()) stringResource(R.string.date_picker_select_range) else value,
                    color = if (value.isBlank()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = stringResource(R.string.date_picker_select_range),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    if (showDialog) {
        val currentRange = parseCreateTripDateRange(value)

        CreateTripDateRangeDialog(
            initialStartDate = currentRange?.first,
            initialEndDate = currentRange?.second,
            onDismiss = { showDialog = false },
            onConfirm = { startDate, endDate ->
                onValueChange(
                    "${startDate.format(CREATE_TRIP_DATE_FORMATTER)} - ${endDate.format(CREATE_TRIP_DATE_FORMATTER)}"
                )
                showDialog = false
            }
        )
    }
}

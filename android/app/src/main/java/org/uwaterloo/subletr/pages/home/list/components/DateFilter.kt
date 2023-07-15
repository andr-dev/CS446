package org.uwaterloo.subletr.pages.home.list.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.pages.createlisting.DateInputButton
import org.uwaterloo.subletr.pages.createlisting.DatePickerBottomSheet
import org.uwaterloo.subletr.pages.home.list.HomeListUiState
import org.uwaterloo.subletr.theme.textFieldBorderColor
import org.uwaterloo.subletr.utils.parseUTCDateTimeToLocal
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFilter(
	currentDateRange: HomeListUiState.DateRange,
	updateDateFilter: (HomeListUiState.DateRange) -> Unit,
	coroutineScope: CoroutineScope,
	closeAction: () -> Unit,
) {
	var startButtonText by remember {
		mutableStateOf(
			if (currentDateRange.startingDate == null) ""
			else parseUTCDateTimeToLocal(currentDateRange.startingDate!!)
		)

	}
	var endButtonText by remember {
		mutableStateOf(
			if (currentDateRange.endingDate == null) ""
			else parseUTCDateTimeToLocal(currentDateRange.endingDate!!)
		)
	}
	val dateRangePickerState = rememberDateRangePickerState()
	var openDatePicker by rememberSaveable { mutableStateOf(false) }
	val datePickerBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

	BasicFilterLayout(
		modifier = Modifier.height(240.dp),
		titleId = R.string.dates,
		closeAction = closeAction,
		content = {
			Column(modifier = Modifier.fillMaxWidth(1.0f)) {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
					verticalAlignment = Alignment.CenterVertically,
				) {
					DateInputButton(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f)
							.border(
								dimensionResource(id = R.dimen.xxxs),
								textFieldBorderColor,
								RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
							),
						labelStringId = R.string.start_date,
						value = startButtonText,
						onClick = {
							coroutineScope.launch {
								openDatePicker = true
							}
						},
					)

					DateInputButton(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f)
							.border(
								dimensionResource(id = R.dimen.xxxs),
								textFieldBorderColor,
								RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl)),
							),
						labelStringId = R.string.end_date,
						value = endButtonText,
						onClick = {
							coroutineScope.launch {
								openDatePicker = true
							}
						})
				}

				if (openDatePicker) {
					DatePickerBottomSheet(
						datePickerBottomSheetState, dateRangePickerState,
						onDismissRequest = { openDatePicker = false },
						onClick = {
							coroutineScope.launch { datePickerBottomSheetState.hide() }
								.invokeOnCompletion {
									if (!datePickerBottomSheetState.isVisible) {
										openDatePicker = false
									}
									startButtonText = if (dateRangePickerState.selectedStartDateMillis != null) {
										displayDateFormatter.formatDate(
											dateRangePickerState.selectedStartDateMillis,
											locale = Locale.getDefault()
										)!!
									} else {
										""

									}
									endButtonText = if (dateRangePickerState.selectedEndDateMillis != null) {
										displayDateFormatter.formatDate(
											dateRangePickerState.selectedEndDateMillis,
											locale = Locale.getDefault()
										)!!
									} else {
										""
									}
								}
						},
					)
				}
			}

		},
		clearAction = {
			startButtonText = ""
			endButtonText = ""
		},
		updateFilterAndClose = {
//			TODO: Check if the end date is after the start date
			val newStartingDate = SimpleDateFormat("MM/dd/yyyy").parse(startButtonText)
			val newEndingDate = SimpleDateFormat("MM/dd/yyyy").parse(endButtonText)
			updateDateFilter(
				HomeListUiState.DateRange(
					startingDate = if (newStartingDate is Date)
						newStartingDate.toInstant().atOffset(ZoneOffset.UTC).format(
							storeDateFormatISO
						) else null,
					endingDate = if (newEndingDate is Date)
						newEndingDate.toInstant().atOffset(ZoneOffset.UTC).format(
							storeDateFormatISO
						) else null,
				),

				)
			closeAction()
		},
		revertInput = {
			startButtonText =
				if (currentDateRange.startingDate == null) ""
				else parseUTCDateTimeToLocal(currentDateRange.startingDate!!)

			endButtonText =
				if (currentDateRange.endingDate == null) ""
				else parseUTCDateTimeToLocal(currentDateRange.endingDate!!)
		}
	)

}

@OptIn(ExperimentalMaterial3Api::class)
private val displayDateFormatter =
	DatePickerDefaults.dateFormatter(selectedDateSkeleton = "MM/dd/yyyy")
private val storeDateFormatISO: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

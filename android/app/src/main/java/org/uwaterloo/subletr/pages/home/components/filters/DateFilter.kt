package org.uwaterloo.subletr.pages.home.components.filters

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import org.uwaterloo.subletr.components.bottomsheet.DatePickerBottomSheet
import org.uwaterloo.subletr.components.button.DateInputButton
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.theme.subletrPalette
import org.uwaterloo.subletr.utils.displayDateFormatter
import org.uwaterloo.subletr.utils.parseUTCDateTimeToLocal
import org.uwaterloo.subletr.utils.storeDateFormatISO
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale

@SuppressWarnings("CyclomaticComplexMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFilter(
	currentDateRange: HomePageUiState.DateRange,
	updateDateFilter: (HomePageUiState.DateRange) -> Unit,
	coroutineScope: CoroutineScope,
	closeAction: () -> Unit,
) {
	var startButtonText by remember {
		mutableStateOf(
			currentDateRange.startingDate?.let {
				parseUTCDateTimeToLocal(it)
			} ?: ""
		)

	}
	var endButtonText by remember {
		mutableStateOf(
			currentDateRange.endingDate?.let {
				parseUTCDateTimeToLocal(it)
			} ?: ""
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
								width = dimensionResource(id = R.dimen.xxxs),
								color = MaterialTheme.subletrPalette.textFieldBorderColor,
								shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl)),
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
								width = dimensionResource(id = R.dimen.xxxs),
								color = MaterialTheme.subletrPalette.textFieldBorderColor,
								shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl)),
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
									startButtonText =
										displayDateFormatter.formatDate(
											dateRangePickerState.selectedStartDateMillis,
											locale = Locale.getDefault()
										) ?: ""

									endButtonText =
										displayDateFormatter.formatDate(
											dateRangePickerState.selectedEndDateMillis,
											locale = Locale.getDefault()
										) ?: ""

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
			val newStartingDate = if (startButtonText.isNotEmpty()) SimpleDateFormat("MM/dd/yyyy").parse(startButtonText) else ""
			val newEndingDate =if (endButtonText.isNotEmpty())  SimpleDateFormat("MM/dd/yyyy").parse(endButtonText) else ""
			updateDateFilter(
				HomePageUiState.DateRange(
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
				currentDateRange.startingDate?.let {
					parseUTCDateTimeToLocal(it)
				} ?: ""

			endButtonText =
				currentDateRange.endingDate?.let {
					parseUTCDateTimeToLocal(it)
				} ?: ""
		}
	)
}



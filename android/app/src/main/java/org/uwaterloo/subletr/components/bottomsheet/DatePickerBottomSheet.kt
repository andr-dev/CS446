package org.uwaterloo.subletr.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.unit.dp
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.primaryBackgroundColor
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.textFieldBackgroundColor
import org.uwaterloo.subletr.theme.textOnSubletrPink
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerBottomSheet(
	bottomSheetState: SheetState,
	state: DateRangePickerState,
	onDismissRequest: () -> Unit,
	onClick: () -> Unit,
) {
	ModalBottomSheet(
		onDismissRequest = onDismissRequest,
		sheetState = bottomSheetState,
		content = {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(550.dp) // TODO: change this to not use explicit value
					.background(primaryBackgroundColor)
			) {
				LeaseDatePicker(state, onClick)
				Button(
					onClick = onClick,
					colors = ButtonDefaults.buttonColors(
						containerColor = subletrPink,
					),
					modifier = Modifier
						.align(Alignment.BottomCenter)
						.padding(
							start = dimensionResource(id = R.dimen.s),
							end = dimensionResource(id = R.dimen.s),
							bottom = dimensionResource(id = R.dimen.s)
						)
						.fillMaxWidth()
						.height(dimensionResource(id = R.dimen.xl))
				) {
					Text(stringResource(id = R.string.done), color = textOnSubletrPink)
				}
			}
		},
		dragHandle = {}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaseDatePicker(state: DateRangePickerState, onClick: () -> Unit) {
	Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					start = dimensionResource(id = R.dimen.xs),
					end = dimensionResource(id = R.dimen.xs)
				),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			IconButton(onClick = onClick) {
				Icon(Icons.Filled.Close, contentDescription = stringResource(id = R.string.close))
			}
		}

		DateRangePicker(state = state,
			modifier = Modifier.weight(1f),
			title = {
				Text(
					text = stringResource(id = R.string.lease_start_end_dates),
					modifier = Modifier
						.padding(start = dimensionResource(id = R.dimen.l), end = dimensionResource(id = R.dimen.xs)),
					color = secondaryTextColor,
				)
			},
			headline = {
				Row(
					modifier = Modifier
						.clearAndSetSemantics {
							liveRegion = LiveRegionMode.Polite
						}
						.padding(start = dimensionResource(id = R.dimen.l)),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxs)),
				) {
					val dateFormatter = DatePickerDefaults.dateFormatter()
					Text(
						text =
						if (state.selectedStartDateMillis != null )
							dateFormatter.formatDate(state.selectedStartDateMillis, locale = Locale.getDefault())!!
						else stringResource(id = R.string.start_date),
						style = MaterialTheme.typography.displayLarge
					)
					Text(
						text = stringResource(id = R.string.dash),
						style = MaterialTheme.typography.displayLarge
					)
					Text(
						text =
						if (state.selectedEndDateMillis != null )
							dateFormatter.formatDate(state.selectedEndDateMillis, locale = Locale.getDefault())!!
						else stringResource(id = R.string.end_date),
						style = MaterialTheme.typography.displayLarge
					)
				}
			},
			colors = DatePickerDefaults.colors(
				containerColor = primaryBackgroundColor,
				dayInSelectionRangeContainerColor = textFieldBackgroundColor,
				selectedDayContainerColor = subletrPink,
			)
		)
	}
}

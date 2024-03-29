package org.uwaterloo.subletr.pages.home.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.switch.PrimarySwitch
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.theme.filterRegularFont
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun RoomFilter(
	currentRoomRange: HomePageUiState.RoomRange,
	updateRoomFilter: (HomePageUiState.RoomRange) -> Unit,
	closeAction: () -> Unit,
) {
	var bedroomForSublet by remember {
		mutableStateOf(
			currentRoomRange.bedroomForSublet
		)
	}
	var bedroomInProperty by remember {
		mutableStateOf(
			currentRoomRange.bedroomInProperty
		)
	}
	var bathroom by remember {
		mutableStateOf(
			currentRoomRange.bathroom
		)
	}
	var ensuitebathroom by remember {
		mutableStateOf(
			currentRoomRange.ensuiteBathroom
		)
	}
	BasicFilterLayout(
		modifier = Modifier.height(500.dp),
		titleId = R.string.rooms,
		content = {
			Column(
				modifier = Modifier
					.fillMaxWidth(1.0f)
					.wrapContentHeight(),
				horizontalAlignment = Alignment.Start,
			) {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
				Text(
					text = stringResource(id = R.string.bedrooms_for_sublet),
					style = filterRegularFont,
					color = MaterialTheme.subletrPalette.secondaryTextColor,
				)
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))

				Row(
					modifier = Modifier.fillMaxWidth(1.0f),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {

					DefaultFilterButton(
						isSelected = (bedroomForSublet == null),
						onClick = { bedroomForSublet = null },
						text = stringResource(
							id = R.string.any,
						),
					)
					for (i in 1..4) {
						DefaultFilterButton(
							isSelected = (bedroomForSublet == i),
							onClick = { bedroomForSublet = i },
							text = i.toString(),
						)
					}
					DefaultFilterButton(
						isSelected = (bedroomForSublet == 5),
						onClick = { bedroomForSublet = 5 },
						text = stringResource(
							id = R.string.five_plus,
						),
					)
				}
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))
				Text(
					text = stringResource(id = R.string.bedrooms_in_property),
					style = filterRegularFont,
					color = MaterialTheme.subletrPalette.secondaryTextColor,
				)
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
				Row(
					modifier = Modifier.fillMaxWidth(1.0f),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {

					DefaultFilterButton(
						isSelected = (bedroomInProperty == null),
						onClick = { bedroomInProperty = null },
						text = stringResource(
							id = R.string.any,
						),
					)
					for (i in 1..4) {
						DefaultFilterButton(
							isSelected = (bedroomInProperty == i),
							onClick = { bedroomInProperty = i },
							text = i.toString(),
						)
					}
					DefaultFilterButton(
						isSelected = (bedroomInProperty == 5),
						onClick = { bedroomInProperty = 5 },
						text = stringResource(
							id = R.string.five_plus,
						),
					)
				}
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))
				Text(
					text = stringResource(id = R.string.bathrooms),
					style = filterRegularFont,
					color = MaterialTheme.subletrPalette.secondaryTextColor,
				)
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
				Row(
					modifier = Modifier.fillMaxWidth(1.0f),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {

					DefaultFilterButton(
						isSelected = (bathroom == null),
						onClick = { bathroom = null },
						text = stringResource(
							id = R.string.any,
						),
					)
					for (i in 1..4) {
						DefaultFilterButton(
							isSelected = (bathroom == i),
							onClick = { bathroom = i },
							text = i.toString(),
						)
					}
					DefaultFilterButton(
						isSelected = (bathroom == 5),
						onClick = { bathroom = 5 },
						text = stringResource(
							id = R.string.five_plus,
						),
					)
				}
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))
				Row(
					modifier = Modifier.fillMaxWidth(1.0f),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					Text(
						text = stringResource(id = R.string.ensuite_bathroom),
						style = filterRegularFont,
						color = MaterialTheme.subletrPalette.secondaryTextColor,
					)
					PrimarySwitch(
						modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
						checked = ensuitebathroom,
						onCheckedChange = { ensuitebathroom = it },
						colors = SwitchDefaults.colors(
							checkedTrackColor = MaterialTheme.subletrPalette.subletrPink,
							checkedBorderColor = MaterialTheme.subletrPalette.subletrPink,
							uncheckedTrackColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
							uncheckedBorderColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
							checkedThumbColor = MaterialTheme.subletrPalette.textOnSubletrPink,
							uncheckedThumbColor = MaterialTheme.subletrPalette.textOnSubletrPink,
						)
					)
				}


			}

		},
		closeAction = closeAction,
		clearAction = {
			bedroomForSublet = null
			bedroomInProperty = null
			bathroom = null
			ensuitebathroom = false
		},
		revertInput = {
			bedroomForSublet = currentRoomRange.bedroomForSublet
			bedroomInProperty = currentRoomRange.bedroomInProperty
			bathroom = currentRoomRange.bathroom
			ensuitebathroom = currentRoomRange.ensuiteBathroom
		},
		updateFilterAndClose = {
			updateRoomFilter(
				HomePageUiState.RoomRange(
					bedroomForSublet = bedroomForSublet,
					bedroomInProperty = bedroomInProperty,
					bathroom = bathroom,
					ensuiteBathroom = ensuitebathroom,
				)
			)
			closeAction()
		},


		)
}



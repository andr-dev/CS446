package org.uwaterloo.subletr.pages.home.list.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.theme.SubletrLightColorScheme
import org.uwaterloo.subletr.theme.darkerGrayButtonColor
import org.uwaterloo.subletr.theme.filterBoldFont
import org.uwaterloo.subletr.theme.filterRegularFont
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink

const val MAX_LOCATION_RANGE = 50

@SuppressWarnings("CyclomaticComplexMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationFilter(
	currentLocationRange: HomePageUiState.LocationRange,
	updateLocationFilter: (HomePageUiState.LocationRange) -> Unit,
	closeAction: () -> Unit,
) {
	val lowerBound =
		if (currentLocationRange.lowerBound == null) 0 else currentLocationRange.lowerBound
	val upperBound =
		if (currentLocationRange.upperBound == null) MAX_LOCATION_RANGE else currentLocationRange.upperBound
	var sliderPosition by remember { mutableStateOf(lowerBound!!.toFloat()..upperBound!!.toFloat()) }
	var lowerBoundText by remember {
		mutableStateOf(
			if (currentLocationRange.lowerBound == null) ""
			else currentLocationRange.lowerBound.toString()
		)
	}
	var upperboundText by remember {
		mutableStateOf(
			if (currentLocationRange.upperBound == null) ""
			else currentLocationRange.upperBound.toString()
		)
	}
	var lowerTextFieldError by remember { mutableStateOf(false) }
	var upperTextFieldError by remember { mutableStateOf(false) }
	BasicFilterLayout(
		modifier = Modifier.height(360.dp),
		titleId = R.string.location,
		closeAction = closeAction,
		content = {
			Column(
				modifier = Modifier
					.fillMaxWidth(1.0f)
					.wrapContentHeight(),
			) {
				RangeSlider(
					modifier = Modifier
						.fillMaxWidth(1.0f),
					value = sliderPosition,
					onValueChange = {
						sliderPosition = it
						lowerBoundText = it.start.toInt().toString()
						upperboundText = it.endInclusive.toInt().toString()
					},
					valueRange = 0f..MAX_LOCATION_RANGE.toFloat(),
					colors = SliderDefaults.colors(
						thumbColor = SubletrLightColorScheme.onPrimary,
						activeTrackColor = subletrPink,
						inactiveTrackColor = darkerGrayButtonColor,
					),
					startThumb = {
						SliderDefaults.Thumb(
							modifier = Modifier
								.shadow(dimensionResource(id = R.dimen.xxs), CircleShape)
								.size(dimensionResource(id = R.dimen.m)),
							interactionSource = remember { MutableInteractionSource() },
							colors = SliderDefaults.colors(
								thumbColor = SubletrLightColorScheme.onPrimary,
							),
							enabled = true
						)
					},
					endThumb = {
						SliderDefaults.Thumb(
							modifier = Modifier
								.shadow(dimensionResource(id = R.dimen.xxs), CircleShape)
								.size(dimensionResource(id = R.dimen.m)),
							interactionSource = remember { MutableInteractionSource() },
							colors = SliderDefaults.colors(
								thumbColor = SubletrLightColorScheme.onPrimary,
							),
							enabled = true
						)
					},
				)
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
				Text(
					text = stringResource(id = R.string.Distance_instruction),
					style = filterRegularFont,
					color = secondaryTextColor,
				)
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
				Row(
					modifier = Modifier.fillMaxWidth(1.0f),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {

					TextFieldWithErrorIndication(
						value = lowerBoundText,
						onValueChange = {
							lowerBoundText = it
							lowerTextFieldError =
								!verifyNewBoundVal(
									newVal = lowerBoundText,
									upperBound = upperboundText
								)
							val newNum = it.trim().toIntOrNull()
							if (newNum == null || lowerTextFieldError || newNum > MAX_LOCATION_RANGE) {
								sliderPosition = (0f..sliderPosition.endInclusive)
							} else {
								lowerTextFieldError = false
								sliderPosition = (newNum.toFloat()..sliderPosition.endInclusive)
							}
						},
						isError = lowerTextFieldError,
						placeholderString = stringResource(id = R.string.minimum),
						suffix = {
							Text(
								text = stringResource(id = R.string.km),
								style = filterBoldFont,
							)
						},

						)
					Divider(
						modifier = Modifier.width(width = dimensionResource(id = R.dimen.m)),
						color = secondaryTextColor,
						thickness = dimensionResource(id = R.dimen.xxxxs),
					)
					TextFieldWithErrorIndication(
						value = upperboundText,
						onValueChange = {
							upperboundText = it
							upperTextFieldError =
								!verifyNewBoundVal(
									newVal = upperboundText,
									lowerBound = lowerBoundText
								)
							val newNum = it.trim().toIntOrNull()
							if (newNum == null || upperTextFieldError || newNum > MAX_LOCATION_RANGE) {
								sliderPosition =
									(sliderPosition.start..MAX_LOCATION_RANGE.toFloat())
							} else {
								upperTextFieldError = false
								sliderPosition = (sliderPosition.start..newNum.toFloat())
							}
						},
						isError = upperTextFieldError,
						placeholderString = stringResource(id = R.string.maximum),
						suffix = {
							Text(
								text = stringResource(id = R.string.km),
								style = filterBoldFont,
							)
						},
					)
				}
			}
		},
		clearAction = {
			sliderPosition = 0f..MAX_LOCATION_RANGE.toFloat()
			lowerBoundText = ""
			upperboundText = ""
			lowerTextFieldError = false
			upperTextFieldError = false

		},
		updateFilterAndClose = {
			if (!lowerTextFieldError && !upperTextFieldError) {
				updateLocationFilter(
					HomePageUiState.LocationRange(
						lowerBoundText.toIntOrNull(),
						upperboundText.toIntOrNull(),
					)
				)
				closeAction()

			}
		},
		revertInput = {
			lowerBoundText =
				if (currentLocationRange.lowerBound == null) "" else currentLocationRange.lowerBound.toString()
			upperboundText =
				if (currentLocationRange.upperBound == null) "" else currentLocationRange.upperBound.toString()
			sliderPosition = lowerBound!!.toFloat()..upperBound!!.toFloat()
			lowerTextFieldError = false
			upperTextFieldError = false

		}
	)

}

package org.uwaterloo.subletr.pages.home.list.components

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.pages.home.list.HomeListUiState
import org.uwaterloo.subletr.theme.darkerGrayButtonColor
import org.uwaterloo.subletr.theme.filterBoldFont
import org.uwaterloo.subletr.theme.filterRegularFont
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.textFieldBackgroundColor

const val MAX_LOCATION_RANGE = 50

@SuppressWarnings("CyclomaticComplexMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationFilterForm(
	currentLocationRange: HomeListUiState.LocationRange,
	updateLocationFilter: (HomeListUiState.LocationRange) -> Unit,
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
		modifier = Modifier.height(280.dp),
		titleId = R.string.location,
		closeAction = closeAction,
		content = {
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
					thumbColor = Color.White,
					activeTrackColor = subletrPink,
					inactiveTrackColor = darkerGrayButtonColor
				),
				startThumb = {
					SliderDefaults.Thumb(
						modifier = Modifier
							.shadow(dimensionResource(id = R.dimen.xxs), CircleShape)
							.size(dimensionResource(id = R.dimen.m)),
						interactionSource = remember { MutableInteractionSource() },
						colors = SliderDefaults.colors(
							thumbColor = Color.White,
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
							thumbColor = Color.White,
						),
						enabled = true
					)
				},
			)
			Text(
				text = stringResource(id = R.string.Distance_instruction),
				style = filterRegularFont,
				color = secondaryTextColor
			)
			Row(
				modifier = Modifier.fillMaxWidth(1.0f),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {

				TextFieldWithErrorIndication(
					value = lowerBoundText,
					onValueChange = {
						lowerBoundText = it
						lowerTextFieldError =
							!verifyNewBoundVal(newVal = lowerBoundText, upperBound = upperboundText)
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
							style = filterBoldFont
						)
					}

				)
				Divider(
					modifier = Modifier.width(width = dimensionResource(id = R.dimen.m)),
					color = secondaryTextColor,
					thickness = dimensionResource(id = R.dimen.xxxxs)
				)
				TextFieldWithErrorIndication(
					value = upperboundText,
					onValueChange = {
						upperboundText = it
						upperTextFieldError =
							!verifyNewBoundVal(newVal = upperboundText, lowerBound = lowerBoundText)
						val newNum = it.trim().toIntOrNull()
						if (newNum == null || upperTextFieldError || newNum > MAX_LOCATION_RANGE) {
							sliderPosition = (sliderPosition.start..MAX_LOCATION_RANGE.toFloat())
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
							style = filterBoldFont
						)
					}
				)
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
					HomeListUiState.LocationRange(
						lowerBoundText.toIntOrNull(),
						upperboundText.toIntOrNull()
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

@Composable
fun PriceFilterForm(
	currentPriceRange: HomeListUiState.PriceRange,
	updatePriceFilter: (HomeListUiState.PriceRange) -> Unit,
	closeAction: () -> Unit,
) {
	var lowerBoundText by remember {
		mutableStateOf(
			if (currentPriceRange.lowerBound == null) ""
			else currentPriceRange.lowerBound.toString()
		)
	}
	var upperboundText by remember {
		mutableStateOf(
			if (currentPriceRange.upperBound == null) ""
			else currentPriceRange.upperBound.toString()
		)
	}
	var lowerTextFieldError by remember { mutableStateOf(false) }
	var upperTextFieldError by remember { mutableStateOf(false) }
	BasicFilterLayout(
		modifier = Modifier.height(180.dp),
		titleId = R.string.price_range,
		closeAction = closeAction,
		content = {
			Row(
				modifier = Modifier.fillMaxWidth(1.0f),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				TextFieldWithErrorIndication(
					value = lowerBoundText,
					onValueChange = {
						lowerBoundText = it
						lowerTextFieldError =
							!verifyNewBoundVal(newVal = lowerBoundText, upperBound = upperboundText)
					},
					isError = lowerTextFieldError,
					placeholderString = stringResource(id = R.string.minimum),
				)
				Divider(
					modifier = Modifier.width(width = dimensionResource(id = R.dimen.m)),
					color = secondaryTextColor,
					thickness = dimensionResource(id = R.dimen.xxxxs)
				)
				TextFieldWithErrorIndication(
					value = upperboundText,
					onValueChange = {
						upperboundText = it
						upperTextFieldError =
							!verifyNewBoundVal(newVal = upperboundText, lowerBound = lowerBoundText)
					},
					isError = upperTextFieldError,
					placeholderString = stringResource(id = R.string.maximum),

					)
			}
		},
		clearAction = {
			lowerBoundText = ""
			upperboundText = ""
			lowerTextFieldError = false
			upperTextFieldError = false
		},
		updateFilterAndClose = {
			if (!lowerTextFieldError && !upperTextFieldError) {
				updatePriceFilter(
					HomeListUiState.PriceRange(
						lowerBoundText.toIntOrNull(),
						upperboundText.toIntOrNull()
					)
				)
				closeAction()

			}
		},
		revertInput = {
			lowerBoundText =
				if (currentPriceRange.lowerBound == null) "" else currentPriceRange.lowerBound.toString()
			upperboundText =
				if (currentPriceRange.upperBound == null) "" else currentPriceRange.upperBound.toString()
			lowerTextFieldError = false
			upperTextFieldError = false

		}
	)

}

@Composable
fun RoomFilterForm() {
	//	TODO

}

@Composable
fun GenderFilterForm() {
	//	TODO

}


@Composable
fun PropertyTypeFilterForm() {
	//	TODO

}

@Composable
fun AllFilterForm() {
	//	TODO

}

@Composable
fun BasicFilterLayout(
	modifier: Modifier = Modifier,
	@StringRes titleId: Int,
	closeAction: () -> Unit,
	content: @Composable (() -> Unit)? = null,
	updateFilterAndClose: () -> Unit,
	clearAction: () -> Unit,
	revertInput: () -> Unit,
) {
	Scaffold(
		modifier = modifier
			.fillMaxWidth(1.0f),
		containerColor = Color.White,
		topBar = {
			Row(
				modifier = Modifier
					.fillMaxWidth(1.0f)
					.height(dimensionResource(id = R.dimen.xl))
					.padding(
						dimensionResource(id = R.dimen.m),
						dimensionResource(id = R.dimen.xxs)
					),
				horizontalArrangement = Arrangement.SpaceBetween
			)
			{
				Icon(
					modifier = Modifier/**/.clickable {
						closeAction()
						revertInput()
					},
					painter = painterResource(
						id = R.drawable.close_filled_black_24
					),
					contentDescription = stringResource(id = R.string.close),
				)
				Text(
					stringResource(id = titleId),
					style = MaterialTheme.typography.displayMedium
				)
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.m)))

			}
		},
		bottomBar = {
			Column {
				Divider(
					color = textFieldBackgroundColor,
					modifier = Modifier
						.height(dimensionResource(id = R.dimen.xxxxs))
						.fillMaxWidth()
				)
				Row(
					modifier = Modifier
						.fillMaxWidth(1.0f)
						.padding(
							dimensionResource(id = R.dimen.m),
							dimensionResource(id = R.dimen.xxs)
						),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						stringResource(id = R.string.clear),
						style = filterBoldFont,
						modifier = Modifier.clickable { clearAction() })
					PrimaryButton(onClick = {
						updateFilterAndClose()

					}, content = {
						Text(
							stringResource(
								id = R.string.show_sublets,
							),
							color = Color.White,
							style = filterBoldFont

						)
					})
				}

			}

		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues = paddingValues)
				.padding(dimensionResource(id = R.dimen.m), dimensionResource(id = R.dimen.zero))
				.fillMaxWidth(1.0f),
			verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s))
		) {
			if (content != null) {
				content()
			}
		}
	}
}

@Composable
fun TextFieldWithErrorIndication(
	modifier: Modifier = Modifier,
	value: String,
	onValueChange: (String) -> Unit,
	isError: Boolean,
	placeholderString: String,
	suffix: @Composable (() -> Unit)? = null,
) {
	TextField(
		modifier = modifier
			.width(dimensionResource(id = R.dimen.xxxxxxl))
			.height(dimensionResource(id = R.dimen.xl))
			.border(
				dimensionResource(id = R.dimen.xxxxs),
				if (isError) Color.Red else Color.Transparent,
				RoundedCornerShape(dimensionResource(id = R.dimen.xs))
			),
		value = value,
		colors = TextFieldDefaults.colors(
			unfocusedContainerColor = textFieldBackgroundColor,
			focusedContainerColor = textFieldBackgroundColor,
			unfocusedIndicatorColor = Color.Transparent,
			focusedIndicatorColor = Color.Transparent,
			errorIndicatorColor = Color.Transparent,
			errorContainerColor = textFieldBackgroundColor
		),
		textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
		onValueChange = onValueChange,
		isError = isError,
		singleLine = true,
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
		placeholder = {
			Text(
				text = placeholderString,
				color = secondaryTextColor,
				style = TextStyle(fontSize = 14.sp)
			)
		},
		suffix = suffix

	)
}

fun verifyNewBoundVal(
	newVal: String,
	lowerBound: String? = null,
	upperBound: String? = null,
): Boolean {
	if (newVal.isEmpty()) {
		return true
	}
	val newInt = newVal.toIntOrNull()
	var result = false
	if (newInt != null && newInt >= 0) {
		if (lowerBound == null) {
			if (upperBound!!.isEmpty()) {
				result = true
			}
			val ub = upperBound!!.toIntOrNull()
			if (ub == null || newInt <= ub) {
				result = true
			}
		} else {
			if (lowerBound.isEmpty()) {
				result = true
			}
			val lb = lowerBound.toIntOrNull()
			if (lb == null || newInt >= lb) {
				result = true
			}
		}
	}
	return result

}

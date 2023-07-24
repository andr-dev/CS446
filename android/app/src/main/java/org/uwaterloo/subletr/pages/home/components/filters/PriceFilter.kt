package org.uwaterloo.subletr.pages.home.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun PriceFilter(
	currentPriceRange: HomePageUiState.PriceRange,
	updatePriceFilter: (HomePageUiState.PriceRange) -> Unit,
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
		modifier = Modifier.height(220.dp),
		titleId = R.string.price_range,
		closeAction = closeAction,
		content = {
				Row(
					modifier = Modifier
						.fillMaxWidth(1.0f)
						.padding(top = dimensionResource(id = R.dimen.l)),
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
						},
						isError = lowerTextFieldError,
						placeholderString = stringResource(id = R.string.minimum),
					)
					Divider(
						modifier = Modifier.width(width = dimensionResource(id = R.dimen.m)),
						color = MaterialTheme.subletrPalette.secondaryTextColor,
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
					HomePageUiState.PriceRange(
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

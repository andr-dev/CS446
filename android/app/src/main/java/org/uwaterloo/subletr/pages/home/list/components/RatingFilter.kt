package org.uwaterloo.subletr.pages.home.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.components.rating.StaticRatingsBar
import org.uwaterloo.subletr.theme.subletrPalette

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RatingFilter(
	currentRatingPref: Int,
	updateRatingFilter: (Int) -> Unit,
	closeAction: () -> Unit,
) {
	var ratingPref by remember {
		mutableIntStateOf(
			currentRatingPref
		)
	}

	BasicFilterLayout(
		modifier = Modifier.height(240.dp),
		titleId = R.string.rating,
		content = {
			Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
				content = {
					DefaultFilterButton(
						isSelected = (ratingPref == 0),
						onClick = { ratingPref = 0 },
						text = stringResource(
							id = R.string.any,
						),
					)
					for (i in 5 downTo 1) {
						SecondaryButton(
							modifier = Modifier
								.defaultMinSize(
									minWidth = dimensionResource(id = R.dimen.xxxxs),
									minHeight = dimensionResource(id = R.dimen.xxxxs),
								),
							onClick = { ratingPref = i },
							contentPadding = PaddingValues(
								horizontal = dimensionResource(id = R.dimen.s),
								vertical = dimensionResource(id = R.dimen.xs),
							),
							colors = ButtonDefaults.buttonColors(
								containerColor =
								if (ratingPref == i) MaterialTheme.subletrPalette.subletrPink
								else MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
							)
						) {
							Row(
								modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxs)),

								) {
								StaticRatingsBar(
									modifier = Modifier
										.height(dimensionResource(id = R.dimen.m))
										.padding(dimensionResource(id = R.dimen.xxs)),
									rating = i.toFloat(),
									ratingColor = if (ratingPref == i) MaterialTheme.subletrPalette.textOnSubletrPink
									else MaterialTheme.subletrPalette.primaryTextColor,
									onlyShowFilled = true,
								)
								if (i < 5) {
									Text(
										text = "& up",
										color = if (ratingPref == i) MaterialTheme.subletrPalette.textOnSubletrPink
										else MaterialTheme.subletrPalette.primaryTextColor,
									)

								}
							}
						}
					}
				},
			)
		},
		closeAction = closeAction,
		clearAction = {
			ratingPref = 0
		},
		revertInput = {
			ratingPref = currentRatingPref
		},
		updateFilterAndClose = {
			updateRatingFilter(ratingPref)
			closeAction()
		},
	)
}

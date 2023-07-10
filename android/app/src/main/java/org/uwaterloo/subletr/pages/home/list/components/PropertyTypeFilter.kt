package org.uwaterloo.subletr.pages.home.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.enums.HousingType

@Composable
fun PropertyTypeFilter(
	currentHousingPref: HousingType,
	updateHousingFilter: (HousingType) -> Unit,
	closeAction: () -> Unit,
) {
	var housingPref by remember {
		mutableStateOf(
			currentHousingPref
		)
	}

	BasicFilterLayout(
		modifier = Modifier.height(280.dp),
		titleId = R.string.property_type,
		content = {
			Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
			LazyVerticalGrid(
				columns = GridCells.Fixed(3),
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
				content = {
					item {
						defaultFilterButton(
							isSelected = (housingPref == HousingType.OTHER),
							onClick = { housingPref = HousingType.OTHER },
							text = stringResource(
								id = R.string.any,
							),
						)
					}
					HousingType.values().map {
						if (it != HousingType.OTHER) {
							item {
								defaultFilterButton(
									isSelected = (housingPref == it),
									onClick = { housingPref = it },
									text = stringResource(
										id = it.stringId,
									),
								)
							}
						}
					}
				},
			)
		},
		closeAction = closeAction,
		clearAction = {
			housingPref = HousingType.OTHER
		},
		revertInput = {
			housingPref = currentHousingPref
		},
		updateFilterAndClose = {
			updateHousingFilter(housingPref)
			closeAction()
		},
		)

}

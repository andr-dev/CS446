package org.uwaterloo.subletr.pages.home.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import org.uwaterloo.subletr.theme.filterRegularFont
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun FavouriteFilter(
	currentFavourite: Boolean,
	updateFavouriteFilter: (Boolean) -> Unit,
	closeAction: () -> Unit,
) {
	var isFavourite by remember {
		mutableStateOf(
			currentFavourite
		)
	}
	BasicFilterLayout(
		modifier = Modifier.height(160.dp),
		titleId = R.string.favourite,

		content = {
			Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))
			Row(
				modifier = Modifier.fillMaxWidth(1.0f),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text = stringResource(id = R.string.only_show_favourites),
					style = filterRegularFont,
					color = MaterialTheme.subletrPalette.secondaryTextColor,
				)
				PrimarySwitch(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
					checked = isFavourite,
					onCheckedChange = { isFavourite = it },
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

		},
		closeAction = closeAction,
		clearAction = {
			isFavourite = false
		},
		revertInput = {
			isFavourite = currentFavourite

		},
		updateFilterAndClose = {
			updateFavouriteFilter(isFavourite)
			closeAction()
		}


	)

}

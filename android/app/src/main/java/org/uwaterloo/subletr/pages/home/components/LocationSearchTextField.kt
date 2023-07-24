package org.uwaterloo.subletr.pages.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.pages.home.HomePageViewModel.Companion.CURRENT_LOCATION_STRING_VAL
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun LocationSearchTextField(
	modifier: Modifier = Modifier,
	addressSearch: String,
	onValueChange: (String) -> Unit,
	onLocationIconClick: () -> Unit,
) {
	RoundedTextField(
		modifier = modifier
			.fillMaxWidth(fraction = 1.0f)
			.padding(horizontal = dimensionResource(id = R.dimen.xs)),
		value = if (addressSearch != CURRENT_LOCATION_STRING_VAL) {
			addressSearch
		} else {
			stringResource(id = R.string.current_location)
		},
		onValueChange = onValueChange,
		placeholder = {
			Text(
				text = stringResource(id = R.string.address_city_postal_code),
				color = MaterialTheme.subletrPalette.secondaryTextColor,
			)
		},
		leadingIcon = {
			Icon(
				painter = painterResource(id = R.drawable.search_solid_gray_24),
				contentDescription = stringResource(id = R.string.search),
				tint = MaterialTheme.subletrPalette.unselectedGray,
			)
		},
		trailingIcon = {
			IconButton(onClick = onLocationIconClick) {
				Icon(
					painter = painterResource(id = R.drawable.my_location_solid_pink_24),
					contentDescription = stringResource(id = R.string.my_location),
					tint = MaterialTheme.subletrPalette.subletrPink,
				)
			}
		},
	)
}

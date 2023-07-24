package org.uwaterloo.subletr.pages.home.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.SegmentedIconButton
import org.uwaterloo.subletr.components.button.SegmentedIconButtonPosition
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.pages.home.map.HomeMapUiState
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun HomeMapFiltersRowView(
	modifier: Modifier,
	uiState: HomeMapUiState.Loaded,
	updateTransportationMethod: (HomePageUiState.TransportationMethod) -> Unit,
	showAllFilters: () -> Unit,
) {
	Row(
		modifier = modifier
			.fillMaxWidth(fraction = 1.0f)
			.padding(horizontal = dimensionResource(id = R.dimen.xs))
			.height(dimensionResource(id = R.dimen.l)),
	) {
		Box(
			modifier = Modifier
				.clip(CircleShape),
		) {
			IconButton(
				modifier = Modifier.background(color = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor),
				onClick = showAllFilters,
			) {
				Icon(
					painter = painterResource(id = R.drawable.tune_round_black_24),
					contentDescription = stringResource(id = R.string.filter_menu),
				)
			}
		}

		Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)))

		Row(
			modifier = Modifier
				.height(dimensionResource(id = R.dimen.l)),
		) {
			SegmentedIconButton(
				onClick = {
					updateTransportationMethod(HomePageUiState.TransportationMethod.WALK)
				},
				iconPainter = painterResource(id = R.drawable.directions_walk_solid_gray_24),
				iconContentDescription = stringResource(id = R.string.walk),
				selectedTint = MaterialTheme.subletrPalette.subletrPink,
				unselectedTint = MaterialTheme.subletrPalette.unselectedGray,
				selected = uiState.transportationMethod == HomePageUiState.TransportationMethod.WALK,
				position = SegmentedIconButtonPosition.START,
			)
			SegmentedIconButton(
				onClick = {
					updateTransportationMethod(HomePageUiState.TransportationMethod.BIKE)
				},
				iconPainter = painterResource(id = R.drawable.directions_bike_solid_gray_24),
				iconContentDescription = stringResource(id = R.string.bike),
				selectedTint = MaterialTheme.subletrPalette.subletrPink,
				unselectedTint = MaterialTheme.subletrPalette.unselectedGray,
				selected = uiState.transportationMethod == HomePageUiState.TransportationMethod.BIKE,
				position = SegmentedIconButtonPosition.MIDDLE,
			)
			SegmentedIconButton(
				onClick = {
					updateTransportationMethod(HomePageUiState.TransportationMethod.BUS)
				},
				iconPainter = painterResource(id = R.drawable.directions_bus_solid_gray_24),
				iconContentDescription = stringResource(id = R.string.bus),
				selectedTint = MaterialTheme.subletrPalette.subletrPink,
				unselectedTint = MaterialTheme.subletrPalette.unselectedGray,
				selected = uiState.transportationMethod == HomePageUiState.TransportationMethod.BUS,
				position = SegmentedIconButtonPosition.MIDDLE,
			)
			SegmentedIconButton(
				onClick = {
					updateTransportationMethod(HomePageUiState.TransportationMethod.CAR)
				},
				iconPainter = painterResource(id = R.drawable.directions_car_solid_gray_24),
				iconContentDescription = stringResource(id = R.string.car),
				selectedTint = MaterialTheme.subletrPalette.subletrPink,
				unselectedTint = MaterialTheme.subletrPalette.unselectedGray,
				selected = uiState.transportationMethod == HomePageUiState.TransportationMethod.CAR,
				position = SegmentedIconButtonPosition.END,
			)
		}
	}
}

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.SegmentedIconButton
import org.uwaterloo.subletr.components.button.SegmentedIconButtonPosition
import org.uwaterloo.subletr.pages.home.map.HomeMapChildViewModel
import org.uwaterloo.subletr.pages.home.map.HomeMapUiState
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.unselectedGray

@Composable
fun HomeMapFiltersRowView(
	modifier: Modifier,
	uiState: HomeMapUiState.Loaded,
	viewModel: HomeMapChildViewModel,
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
				modifier = Modifier.background(color = secondaryButtonBackgroundColor),
				onClick = { /*TODO*/ },
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
					viewModel.transportationMethodStream.onNext(HomeMapUiState.TransportationMethod.WALK)
				},
				iconPainter = painterResource(id = R.drawable.directions_walk_solid_gray_24),
				iconContentDescription = stringResource(id = R.string.walk),
				selectedTint = subletrPink,
				unselectedTint = unselectedGray,
				selected = uiState.transportationMethod == HomeMapUiState.TransportationMethod.WALK,
				position = SegmentedIconButtonPosition.START,
			)
			SegmentedIconButton(
				onClick = {
					viewModel.transportationMethodStream.onNext(HomeMapUiState.TransportationMethod.BIKE)
				},
				iconPainter = painterResource(id = R.drawable.directions_bike_solid_gray_24),
				iconContentDescription = stringResource(id = R.string.bike),
				selectedTint = subletrPink,
				unselectedTint = unselectedGray,
				selected = uiState.transportationMethod == HomeMapUiState.TransportationMethod.BIKE,
				position = SegmentedIconButtonPosition.MIDDLE,
			)
			SegmentedIconButton(
				onClick = {
					viewModel.transportationMethodStream.onNext(HomeMapUiState.TransportationMethod.BUS)
				},
				iconPainter = painterResource(id = R.drawable.directions_bus_solid_gray_24),
				iconContentDescription = stringResource(id = R.string.bus),
				selectedTint = subletrPink,
				unselectedTint = unselectedGray,
				selected = uiState.transportationMethod == HomeMapUiState.TransportationMethod.BUS,
				position = SegmentedIconButtonPosition.MIDDLE,
			)
			SegmentedIconButton(
				onClick = {
					viewModel.transportationMethodStream.onNext(HomeMapUiState.TransportationMethod.ALL)
				},
				iconPainter = painterResource(id = R.drawable.map_outline_black_24),
				iconContentDescription = stringResource(id = R.string.all_transport),
				selectedTint = subletrPink,
				unselectedTint = unselectedGray,
				selected = uiState.transportationMethod == HomeMapUiState.TransportationMethod.ALL,
				position = SegmentedIconButtonPosition.END,
			)
		}
	}
}

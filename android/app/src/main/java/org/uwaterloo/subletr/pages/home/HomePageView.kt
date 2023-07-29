package org.uwaterloo.subletr.pages.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.navOptions
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.SegmentedIconButton
import org.uwaterloo.subletr.components.button.SegmentedIconButtonPosition
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.home.list.HomeListChildView
import org.uwaterloo.subletr.pages.home.list.HomeListUiState
import org.uwaterloo.subletr.pages.home.map.HomeMapChildView
import org.uwaterloo.subletr.pages.home.map.HomeMapUiState
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun HomePageView(
	modifier: Modifier = Modifier,
	viewModel: HomePageViewModel = hiltViewModel(),
	uiState: HomePageUiState = viewModel.uiStateStream.subscribeAsState(
		HomeListUiState.Loading
	).value,
) {
	val isListView = uiState is HomeListUiState
	Scaffold(
		modifier = modifier,
		topBar = {
			Row(
				modifier = Modifier
					.fillMaxWidth(1.0f)
					.padding(
						start = dimensionResource(id = R.dimen.s),
						top = dimensionResource(id = R.dimen.s),
						end = dimensionResource(id = R.dimen.s),
						bottom = dimensionResource(id = R.dimen.m)
					),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
			) {
				Text(
					text = stringResource(id = R.string.view_sublets),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.subletrPalette.primaryTextColor,
				)
				ViewSwitch(
					isListView = isListView,
					uiState = uiState,
					viewModel = viewModel,
				)
			}
		},
		content = { padding: PaddingValues ->
			when (uiState) {
				is HomeListUiState.Failed -> {
					LaunchedEffect(key1 = true) {
						viewModel.navHostController.navigate(
							route = NavigationDestination.LOGIN.rootNavPath,
							navOptions = navOptions {
								popUpTo(viewModel.navHostController.graph.id) {
									saveState = true
								}
								launchSingleTop = true
							},
						)
					}
				}
				is HomeListUiState -> {
					HomeListChildView(
						modifier = Modifier.padding(padding),
						viewModel = viewModel.homeListChildViewModel,
						uiState = uiState,
					)
				}
				is HomeMapUiState -> {
					HomeMapChildView(
						modifier = Modifier.padding(padding),
						viewModel = viewModel.homeMapChildViewModel,
						uiState = uiState,
					)
				}
			}
		},
		bottomBar = {
			Box(modifier = Modifier)
		},
	)
}

@Composable
fun ViewSwitch(
	modifier: Modifier = Modifier,
	isListView: Boolean,
	uiState: HomePageUiState,
	viewModel: HomePageViewModel,
) {
	Row(
		modifier = modifier
			.wrapContentWidth(),
	) {
		SegmentedIconButton(
			modifier = Modifier.height(dimensionResource(id = R.dimen.l)),
			onClick = { viewModel.switchToListView(uiState = uiState) },
			iconPainter = painterResource(
				id = R.drawable.view_list_outline_pink_24,
			),
			iconContentDescription = stringResource(id = R.string.list_icon),
			selectedTint = MaterialTheme.subletrPalette.subletrPink,
			unselectedTint = MaterialTheme.subletrPalette.unselectedGray,
			selected = isListView,
			position = SegmentedIconButtonPosition.START,
		)
		SegmentedIconButton(
			modifier = Modifier.height(dimensionResource(id = R.dimen.l)),
			onClick = { viewModel.switchToMapView(uiState = uiState) },
			iconPainter = painterResource(
				id = R.drawable.map_solid_pink_24,
			),
			iconContentDescription = stringResource(id = R.string.map_icon),
			selectedTint = MaterialTheme.subletrPalette.subletrPink,
			unselectedTint = MaterialTheme.subletrPalette.unselectedGray,
			selected = !isListView,
			position = SegmentedIconButtonPosition.END,
		)
	}
}

@Preview(showBackground = true)
@Composable
fun HomePageViewLoadingPreview() {
	HomePageView()
}

@Preview(showBackground = true)
@Composable
fun LoginPageViewLoadedPreview() {
	SubletrTheme {
		HomePageView(
			uiState = HomeListUiState.Loaded(
				addressSearch = "",
				listingItems = HomePageUiState.ListingItemsModel(
					listings = emptyList(),
					likedListings = emptySet(),
					listingsImages = emptyList(),
					selectedListings = emptyList(),
					timeToDestination = emptyList(),
				),
				filters = HomePageUiState.FiltersModel(
					locationRange = HomePageUiState.LocationRange(),
					priceRange = HomePageUiState.PriceRange(),
					roomRange = HomePageUiState.RoomRange(),
					gender = Gender.OTHER,
					housingType = HousingType.OTHER,
					dateRange = HomePageUiState.DateRange(),
					favourite = false,
					timeToDestination = null,
					addressSearch = null,
					minRating = 0,
					showVerifiedOnly = false,
					computedLatLng = null,
				),
			),
		)
	}
}

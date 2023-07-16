package org.uwaterloo.subletr.pages.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.navOptions
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.home.list.HomeListChildView
import org.uwaterloo.subletr.pages.home.list.HomeListUiState
import org.uwaterloo.subletr.pages.home.map.HomeMapChildView
import org.uwaterloo.subletr.pages.home.map.HomeMapUiState
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink

@Composable
fun HomePageView(
	modifier: Modifier = Modifier,
	viewModel: HomePageViewModel = hiltViewModel(),
	uiState: HomePageUiState = viewModel.uiStateStream.subscribeAsState(
		HomeListUiState.Loading
	).value,
) {
	val isListView = remember { mutableStateOf(true) }
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
				)
				ViewSwitch(isListView = isListView)
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
	isListView: MutableState<Boolean>,
) {
	Row(
		modifier = modifier
			.wrapContentWidth(),
	) {
		Box(
			modifier = Modifier
				.width(dimensionResource(id = R.dimen.xl))
				.height(dimensionResource(id = R.dimen.l))
				.border(
					BorderStroke(
						dimensionResource(id = R.dimen.xxxxs),
						color = if (isListView.value) subletrPink else secondaryButtonBackgroundColor
					),
					shape = RoundedCornerShape(
						topStart = dimensionResource(id = R.dimen.s),
						topEnd = dimensionResource(id = R.dimen.zero),
						bottomStart = dimensionResource(id = R.dimen.s),
						bottomEnd = dimensionResource(id = R.dimen.zero),
					),
				)
				.clip(
					RoundedCornerShape(
						topStart = dimensionResource(id = R.dimen.s),
						topEnd = dimensionResource(id = R.dimen.zero),
						bottomStart = dimensionResource(id = R.dimen.s),
						bottomEnd = dimensionResource(id = R.dimen.zero),
					)
				)
				.background(if (isListView.value) Color.White else secondaryButtonBackgroundColor)
				.padding(
					dimensionResource(id = R.dimen.s),
					dimensionResource(id = R.dimen.xxs),
					dimensionResource(id = R.dimen.xs),
					dimensionResource(id = R.dimen.xxs)
				)
				.clickable {
					isListView.value = true
				},
			content = {
				Icon(
					painter = painterResource(
						id = R.drawable.view_list_outline_pink_24,
					),
					contentDescription = stringResource(id = R.string.list_icon),
					tint = if (isListView.value) subletrPink else secondaryTextColor
				)
			}
		)
		Box(
			modifier = Modifier
				.width(dimensionResource(id = R.dimen.xl))
				.height(dimensionResource(id = R.dimen.l))
				.border(
					BorderStroke(
						dimensionResource(id = R.dimen.xxxxs),
						color = if (!isListView.value) subletrPink else secondaryButtonBackgroundColor
					),
					shape = RoundedCornerShape(
						topStart = dimensionResource(id = R.dimen.zero),
						topEnd = dimensionResource(id = R.dimen.s),
						bottomStart = dimensionResource(id = R.dimen.zero),
						bottomEnd = dimensionResource(id = R.dimen.s)
					),
				)
				.clip(
					RoundedCornerShape(
						topStart = dimensionResource(id = R.dimen.zero),
						topEnd = dimensionResource(id = R.dimen.s),
						bottomStart = dimensionResource(id = R.dimen.zero),
						bottomEnd = dimensionResource(id = R.dimen.s)
					)
				)
				.background(if (!isListView.value) Color.White else secondaryButtonBackgroundColor)
				.padding(
					start = dimensionResource(id = R.dimen.xs),
					top = dimensionResource(id = R.dimen.xxs),
					end = dimensionResource(id = R.dimen.xs),
					bottom = dimensionResource(id = R.dimen.xxs)
				)
				.clickable {
					isListView.value = false
				},
			content = {
				Icon(
					painter = painterResource(
						id = R.drawable.map_solid_pink_24,
					),
					contentDescription = stringResource(id = R.string.map_icon),
					tint = if (!isListView.value) subletrPink else secondaryTextColor
				)
			}
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
				locationRange = HomeListUiState.LocationRange(),
				priceRange = HomeListUiState.PriceRange(),
				roomRange = HomeListUiState.RoomRange(),
				listingItems = HomeListUiState.ListingItemsModel(
					listings = emptyList(),
					likedListings = emptySet(),
					listingsImages = emptyList(),
				),
				genderPreference = Gender.OTHER,
				houseTypePreference = HousingType.OTHER,
				infoTextStringId = null,
			),
		)
	}
}

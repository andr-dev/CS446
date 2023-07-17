package org.uwaterloo.subletr.pages.home.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.enums.FilterType
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.pages.home.list.components.DateFilter
import org.uwaterloo.subletr.pages.home.list.components.FavouriteFilter
import org.uwaterloo.subletr.pages.home.list.components.ListingPost
import org.uwaterloo.subletr.pages.home.list.components.LocationFilter
import org.uwaterloo.subletr.pages.home.list.components.PriceFilter
import org.uwaterloo.subletr.pages.home.list.components.PropertyTypeFilter
import org.uwaterloo.subletr.pages.home.list.components.RoomFilter
import org.uwaterloo.subletr.pages.home.list.components.RoommateFilter
import org.uwaterloo.subletr.services.NavigationService
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.filterTextFont
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@SuppressWarnings("CyclomaticComplexMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeListChildView(
	modifier: Modifier,
	viewModel: HomeListChildViewModel,
	uiState: HomeListUiState,
) {
	if (uiState is HomeListUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f)
				.imePadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			CircularProgressIndicator()
		}
	} else if (uiState is HomeListUiState.Loaded) {
		val filterType = remember { mutableStateOf(FilterType.LOCATION) }
		val coroutineScope = rememberCoroutineScope()
		val modelSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
		val listState: LazyListState = rememberLazyListState()
		var isBottomSheetOpen by remember {
			mutableStateOf(false)
		}
		val lastItemIsShowing by remember {
			derivedStateOf {
				if (listState.layoutInfo.totalItemsCount == 0) {
					false
				} else {
					listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
				}
			}
		}

		LaunchedEffect(key1 = lastItemIsShowing) {
			if (lastItemIsShowing) {
				viewModel.listingPagingParamsStream.onNext(
					HomeListChildViewModel.ListingPagingParams(
						previousListingItemsModel = uiState.listingItems,
						pageNumber = (
							listState.layoutInfo.totalItemsCount
							).floorDiv(HomeListChildViewModel.LISTING_PAGE_SIZE)
					)
				)
			}
		}

		fun updateLocationFilter(newVal: HomePageUiState.LocationRange) {
			viewModel.locationRangeFilterStream.onNext(newVal)
		}

		fun updatePriceFilter(newVal: HomePageUiState.PriceRange) {
			viewModel.priceRangeFilterStream.onNext(newVal)
		}

		fun updateRoomFilter(newVal: HomePageUiState.RoomRange) {
			viewModel.roomRangeFilterStream.onNext(newVal)
		}

		fun updateGenderFilter(newVal: Gender) {
			viewModel.genderFilterStream.onNext(newVal)
		}

		fun updateHousingFilter(newVal: HousingType) {
			viewModel.houseTypeFilterStream.onNext(newVal)
		}

		fun updateDateFilter(newVal: HomeListUiState.DateRange) {
			viewModel.dateFilterStream.onNext(newVal)
		}

		fun updateFavouriteFilter(newVal: Boolean) {
			viewModel.favouriteFilterStream.onNext(newVal)
		}

		fun closeBottomSheet() {
			coroutineScope.launch {
				isBottomSheetOpen = false
			}
		}

		Scaffold(
			modifier = modifier
				.fillMaxSize(1.0f),
			floatingActionButtonPosition = FabPosition.End,
			floatingActionButton = {
				FloatingActionButton(
					modifier = Modifier.padding(
						all = dimensionResource(id = R.dimen.zero),
					),
					onClick = {
						viewModel.navHostController.navigate(NavigationDestination.CREATE_LISTING.fullNavPath)
					},
					shape = CircleShape,
					containerColor = subletrPink,
					contentColor = Color.White,
				) {
					Text(
						stringResource(id = R.string.plus_sign),
						style = TextStyle(
							fontSize = 24.sp
						),
					)
				}
			},
			topBar = {
				Box(modifier = Modifier)
			},
			bottomBar = {
				Box(modifier = Modifier)
			},
		) { scaffoldPadding ->
			LazyColumn(
				modifier = Modifier
					.fillMaxSize(1.0f)
					.padding(scaffoldPadding)
					.padding(
						horizontal = dimensionResource(id = R.dimen.s),
						vertical = dimensionResource(id = R.dimen.zero),
					),
				state = listState,
				verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {

				item {
					Column() {
						LazyRow(
							modifier = Modifier
								.fillMaxWidth(1.0f),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
						) {
							item {
								ButtonWithIcon(
									modifier = Modifier
										.width(dimensionResource(id = R.dimen.xl))
										.height(dimensionResource(id = R.dimen.l)),
//								TODO: make this dark mode conscious
									iconId = R.drawable.tune_round_black_24,
									onClick = {
										filterType.value = FilterType.ALL
										coroutineScope.launch {
											isBottomSheetOpen = true

										}
									},
									contentDescription = stringResource(id = R.string.filter_menu),
								)
							}
							listOf(
								FilterType.LOCATION,
								FilterType.FAVOURITE,
								FilterType.PRICE,
								FilterType.ROOMS,
								FilterType.PROPERTY_TYPE,
								FilterType.ROOMMATE,
								FilterType.DATES,
							).map {
								item {
									FilterButton(
										filterName = stringResource(id = it.stringId),
										onClick = {
											filterType.value = it
											coroutineScope.launch {
												isBottomSheetOpen = true

											}
										},
									)
								}
							}
						}

						if (isBottomSheetOpen) {
							ModalBottomSheet(
								modifier = Modifier.wrapContentSize(),
								onDismissRequest = { isBottomSheetOpen = false },
								sheetState = modelSheetState,
								containerColor = Color.White,
								content = {
									when (filterType.value) {
										FilterType.LOCATION -> {
											LocationFilter(
												currentLocationRange = uiState.locationRange,
												updateLocationFilter = ::updateLocationFilter,
												closeAction = ::closeBottomSheet
											)
										}

										FilterType.DATES -> DateFilter(
											currentDateRange = uiState.dateRange,
											updateDateFilter = ::updateDateFilter,
											coroutineScope = coroutineScope,
											closeAction = ::closeBottomSheet
										)

										FilterType.PRICE -> PriceFilter(
											currentPriceRange = uiState.priceRange,
											updatePriceFilter = ::updatePriceFilter,
											closeAction = ::closeBottomSheet
										)

										FilterType.ROOMS -> RoomFilter(
											currentRoomRange = uiState.roomRange,
											updateRoomFilter = ::updateRoomFilter,
											closeAction = ::closeBottomSheet
										)

										FilterType.PROPERTY_TYPE -> PropertyTypeFilter(
											currentHousingPref = uiState.houseTypePreference,
											updateHousingFilter = ::updateHousingFilter,
											closeAction = ::closeBottomSheet
										)

										FilterType.ROOMMATE -> RoommateFilter(
											currentGenderPref = uiState.genderPreference,
											updateGenderFilter = ::updateGenderFilter,
											closeAction = ::closeBottomSheet
										)

										FilterType.FAVOURITE -> FavouriteFilter(
											currentFavourite = uiState.filterFavourite,
											updateFavouriteFilter = ::updateFavouriteFilter,
											closeAction = ::closeBottomSheet
										)

										FilterType.ALL -> TODO()

									}
								},
							)
						}


					}
				}

				items(uiState.listingItems.listings.size) {
					val listingSummary = uiState.listingItems.listings[it]
					val listingImage = uiState.listingItems.listingsImages[it]
					ListingPost(
						listingSummary = listingSummary,
						listingImage = listingImage,
						detailsNavigation = {
							viewModel.navHostController.navigate(
								route = "${NavigationDestination.LISTING_DETAILS.rootNavPath}/${listingSummary.listingId}"
							)
						},
					)
				}
			}
		}

	}
}


@Composable
fun ButtonWithIcon(
	modifier: Modifier = Modifier,
	iconId: Int,
	onClick: () -> Unit,
	contentDescription: String,
	colors: ButtonColors = ButtonDefaults.buttonColors(
		containerColor = secondaryButtonBackgroundColor,
		contentColor = Color.Black
	),
) {
	SecondaryButton(onClick = onClick,
		modifier = modifier
			.wrapContentSize(align = Alignment.Center),
		contentPadding = PaddingValues(all = dimensionResource(id = R.dimen.zero)),
		colors = colors,
		content = {
			Icon(
				painter = painterResource(
					id = iconId
				),
				contentDescription = contentDescription,
			)
		})
}

@Composable
fun FilterButton(
	modifier: Modifier = Modifier,
	filterName: String,
	onClick: () -> Unit,
) {
	Box(
		modifier = modifier
			.wrapContentSize()
			.height(dimensionResource(id = R.dimen.l))
	) {
		Button(
			modifier = Modifier
				.wrapContentSize(),
			contentPadding = PaddingValues(
				horizontal = dimensionResource(id = R.dimen.s),
				vertical = dimensionResource(id = R.dimen.zero),
			),
			colors = ButtonDefaults.buttonColors(
				containerColor = secondaryButtonBackgroundColor,
				contentColor = secondaryTextColor,
			),
			shape = RoundedCornerShape(
				size = dimensionResource(id = R.dimen.s),
			),
			onClick = {
				onClick()
			},
		) {
			Row(
				modifier = Modifier.wrapContentWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
			) {
				Text(
					text = filterName,
					style = filterTextFont,
				)

				Icon(
					painter = painterResource(
						id = R.drawable.arrow_drop_down_solid_gray_24
					),
					contentDescription = stringResource(
						id = R.string.drop_down_arrow
					)
				)
			}
		}
	}
}

fun dateTimeFormatter(offsetDateTime: OffsetDateTime): String {
	// TODO: Localize this
	val formatter = DateTimeFormatter.ofPattern("MMM. yyyy")
	return offsetDateTime.format(formatter)
}

@Preview
@Composable
@Suppress("UnusedPrivateMember")
private fun HomeListViewPreview() {
	SubletrTheme {
		HomeListChildView(
			modifier = Modifier,
			viewModel = HomeListChildViewModel(
				ListingsApi(),
				NavigationService(context = LocalContext.current),
			),
			uiState = HomeListUiState.Loaded(
				locationRange = HomePageUiState.LocationRange(null, null),
				priceRange = HomePageUiState.PriceRange(null, null),
				roomRange = HomePageUiState.RoomRange(),
				listingItems = HomePageUiState.ListingItemsModel(
					listings = emptyList(),
					likedListings = emptySet(),
					listingsImages = emptyList(),
				),
				genderPreference = Gender.OTHER,
				houseTypePreference = HousingType.OTHER,
				dateRange = HomeListUiState.DateRange(),
				infoTextStringId = null,
				filterFavourite = false,
			)
		)
	}
}

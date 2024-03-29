package org.uwaterloo.subletr.pages.home.list

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.enums.FilterType
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.pages.home.HomePageViewModel
import org.uwaterloo.subletr.pages.home.components.LocationSearchTextField
import org.uwaterloo.subletr.pages.home.components.filters.AllFilter
import org.uwaterloo.subletr.pages.home.components.filters.DateFilter
import org.uwaterloo.subletr.pages.home.components.filters.FavouriteFilter
import org.uwaterloo.subletr.pages.home.components.filters.LocationFilter
import org.uwaterloo.subletr.pages.home.components.filters.PriceFilter
import org.uwaterloo.subletr.pages.home.components.filters.PropertyTypeFilter
import org.uwaterloo.subletr.pages.home.components.filters.RatingFilter
import org.uwaterloo.subletr.pages.home.components.filters.RoomFilter
import org.uwaterloo.subletr.pages.home.components.filters.RoommateFilter
import org.uwaterloo.subletr.pages.home.components.filters.VerifiedPostFilter
import org.uwaterloo.subletr.pages.home.list.components.ButtonWithIcon
import org.uwaterloo.subletr.pages.home.list.components.FilterButton
import org.uwaterloo.subletr.pages.home.list.components.HomeListListingItemView
import org.uwaterloo.subletr.services.LocationService
import org.uwaterloo.subletr.services.NavigationService
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.subletrPalette
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
	val coroutineScope = rememberCoroutineScope()

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestMultiplePermissions(),
	) { permissions ->
		if (uiState is HomeListUiState.Loaded) {
			when {
				permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
					coroutineScope.launch {
						viewModel.setLocationToCurrentLocation(uiState = uiState)
					}
				}
				permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
					coroutineScope.launch {
						viewModel.setLocationToCurrentLocation(uiState = uiState)
					}
				} else -> {
				Log.d("LocationLogs", "No Location Permissions")
			}
			}
		}
	}

	Scaffold(
		modifier = modifier.fillMaxSize(1.0f),
		floatingActionButtonPosition = FabPosition.End,
		floatingActionButton = {
			FloatingActionButton(
				modifier = Modifier.padding(
					all = dimensionResource(id = R.dimen.zero),
				),
				onClick = {
					if (uiState is HomeListUiState.Loaded && uiState.userListingId != null) {
						viewModel.navHostController.navigate(
							route = "${NavigationDestination.MANAGE_LISTING.rootNavPath}/${uiState.userListingId}"
						)
					} else {
						viewModel.navHostController.navigate(NavigationDestination.CREATE_LISTING.fullNavPath)
					}
				},
				shape = CircleShape,
				containerColor = MaterialTheme.subletrPalette.subletrPink,
				contentColor = MaterialTheme.subletrPalette.textOnSubletrPink,
			) {
				if (uiState is HomeListUiState.Loaded && uiState.userListingId != null) {
					Icon(
						painter = painterResource(
							id = R.drawable.edit_outline_black_24
						),
						contentDescription = stringResource(id = R.string.edit),
						tint = MaterialTheme.subletrPalette.textOnSubletrPink,
					)
				} else {
					Text(
						stringResource(id = R.string.plus_sign),
						style = TextStyle(
							fontSize = 24.sp
						),
						color = MaterialTheme.subletrPalette.textOnSubletrPink,
					)
				}

			}
		},
		topBar = {
			Box(modifier = Modifier)
		},
		bottomBar = {
			Box(modifier = Modifier)
		},
	) { scaffoldPadding ->
		if (uiState is HomeListUiState.Loading) {
			Column(
				modifier = Modifier
					.fillMaxSize(1.0f)
					.padding(paddingValues = scaffoldPadding)
					.imePadding(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center,
			) {
				CircularProgressIndicator()
			}
		} else if (uiState is HomeListUiState.Loaded) {
			var filterType by remember { mutableStateOf(FilterType.LOCATION) }
			val modalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
			var isBottomSheetOpen by remember {
				mutableStateOf(false)
			}
			val listState: LazyListState = rememberLazyListState()
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
					viewModel.getListingParamsStream.onNext(
						HomePageViewModel.GetListingParams(
							filters = uiState.filters,
							transportationMethod = HomePageUiState.TransportationMethod.WALK,
							listingPagingParams = HomePageViewModel.ListingPagingParams(
								previousListingItemsModel = uiState.listingItems,
								pageNumber = listState.layoutInfo.totalItemsCount.floorDiv(
									HomePageViewModel.LISTING_PAGE_SIZE
								)
							),
							homePageView = HomePageUiState.HomePageViewType.LIST,
						)
					)
				}
			}

			fun updateLocationFilter(newVal: HomePageUiState.LocationRange) {
				viewModel.getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(locationRange = newVal),
						transportationMethod = HomePageUiState.TransportationMethod.WALK,
						homePageView = HomePageUiState.HomePageViewType.LIST,
					)
				)
			}

			fun updatePriceFilter(newVal: HomePageUiState.PriceRange) {
				viewModel.getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(priceRange = newVal),
						transportationMethod = HomePageUiState.TransportationMethod.WALK,
						homePageView = HomePageUiState.HomePageViewType.LIST,
					)
				)
			}

			fun updateRoomFilter(newVal: HomePageUiState.RoomRange) {
				viewModel.getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(roomRange = newVal),
						transportationMethod = HomePageUiState.TransportationMethod.WALK,
						homePageView = HomePageUiState.HomePageViewType.LIST,
					)
				)
			}

			fun updateGenderFilter(newVal: Gender) {
				viewModel.getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(gender = newVal),
						transportationMethod = HomePageUiState.TransportationMethod.WALK,
						homePageView = HomePageUiState.HomePageViewType.LIST,
					)
				)
			}

			fun updateHousingFilter(newVal: HousingType) {
				viewModel.getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(housingType = newVal),
						transportationMethod = HomePageUiState.TransportationMethod.WALK,
						homePageView = HomePageUiState.HomePageViewType.LIST,
					)
				)
			}

			fun updateDateFilter(newVal: HomePageUiState.DateRange) {
				viewModel.getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(dateRange = newVal),
						transportationMethod = HomePageUiState.TransportationMethod.WALK,
						homePageView = HomePageUiState.HomePageViewType.LIST,
					)
				)
			}

			fun updateFavouriteFilter(newVal: Boolean) {
				viewModel.getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(favourite = newVal),
						transportationMethod = HomePageUiState.TransportationMethod.WALK,
						homePageView = HomePageUiState.HomePageViewType.LIST,
					)
				)
			}

			fun updateRatingFilter(newVal: Int) {
				viewModel.getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(minRating = newVal),
						transportationMethod = HomePageUiState.TransportationMethod.WALK,
						homePageView = HomePageUiState.HomePageViewType.LIST,
					)
				)
			}

			fun updateVerifiedPostFilter(newVal: Boolean) {
				viewModel.getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(showVerifiedOnly = newVal),
						transportationMethod = HomePageUiState.TransportationMethod.WALK,
						homePageView = HomePageUiState.HomePageViewType.LIST,
					)
				)
			}

			fun updateAllFilters(newVal: HomePageUiState.FiltersModel) {
				viewModel.getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = newVal,
						transportationMethod = HomePageUiState.TransportationMethod.WALK,
						homePageView = HomePageUiState.HomePageViewType.LIST,
					)
				)
			}

			fun closeBottomSheet() {
				coroutineScope.launch {
					isBottomSheetOpen = false
				}
			}
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
				userScrollEnabled = true,
			) {
				item {
					LocationSearchTextField(
						addressSearch = uiState.addressSearch,
						onValueChange = {
							viewModel.uiStateStream.onNext(
								uiState.copy(
									addressSearch = it,
								),
							)
						},
						onLocationIconClick = {
							launcher.launch(LocationService.locationPermissions)
						}
					)
				}

				item {
					Column {
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
									iconId = R.drawable.tune_round_black_24,
									onClick = {
										filterType = FilterType.ALL
										coroutineScope.launch {
											isBottomSheetOpen = true
										}
									},
									border = BorderStroke(
										width = dimensionResource(id = R.dimen.xxxxs),
										color = MaterialTheme.subletrPalette.textFieldBorderColor,
									),
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
								FilterType.RATING,
								FilterType.VERIFIEDPOST
							).map {
								item {
									FilterButton(
										filterName = stringResource(id = it.stringId),
										onClick = {
											filterType = it
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
								sheetState = modalSheetState,
								containerColor = MaterialTheme.subletrPalette.bottomSheetColor,
								content = {
									when (filterType) {
										FilterType.LOCATION -> {
											LocationFilter(
												currentLocationRange = uiState.filters.locationRange,
												updateLocationFilter = ::updateLocationFilter,
												closeAction = ::closeBottomSheet,
											)
										}

										FilterType.DATES -> DateFilter(
											currentDateRange = uiState.filters.dateRange,
											updateDateFilter = ::updateDateFilter,
											coroutineScope = coroutineScope,
											closeAction = ::closeBottomSheet,
										)

										FilterType.PRICE -> PriceFilter(
											currentPriceRange = uiState.filters.priceRange,
											updatePriceFilter = ::updatePriceFilter,
											closeAction = ::closeBottomSheet,
										)

										FilterType.ROOMS -> RoomFilter(
											currentRoomRange = uiState.filters.roomRange,
											updateRoomFilter = ::updateRoomFilter,
											closeAction = ::closeBottomSheet,
										)

										FilterType.PROPERTY_TYPE -> PropertyTypeFilter(
											currentHousingPref = uiState.filters.housingType,
											updateHousingFilter = ::updateHousingFilter,
											closeAction = ::closeBottomSheet,
										)

										FilterType.ROOMMATE -> RoommateFilter(
											currentGenderPref = uiState.filters.gender,
											updateGenderFilter = ::updateGenderFilter,
											closeAction = ::closeBottomSheet,
										)

										FilterType.FAVOURITE -> FavouriteFilter(
											currentFavourite = uiState.filters.favourite,
											updateFavouriteFilter = ::updateFavouriteFilter,
											closeAction = ::closeBottomSheet,
										)

										FilterType.RATING -> RatingFilter(
											currentRatingPref = uiState.filters.minRating,
											updateRatingFilter = ::updateRatingFilter,
											closeAction = ::closeBottomSheet,
										)

										FilterType.ALL -> AllFilter(
											currentFilterVals = uiState.filters,
											updateFilterVals = ::updateAllFilters,
											coroutineScope = coroutineScope,
											closeAction = ::closeBottomSheet,
										)

										FilterType.VERIFIEDPOST -> VerifiedPostFilter(
											currentVerified = uiState.filters.showVerifiedOnly,
											updateFavouriteFilter = ::updateVerifiedPostFilter,
											closeAction = ::closeBottomSheet,
										)
									}
								},
							)
						}
					}
				}

				items(uiState.listingItems) {
					HomeListListingItemView(
						listingItem = it,
						viewModel = viewModel,
					)
				}
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
				locationService = LocationService(context = LocalContext.current),
				navigationService = NavigationService(context = LocalContext.current),
			),
			uiState = HomeListUiState.Loaded(
				addressSearch = "",
				listingItems = emptyList(),
				filters = HomePageUiState.FiltersModel(
					locationRange = HomePageUiState.LocationRange(null, null),
					priceRange = HomePageUiState.PriceRange(null, null),
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
				userListingId = null,
			),
		)
	}
}

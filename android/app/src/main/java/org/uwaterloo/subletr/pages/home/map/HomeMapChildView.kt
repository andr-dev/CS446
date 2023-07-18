package org.uwaterloo.subletr.pages.home.map

import android.util.Log
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.pages.home.HomePageViewModel
import org.uwaterloo.subletr.pages.home.map.components.HomeMapFiltersRowView
import org.uwaterloo.subletr.pages.home.map.components.HomeMapListingItemView
import org.uwaterloo.subletr.services.LocationService
import org.uwaterloo.subletr.services.LocationService.Companion.locationPermissions
import org.uwaterloo.subletr.services.NavigationService
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.timeToDestinationFont
import org.uwaterloo.subletr.theme.unselectedGray
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeMapChildView(
	modifier: Modifier = Modifier,
	viewModel: HomeMapChildViewModel,
	uiState: HomeMapUiState,
) {
	val coroutineScope = rememberCoroutineScope()
	val cameraPositionState = rememberCameraPositionState {
		position = CameraPosition.fromLatLngZoom(WATERLOO, DEFAULT_ZOOM)
	}
	var scrollEnabled: Boolean by remember{ mutableStateOf(true) }
	LaunchedEffect(cameraPositionState.isMoving) {
		if (!cameraPositionState.isMoving) {
			scrollEnabled = true
		}
	}

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestMultiplePermissions(),
	) { permissions ->
		when {
			permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
				coroutineScope.launch {
					viewModel.getLocation()
				}
			}
			permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
				coroutineScope.launch {
					viewModel.getLocation()
				}
			} else -> {
				Log.d("LocationLogs", "No Location Permissions")
			}
		}
	}

	Scaffold(
		modifier = modifier
			.fillMaxSize(fraction = 1.0f),
		topBar = {Box{}},
		bottomBar = {Box{}}
	) { paddingValues ->
		if (uiState is HomeMapUiState.Loading) {
			Column(
				modifier = Modifier.padding(
					paddingValues = paddingValues,
				),
			) {
				CircularProgressIndicator()
			}
		}
		else if (uiState is HomeMapUiState.Loaded) {
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
							transportationMethod = uiState.transportationMethod,
							listingPagingParams = HomePageViewModel.ListingPagingParams(
								previousListingItemsModel = uiState.listingItems,
								pageNumber = listState.layoutInfo.totalItemsCount
									.floorDiv(HomePageViewModel.LISTING_PAGE_SIZE)
							),
							homePageView = HomePageUiState.HomePageViewType.MAP,
						)
					)
				}
			}

			LazyColumn(
				modifier = Modifier.padding(
					paddingValues = paddingValues,
				),
				state = listState,
				userScrollEnabled = scrollEnabled,
			) {
				item {
					RoundedTextField(
						modifier = Modifier
							.fillMaxWidth(fraction = 1.0f)
							.padding(horizontal = dimensionResource(id = R.dimen.xs)),
						value = uiState.addressSearch,
						onValueChange = {
							viewModel.uiStateStream.onNext(
								uiState.copy(
									addressSearch = it,
								),
							)
						},
						placeholder = {
							Text(
								text = stringResource(id = R.string.address_city_postal_code),
								color = secondaryTextColor,
							)
						},
						leadingIcon = {
							Icon(
								painter = painterResource(id = R.drawable.search_solid_gray_24),
								contentDescription = stringResource(id = R.string.search),
								tint = unselectedGray,
							)
						},
						trailingIcon = {
							IconButton(onClick = {launcher.launch(locationPermissions)}) {
								Icon(
									painter = painterResource(id = R.drawable.my_location_solid_pink_24),
									contentDescription = stringResource(id = R.string.my_location),
									tint = subletrPink,
								)
							}
						},
					)
				}
				item {
					Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
				}
				item {
					HomeMapFiltersRowView(
						modifier = Modifier,
						uiState = uiState,
						updateTransportationMethod = {
							viewModel.getListingParamsStream.onNext(
								HomePageViewModel.GetListingParams(
									filters = uiState.filters,
									transportationMethod = it,
									homePageView = HomePageUiState.HomePageViewType.MAP,
								)
							)
						},
					)
				}
				item {
					Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
				}
				item {
					GoogleMap(
						modifier = Modifier
							.padding(horizontal = dimensionResource(id = R.dimen.xs))
							.fillMaxWidth(fraction = 1.0f)
							.aspectRatio(ratio = 1.0f)
							.pointerInteropFilter(
								onTouchEvent = {
									when (it.action) {
										MotionEvent.ACTION_DOWN -> {
											scrollEnabled = false
											false
										}

										MotionEvent.ACTION_UP -> {
											scrollEnabled = true
											true
										}

										else -> {
											true
										}
									}
								}
							),
						contentDescription = stringResource(id = R.string.google_maps_view),
						cameraPositionState = cameraPositionState,
					) {
						// TODO: Add when ListingSummary is updated with coordinates
//						uiState.listingItems.listings.forEachIndexed { index, listingSummary ->
//							val selected = uiState.listingItems.selectedListings.getOrElse(index, {false})
//							if (selected) {
//								Marker(
//									state = MarkerState(position = listingSummary),
//								)
//							}
//						}
					}
				}
				item {
					Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
				}
				item {
					Slider(
						modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.m)),
						value = uiState.timeToDestination,
						onValueChange = {
							viewModel.uiStateStream.onNext(
								uiState.copy(
									timeToDestination = it,
								)
							)
						},
						colors = SliderDefaults.colors(
							thumbColor = subletrPink,
							activeTrackColor = subletrPink,
						),
						valueRange = 0.0f..MAX_DISTANCE_IN_MINUTES
					)
				}
				item {
					Row(
						modifier = Modifier
							.padding(horizontal = dimensionResource(id = R.dimen.xs))
							.fillMaxWidth(),
						horizontalArrangement = Arrangement.Center,
					) {
						Text(
							text = stringResource(
								id = R.string.time_to_destination_integer_minutes,
								(uiState.timeToDestination).roundToInt(),
							),
							style = timeToDestinationFont,
						)
					}
				}
				item {
					Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
				}
				items(count = uiState.listingItems.listings.size) {
					val listingSummary = uiState.listingItems.listings[it]
					val listingImage = uiState.listingItems.listingsImages.getOrNull(it)
					val selected = uiState.listingItems.selectedListings.getOrElse(it) { false }
					HomeMapListingItemView(
						listingSummary = listingSummary,
						listingImage = listingImage,
						selected = selected,
						viewModel = viewModel,
						setSelected = { newSelected ->
							viewModel.uiStateStream.onNext(
								uiState.copy(
									listingItems = uiState.listingItems.copy(
										selectedListings = uiState.listingItems.selectedListings
											.mapIndexed { index, b ->
												if (it == index) {
													newSelected
												}
												else {
													b
												}
											}
										),
								)
							)
						}
					)
				}
			}
		}
	}
}

const val MAX_DISTANCE_IN_MINUTES = 180.0f
private const val DEFAULT_ZOOM = 12.0f
private val WATERLOO = LatLng(
	43.4643,
	-80.5204,
)

@Preview
@Composable
fun HomeMapChildViewLoadingPreview() {
	SubletrTheme {
		HomeMapChildView(
			viewModel = HomeMapChildViewModel(
				locationService = LocationService(context = LocalContext.current),
				navigationService = NavigationService(context = LocalContext.current),
			),
			uiState = HomeMapUiState.Loading,
		)
	}
}

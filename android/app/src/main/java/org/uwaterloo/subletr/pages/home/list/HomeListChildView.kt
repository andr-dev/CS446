package org.uwaterloo.subletr.pages.home.list

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.enums.FilterType
import org.uwaterloo.subletr.enums.RoomRange
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.home.list.components.LocationFilterForm
import org.uwaterloo.subletr.pages.home.list.components.PriceFilterForm
import org.uwaterloo.subletr.services.NavigationService
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.darkerGrayButtonColor
import org.uwaterloo.subletr.theme.filterTextFont
import org.uwaterloo.subletr.theme.listingDescriptionFont
import org.uwaterloo.subletr.theme.listingTitleFont
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.secondaryTextColor
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

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
		val scaffoldState = rememberBottomSheetScaffoldState()
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
		fun updateLocationFilter(newVal: HomeListUiState.LocationRange) {
			viewModel.locationRangeFilterStream.onNext(newVal)
		}

		fun updatePriceFilter(newVal: HomeListUiState.PriceRange) {
			viewModel.priceRangeFilterStream.onNext(newVal)
		}
		BottomSheetScaffold(
			modifier = Modifier.fillMaxSize(1.0f),
			scaffoldState = scaffoldState,
			sheetContent = {
//TODO: remove the add button that is on top of the bottomsheet
				when (filterType.value) {
					FilterType.LOCATION -> {
						LocationFilterForm(
							uiState.locationRange,
							::updateLocationFilter
						) { closeBottomSheet(coroutineScope, scaffoldState) }
					}

					FilterType.PRICE -> PriceFilterForm(
						currentPriceRange = uiState.priceRange,
						updatePriceFilter = ::updatePriceFilter
					) { closeBottomSheet(coroutineScope, scaffoldState) }

					FilterType.ROOMS -> TODO()
					FilterType.PROPERTY_TYPE -> TODO()
					FilterType.ROOMMATE -> TODO()
					FilterType.ALL -> TODO()
				}


			},
			sheetSwipeEnabled = false,
			sheetContainerColor = Color.White,
			sheetPeekHeight = dimensionResource(id = R.dimen.zero)
		) {
			LazyColumn(
				modifier = modifier
					.fillMaxSize(1.0f)
					.padding(
						dimensionResource(id = R.dimen.s),
						dimensionResource(id = R.dimen.zero)
					),
				state = listState,
				verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				item {
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
									filterType.value = FilterType.ALL
									coroutineScope.launch {
										scaffoldState.bottomSheetState.expand()

									}
								},
								contentDescription = stringResource(id = R.string.filter_menu),
							)
						}
						item {
							FilterButton(
								filterName = stringResource(id = R.string.location),
								onClick = {
									filterType.value = FilterType.LOCATION
									coroutineScope.launch {
										scaffoldState.bottomSheetState.expand()

									}
								}

							)
						}
						item {
							FilterButton(
								filterName = stringResource(id = R.string.price),
								onClick = {
									filterType.value = FilterType.PRICE
									coroutineScope.launch {
										scaffoldState.bottomSheetState.expand()

									}
								}
							)
						}
						item {
							FilterButton(
								filterName = stringResource(id = R.string.rooms),
								onClick = {
									filterType.value = FilterType.ROOMS
									coroutineScope.launch {
										scaffoldState.bottomSheetState.expand()

									}
								}
							)
						}
						item {
							FilterButton(
								filterName = stringResource(id = R.string.property_type),
								onClick = {
									filterType.value = FilterType.PROPERTY_TYPE
									coroutineScope.launch {
										scaffoldState.bottomSheetState.expand()

									}
								}
							)
						}
						item {
							FilterButton(
								filterName = stringResource(id = R.string.roommate),
								onClick = {
									filterType.value = FilterType.ROOMMATE
									coroutineScope.launch {
										scaffoldState.bottomSheetState.expand()

									}
								}
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


@OptIn(ExperimentalMaterial3Api::class)
fun closeBottomSheet(
	coroutineScope: CoroutineScope, scaffoldState: BottomSheetScaffoldState,
) {
	coroutineScope.launch {
		scaffoldState.bottomSheetState.partialExpand()

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
fun ListingPost(
	modifier: Modifier = Modifier,
	listingSummary: ListingSummary,
	listingImage: Bitmap?,
	detailsNavigation: () -> Unit,
) {
	Box(
		modifier = modifier
			.wrapContentHeight()
			.fillMaxWidth(1.0f)
			.clip(
				RoundedCornerShape(
					dimensionResource(id = R.dimen.xxs)
				)
			)
			.background(secondaryButtonBackgroundColor)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth(1.0f)
				.padding(
					dimensionResource(id = R.dimen.s),
					dimensionResource(id = R.dimen.s),
					dimensionResource(id = R.dimen.s),
					dimensionResource(id = R.dimen.xs),
				),
			verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs))

		) {
			Row(
				modifier = Modifier
					.fillMaxWidth(1.0f)

			) {
				if (listingImage != null) {
					Image(
						modifier = Modifier
							.height(dimensionResource(id = R.dimen.xxxl))
							.width(dimensionResource(id = R.dimen.xxxl)),
						bitmap = listingImage.asImageBitmap(),
						contentDescription = stringResource(id = R.string.listing_image),
						contentScale = ContentScale.Crop,
					)
				} else {
					Image(
						modifier = Modifier
							.height(dimensionResource(id = R.dimen.xxxl))
							.width(dimensionResource(id = R.dimen.xxxl)),
						painter = painterResource(
							id = R.drawable.room,
						),
						contentDescription = stringResource(id = R.string.listing_image),
						contentScale = ContentScale.Crop,
					)
				}
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.s)))
				Column(
					modifier = Modifier
						.fillMaxWidth(1.0f),
					horizontalAlignment = Alignment.Start,
					verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxxs))

				) {
					Text(
						listingSummary.address,
						style = listingTitleFont,
						overflow = TextOverflow.Clip,
						textAlign = TextAlign.Start,
						maxLines = 1
					)
					Text(
						stringResource(
							id = R.string.dollar_sign_n,
							listingSummary.price,
						),
						style = listingTitleFont,
					)
					Text(
						text = stringResource(
							id = R.string.short_for_all_date_range,
							dateTimeFormater(listingSummary.leaseStart),
							dateTimeFormater(offsetDateTime = listingSummary.leaseEnd),
						),
						style = MaterialTheme.typography.bodyLarge,
					)
				}
			}
			Row(
				modifier = Modifier
					.fillMaxWidth(1.0f),
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					painter = painterResource(
						id = R.drawable.bed_solid_gray_16,
					),
					contentDescription = stringResource(id = R.string.bed_icon),
					tint = secondaryTextColor
				)
				Text(
					pluralStringResource(
						id = R.plurals.n_bedrooms,
						count = listingSummary.rooms,
						listingSummary.rooms,
					),
					style = listingDescriptionFont,
				)
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)))
				Icon(
					painter = painterResource(
						id =
						when (listingSummary.residenceType) {
							ResidenceType.apartment -> R.drawable.apartment_solid_gray_16
							ResidenceType.other -> R.drawable.other_houses_solid_gray_16
							else -> R.drawable.home_outline_gray_16
						}

					),
					contentDescription = stringResource(R.string.home_icon),
					tint = secondaryTextColor
				)
				Text(
					text = listingSummary.residenceType.value,
					style = listingDescriptionFont,
				)
			}
			Row(
				modifier = Modifier
					.height(40.dp),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				SecondaryButton(
					modifier = Modifier
						.fillMaxWidth(0.5f)
						.fillMaxHeight(1.0f),
					colors = ButtonDefaults.buttonColors(
						containerColor = darkerGrayButtonColor,
						contentColor = Color.Black
					),
					onClick = detailsNavigation,
				) {

					Text(
						stringResource(id = R.string.view_details),
						style = MaterialTheme.typography.bodyLarge
					)

				}
				ButtonWithIcon(
					modifier = Modifier
						.fillMaxHeight(1.0f)
						.width(dimensionResource(id = R.dimen.xxxl)),
					colors = ButtonDefaults.buttonColors(
						containerColor = darkerGrayButtonColor,
						contentColor = Color.Black
					),
					iconId = R.drawable.chat_bubble_outline_gray_24,
					onClick = { /*TODO*/ },
					contentDescription = stringResource(id = R.string.chat_icon)
				)
				ButtonWithIcon(
					modifier = Modifier
						.fillMaxHeight(1.0f)
						.width(dimensionResource(id = R.dimen.xxxl)),
					colors = ButtonDefaults.buttonColors(
						containerColor = darkerGrayButtonColor,
						contentColor = Color.Black
					),
					iconId = R.drawable.star_outline_black_26,
					onClick = { /*TODO*/ },
					contentDescription = stringResource(id = R.string.chat_icon)
				)
			}
		}
	}
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

fun dateTimeFormater(offsetDateTime: OffsetDateTime): String {
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
			viewModel = HomeListChildViewModel(ListingsApi(), NavigationService()),
			uiState = HomeListUiState.Loaded(
				locationRange = HomeListUiState.LocationRange(null, null),
				priceRange = HomeListUiState.PriceRange(null, null),
				roomRange = RoomRange.FOURTOFIVE,
				listingItems = HomeListUiState.ListingItemsModel(
					listings = emptyList(),
					likedListings = emptySet(),
					listingsImages = emptyList(),
				),
				infoTextStringId = null,
			)
		)
	}
}

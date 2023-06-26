package org.uwaterloo.subletr.pages.home

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.models.GetListingsResponse
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.enums.LocationRange
import org.uwaterloo.subletr.enums.PriceRange
import org.uwaterloo.subletr.enums.RoomRange
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.darkerGrayButtonColor
import org.uwaterloo.subletr.theme.filterTextFont
import org.uwaterloo.subletr.theme.listingDescriptionFont
import org.uwaterloo.subletr.theme.listingTitleFont
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomePageView(
	modifier: Modifier = Modifier,
	viewModel: HomePageViewModel = hiltViewModel(),
	uiState: HomePageUiState = viewModel.uiStateStream.subscribeAsState(
		HomePageUiState.Loading
	).value,

	) {

	if (uiState is HomePageUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f)
				.imePadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			CircularProgressIndicator()
		}
	} else if (uiState is HomePageUiState.Loaded) {
		LaunchedEffect(key1 = true) {
			viewModel.priceRangeFilterStream.onNext(viewModel.priceRangeFilterStream.value!!)
		}
		val listState = rememberLazyListState()
		val isListView = remember { mutableStateOf(true) }

		Scaffold(
			modifier = Modifier
				.padding(
					dimensionResource(id = R.dimen.s),
					dimensionResource(id = R.dimen.zero),
					dimensionResource(id = R.dimen.s),
					dimensionResource(id = R.dimen.zero),
				)
				.imePadding(),
			topBar = {
				Row(
					modifier = Modifier
						.fillMaxWidth(1.0f)
						.padding(
							start = dimensionResource(id = R.dimen.zero),
							top = dimensionResource(id = R.dimen.xl),
							end = dimensionResource(id = R.dimen.zero),
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
			content = { padding ->
				LazyColumn(
					modifier = Modifier
						.padding(padding)
						.fillMaxWidth(1.0f)
						.wrapContentHeight()
						.padding(
							dimensionResource(id = R.dimen.zero),
							dimensionResource(id = R.dimen.zero),
							dimensionResource(id = R.dimen.zero),
							dimensionResource(id = R.dimen.xxxxl)
						)
						.imePadding(),
					state = listState,
					verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
					horizontalAlignment = Alignment.CenterHorizontally,
				) {

					item {
						Row(
							modifier = Modifier
								.fillMaxWidth(1.0f),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.SpaceBetween,
						) {
							ButtonWithIcon(
								modifier = Modifier
									.width(dimensionResource(id = R.dimen.xl))
									.height(dimensionResource(id = R.dimen.l)),

								iconId = R.drawable.tune_round_black_24,
								onClick = {},
								contentDescription = stringResource(id = R.string.filter_menu),
							)
							FilterDropDown(
								filterName = stringResource(id = R.string.location),
								dropDownItems = LocationRange.values().map { it.stringId }
									.toTypedArray(),
								updateState = { newVal ->
									viewModel.locationRangeFilterStream.onNext(
										LocationRange.fromInt(newVal)
									)
								},
								selectedValue = uiState.locationRange.stringId,
								width = dimensionResource(id = R.dimen.xxxxl),
							)
							FilterDropDown(
								filterName = stringResource(id = R.string.price),
								dropDownItems = PriceRange.values().map { it.stringId }
									.toTypedArray(),
								updateState = { newVal ->
									viewModel.priceRangeFilterStream.onNext(
										PriceRange.fromInt(newVal)
									)
								},
								selectedValue = uiState.priceRange.stringId,
								width = dimensionResource(id = R.dimen.xxxl),

								)
							FilterDropDown(
								filterName = stringResource(id = R.string.rooms),
								dropDownItems = RoomRange.values().map { it.stringId }
									.toTypedArray(),
								updateState = { newVal ->
									viewModel.roomRangeFilterStream.onNext(
										RoomRange.fromInt(newVal)
									)
								},
								selectedValue = uiState.roomRange.stringId,
								width = dimensionResource(id = R.dimen.xxxxl)
							)
						}

					}
					items(uiState.listings.listings.size) {
						val listingSummary = uiState.listings.listings[it]
						ListingPost(
							listingSummary = listingSummary,
							detailsNavigation = {
								viewModel.navHostController.navigate(
									route = "${NavigationDestination.LISTING_DETAILS.rootNavPath}/${listingSummary.listingId}"
								)
							},
						)
					}

				}
			},
			floatingActionButtonPosition = FabPosition.End,
			floatingActionButton = {
				FloatingActionButton(
					modifier = modifier.padding(
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
						stringResource(id = R.string.plus_sign), style = TextStyle(
							fontSize = 24.sp
						)
					)
				}
			},
		)
	}
}

fun dateTimeFormater(offsetDateTime: OffsetDateTime): String {
	// TODO: Localize this
	val formatter = DateTimeFormatter.ofPattern("MMM. yyyy")
	return offsetDateTime.format(formatter)
}

@Composable
fun ListingPost(
	modifier: Modifier = Modifier,
	listingSummary: ListingSummary,
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
				Image(
					modifier = Modifier
						.height(dimensionResource(id = R.dimen.xxxl))
						.width(dimensionResource(id = R.dimen.xxxl)),
					painter = painterResource(id = R.drawable.room),
					contentDescription = stringResource(id = R.string.room),
					contentScale = ContentScale.Crop
				)
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
						if (listingSummary.residenceType == ResidenceType.apartment)
							R.drawable.apartment_solid_gray_16
						else if (listingSummary.residenceType == ResidenceType.other)
							R.drawable.other_houses_solid_gray_16
						else
							R.drawable.home_outline_gray_16

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
fun FilterDropDown(
	modifier: Modifier = Modifier,
	filterName: String,
	dropDownItems: Array<Int>,
	updateState: (Int) -> Unit,
	@StringRes selectedValue: Int,
	width: Dp,
) {
	var expanded by remember { mutableStateOf(false) }

	Box(
		modifier = modifier
			.width(width)
			.height(dimensionResource(id = R.dimen.l))
			.fillMaxWidth(1.0f),
	) {
		Button(
			modifier = Modifier
				.fillMaxWidth(1.0f)
				.fillMaxHeight(1.0f),
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
				expanded = !expanded
			},
		) {
			Row(
				modifier = Modifier.fillMaxWidth(fraction = 1.0f),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
			) {
				Text(
					text = filterName,
					style = filterTextFont,
				)

				Icon(
					painter = painterResource(
						id =
						if (expanded) R.drawable.arrow_drop_up_soild_gray_24
						else R.drawable.arrow_drop_down_solid_gray_24
					),
					contentDescription = stringResource(
						id =
						if (expanded) R.string.drop_up_arrow
						else R.string.drop_down_arrow
					)
				)
			}
		}
		DropdownMenu(
			modifier = modifier
				.background(color = Color.White),
			expanded = expanded,
			onDismissRequest = { expanded = false },
		) {
			dropDownItems.forEach {
				DropdownMenuItem(
					modifier = Modifier,
					text = {
						Text(
							text = stringResource(it),
							style = filterTextFont,
							color = if (it == selectedValue && it != R.string.clear) subletrPink else Color.Black
						)
					},
					contentPadding = PaddingValues(
						horizontal = dimensionResource(id = R.dimen.s),
						vertical = dimensionResource(id = R.dimen.zero),
					),
					onClick = {
						updateState(it)
						expanded = false
					},
				)
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
fun ViewSwitch(modifier: Modifier = Modifier, isListView: MutableState<Boolean>) {
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
						id = R.drawable.view_list_outline_pink_24
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
			uiState = HomePageUiState.Loaded(
				locationRange = LocationRange.NOFILTER,
				priceRange = PriceRange.NOFILTER,
				roomRange = RoomRange.NOFILTER,
				listings = GetListingsResponse(listOf(), setOf()),
				infoTextStringId = null,
			),
		)
	}
}

package org.uwaterloo.subletr.pages.listingdetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.SubletrTypography
import org.uwaterloo.subletr.theme.primaryTextColor
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.textFieldBackgroundColor
import org.uwaterloo.subletr.theme.textOnSubletrPink
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListingDetailsPageView(
	modifier: Modifier = Modifier,
	viewModel: ListingDetailsPageViewModel = hiltViewModel(),
	uiState: ListingDetailsPageUiState = viewModel.uiStateStream.subscribeAsState(
		ListingDetailsPageUiState.Loading
	).value,
) {
	val scrollState = rememberScrollState()
	val coroutineScope = rememberCoroutineScope()
	Scaffold(
		modifier = modifier,
		topBar = {
			Row(
				modifier = Modifier,
				horizontalArrangement = Arrangement.Start,
				verticalAlignment = Alignment.CenterVertically,
			) {
				IconButton(
					modifier = Modifier,
					onClick = {
						viewModel.navHostController.popBackStack()
					},
				) {
					Icon(
						painter = painterResource(
							id = R.drawable.arrow_back_solid_black_24
						),
						contentDescription = stringResource(id = R.string.back_arrow),
					)
				}
			}
		},
	) { paddingValues ->
		if (uiState is ListingDetailsPageUiState.Loading) {
			Column(
				modifier = Modifier
					.padding(paddingValues = paddingValues)
					.fillMaxSize(1.0f)
					.imePadding(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center,
			) {
				CircularProgressIndicator()
			}
		} else if (uiState is ListingDetailsPageUiState.Loaded) {
			val pagerState = rememberPagerState(
				initialPage = 0,
				initialPageOffsetFraction = 0f,
			) { uiState.images.size }
			Column(
				modifier = Modifier
					.padding(top = paddingValues.calculateTopPadding())
					.fillMaxSize(fraction = 1.0f)
					.verticalScroll(scrollState),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center,
			) {
				Box(
					modifier = Modifier
				) {
					if (uiState.images.isNotEmpty()) {
						HorizontalPager(
							modifier = Modifier,
							state = pagerState,
							contentPadding = PaddingValues(start = dimensionResource(id = R.dimen.xxl)),
						) { page ->
							Image(
								bitmap = uiState.images[page].asImageBitmap(),
								contentDescription = stringResource(id = R.string.listing_image),
								contentScale = ContentScale.Fit,
								modifier = Modifier
									.size(dimensionResource(id = R.dimen.listing_details_image))
									.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xxs))),
							)
						}

						IconButton(
							modifier = Modifier.align(Alignment.CenterStart),
							onClick = {
								coroutineScope.launch {
									pagerState.animateScrollToPage(pagerState.currentPage - 1)
								}
							},
						) {
							Icon(
								painter = painterResource(
									id = R.drawable.arrow_left_solid_black_24
								),
								contentDescription = stringResource(id = R.string.prev_arrow),
							)
						}

						IconButton(
							modifier = Modifier.align(Alignment.CenterEnd),
							onClick = {
								coroutineScope.launch {
									pagerState.animateScrollToPage(pagerState.currentPage + 1)
								}
							},
						) {
							Icon(
								painter = painterResource(
									id = R.drawable.arrow_right_solid_black_24
								),
								contentDescription = stringResource(id = R.string.next_arrow),
							)
						}
					}
					else {
						Image(
							modifier = Modifier
								.size(dimensionResource(id = R.dimen.listing_details_image))
								.clip(
									shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxs))
								),
							painter = painterResource(
								id = R.drawable.room,
							),
							contentDescription = stringResource(id = R.string.listing_image),
							contentScale = ContentScale.Fit,
						)
					}
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				Row(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center,
				) {
					Text(
						text = uiState.listingDetails.address,
						style = SubletrTypography.titleSmall,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				Row {
					PrimaryButton(
						modifier = Modifier
							.height(dimensionResource(id = R.dimen.xl)),
						onClick = { /*TODO*/ },
					) {
						Text(
							text = stringResource(id = R.string.message_sublet_owner),
							color = textOnSubletrPink
						)
					}

					Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.m)))

					SecondaryButton(
						onClick = {
							viewModel.favouritedStream.onNext(!uiState.favourited)
						},
					) {
						Text(
							text =
								if (uiState.favourited)
									stringResource(id = R.string.unfavourite)
								else
									stringResource(id = R.string.favourite),
						)
					}
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				Row(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(R.string.price),
						style = SubletrTypography.displaySmall,
					)
					Text(
						text = stringResource(
							id = R.string.dollar_sign_n,
							uiState.listingDetails.price,
						),
						color = primaryTextColor,
						style = SubletrTypography.displaySmall,
					)
				}

				Row(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(R.string.start_date),
						style = SubletrTypography.displaySmall,
					)
					Text(
						text = uiState.listingDetails.leaseStart.format(dateFormat),
						color = primaryTextColor,
						style = SubletrTypography.displaySmall,
					)
				}

				Row(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(R.string.end_date),
						style = SubletrTypography.displaySmall,
					)
					Text(
						text = uiState.listingDetails.leaseEnd.format(dateFormat),
						color = primaryTextColor,
						style = SubletrTypography.displaySmall,
					)
				}

				Row(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(R.string.rooms),
						style = SubletrTypography.displaySmall,
					)
					Text(
						text = uiState.listingDetails.rooms.toString(),
						color = primaryTextColor,
						style = SubletrTypography.displaySmall,
					)
				}

				Row(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(R.string.ensuite_bathroom),
						style = SubletrTypography.displaySmall,
					)
					Text(
						/* TODO listing details needs ensuite bathroom info */
						text = stringResource(R.string.no),
						color = primaryTextColor,
						style = SubletrTypography.displaySmall,
					)
				}

				Row(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(R.string.total_rooms_in_house),
						style = SubletrTypography.displaySmall,
					)
					Text(
						/* TODO listing details needs total rooms in house */
						text = uiState.listingDetails.rooms.toString(),
						color = primaryTextColor,
						style = SubletrTypography.displaySmall,
					)
				}

				Row(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(R.string.total_bathrooms_in_house),
						style = SubletrTypography.displaySmall,
					)
					Text(
						/* TODO listing details needs total bathrooms in house */
						text = stringResource(R.string.no),
						color = primaryTextColor,
						style = SubletrTypography.displaySmall,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.s)),
				)

				Row(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(id = R.string.description),
						color = primaryTextColor,
						style = SubletrTypography.displaySmall,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.xs)),
				)

				TextField(
					modifier = Modifier
						.wrapContentSize(align = Alignment.Center)
						.border(
							BorderStroke(
								dimensionResource(id = R.dimen.xxxs),
								textFieldBackgroundColor
							),
							shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs))
						)
						.fillMaxWidth(ELEMENT_WIDTH),
					readOnly = true,
					shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.xs)),
					colors = TextFieldDefaults.colors(
						unfocusedContainerColor = secondaryButtonBackgroundColor,
						focusedContainerColor = secondaryButtonBackgroundColor,
						unfocusedIndicatorColor = Color.Transparent,
						focusedIndicatorColor = Color.Transparent,
					),
					value = uiState.listingDetails.description,
					onValueChange = {},
				)
			}
		}
	}
}

private const val ELEMENT_WIDTH = 0.9f
/* TODO localize date format */
private val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")

@Preview(showBackground = true)
@Composable
fun ListingDetailsPagePreview() {
	SubletrTheme {
		ListingDetailsPageView(
			uiState = ListingDetailsPageUiState.Loaded(
				listingDetails = ListingDetails(
					"10 University Ave.",
					1,
					1,
					OffsetDateTime.now(),
					OffsetDateTime.now(),
					"description",
					listOf("image"),
					ResidenceType.apartment,
					1,
				),
				favourited = true,
				images = listOf(),
			),
		)
	}
}

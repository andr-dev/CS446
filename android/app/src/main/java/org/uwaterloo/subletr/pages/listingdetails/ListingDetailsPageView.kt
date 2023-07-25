package org.uwaterloo.subletr.pages.listingdetails

import android.view.MotionEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.components.rating.StaticRatingsBar
import org.uwaterloo.subletr.components.subletdetailsimage.SubletDetailsImageDisplay
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.listingdetails.ListingDetailsPageUiState.Loading.getInfoDisplay
import org.uwaterloo.subletr.pages.listingdetails.ListingDetailsPageUiState.Loading.toListOfInfo
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.SubletrTypography
import org.uwaterloo.subletr.theme.mapCircleFill
import org.uwaterloo.subletr.theme.mapCircleStroke
import org.uwaterloo.subletr.theme.subletrPalette
import java.time.OffsetDateTime

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
			val listingLatLng = LatLng(
				uiState.listingDetails.latitude.toDouble(),
				uiState.listingDetails.longitude.toDouble()
			)
			val imageCount = uiState.images.size
			val pagerState = rememberPagerState(
				initialPage = 0,
				initialPageOffsetFraction = 0f,
			) { imageCount }
			val cameraPositionState = rememberCameraPositionState {
				position = CameraPosition.fromLatLngZoom(listingLatLng, 15f)
			}
			var columnScrollingEnabled by remember { mutableStateOf(true) }
			LaunchedEffect(cameraPositionState.isMoving) {
				if (!cameraPositionState.isMoving) {
					columnScrollingEnabled = true
				}
			}
			Column(
				modifier = Modifier
					.padding(top = paddingValues.calculateTopPadding())
					.fillMaxSize(fraction = 1.0f)
					.verticalScroll(
						state = scrollState,
						enabled = columnScrollingEnabled,
					),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center,
			) {
				Box(
					modifier = Modifier
				) {
					if (uiState.isFetchingImages) {
						Box(
							modifier = modifier
								.padding(dimensionResource(id = R.dimen.xs))
								.size(dimensionResource(id = R.dimen.listing_details_image)),
							contentAlignment = Alignment.Center
						) {
							CircularProgressIndicator()
						}
					} else if (uiState.images.size == 1) {
						SubletDetailsImageDisplay(imageBitmap = uiState.images[0].asImageBitmap())
					} else if (uiState.images.isNotEmpty()) {
						Column(
							modifier = Modifier,
							verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
						) {
							HorizontalPager(
								modifier = Modifier,
								state = pagerState,
								contentPadding = PaddingValues(start = dimensionResource(id = R.dimen.xxl)),
							) { page ->
								SubletDetailsImageDisplay(imageBitmap = uiState.images[page].asImageBitmap())
							}

							Row(
								modifier = Modifier
									.height(dimensionResource(id = R.dimen.s))
									.fillMaxWidth(),
								horizontalArrangement = Arrangement.Center,
								verticalAlignment = Alignment.CenterVertically,
							) {
								repeat(imageCount) { iteration ->
									val dotColor =
										if (pagerState.currentPage == iteration)
											MaterialTheme.subletrPalette.primaryTextColor
										else MaterialTheme.subletrPalette.secondaryTextColor
									Box(
										modifier = Modifier
											.padding(dimensionResource(id = R.dimen.xxs))
											.clip(CircleShape)
											.background(dotColor)
											.size(dimensionResource(id = R.dimen.xs)),
									)
								}
							}
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
					} else {
						SubletDetailsImageDisplay()
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
						color = MaterialTheme.subletrPalette.primaryTextColor,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				Row {
					PrimaryButton(
						modifier = Modifier
							.height(dimensionResource(id = R.dimen.xl)),
						onClick = {
							viewModel.ownerIdStream.value?.let {
								viewModel.navHostController.navigate(
									"${NavigationDestination.INDIVIDUAL_CHAT_PAGE.rootNavPath}/${it}"
								)
							}
						},
					) {
						Text(
							text = stringResource(id = R.string.message_sublet_owner),
							color = MaterialTheme.subletrPalette.textOnSubletrPink
						)
					}

					Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.m)))

					SecondaryButton(
						onClick = {
							viewModel.toggleFavourite(!uiState.favourited)
						},
					) {
						Text(
							text =
							if (uiState.favourited)
								stringResource(id = R.string.unfavourite)
							else
								stringResource(id = R.string.favourite),
							color = MaterialTheme.subletrPalette.primaryTextColor,
						)
					}
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				DetailsInfoColumn(listOfInfo = uiState.listingDetails.toListOfInfo())

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
						color = MaterialTheme.subletrPalette.primaryTextColor,
						style = SubletrTypography.displayMedium,
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
								width = dimensionResource(id = R.dimen.xxxs),
								color = MaterialTheme.subletrPalette.textFieldBorderColor,
							),
							shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
						)
						.fillMaxWidth(ELEMENT_WIDTH),
					readOnly = true,
					shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.xs)),
					colors = TextFieldDefaults.colors(
						unfocusedContainerColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
						focusedContainerColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
						unfocusedIndicatorColor = Color.Transparent,
						focusedIndicatorColor = Color.Transparent,
					),
					value = uiState.listingDetails.description,
					onValueChange = {},
				)

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
						text = stringResource(id = R.string.map),
						color = MaterialTheme.subletrPalette.primaryTextColor,
						style = SubletrTypography.displayMedium,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.xs)),
				)

				GoogleMapComposable(
					cameraPositionState = cameraPositionState,
					disableColumnScrolling = { columnScrollingEnabled = false },
					position = listingLatLng,
				)

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
						text = stringResource(id = R.string.about_sublessor),
						color = MaterialTheme.subletrPalette.primaryTextColor,
						style = SubletrTypography.displayMedium,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.xs)),
				)

				OwnerCard(ownerDetails = uiState.ownerDetails, onClick = { viewModel.goToProfile() })

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)
			}
		}
	}
}

@Composable
fun DetailsInfoColumn(
	modifier: Modifier = Modifier,
	listOfInfo: ListingDetailsPageUiState.ListOfInfo,
) {
	Column(
		modifier = modifier,
	) {
		listOfInfo.getInfoDisplay().forEach {
			Row(
				modifier = modifier
					.fillMaxWidth(ELEMENT_WIDTH),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
			) {
				Text(
					text = stringResource(id = it.first),
					style = SubletrTypography.displaySmall,
				)
				Text(
					text = when (it.first) {
						R.string.price -> stringResource(id = R.string.dollar_sign_n, it.second)
						R.string.ensuite_bathroom -> stringResource(id = it.second as Int)
						else -> it.second.toString()
					},
					color = MaterialTheme.subletrPalette.primaryTextColor,
					style = SubletrTypography.displaySmall,
				)
			}
		}
	}
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GoogleMapComposable(
	modifier: Modifier = Modifier,
	cameraPositionState: CameraPositionState,
	disableColumnScrolling: () -> Unit,
	position: LatLng,
) {
	GoogleMap(
		modifier = modifier
			.fillMaxWidth(fraction = 1.0f)
			.aspectRatio(ratio = 1.0f)
			.pointerInteropFilter(
				onTouchEvent = {
					when (it.action) {
						MotionEvent.ACTION_DOWN -> {
							disableColumnScrolling()
							false
						}

						else -> true
					}
				}
			),
		cameraPositionState = cameraPositionState
	) {
		Circle(
			center = position,
			radius = MAP_CIRCLE_RADIUS,
			fillColor = mapCircleFill,
			strokeColor = mapCircleStroke,
			strokeWidth = 5f,
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerCard(ownerDetails: ListingDetailsPageUiState.OwnerDetails, onClick: () -> Unit) {
	Card(
		modifier = Modifier.fillMaxWidth(ELEMENT_WIDTH),
		shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
		elevation = CardDefaults.elevatedCardElevation(
			defaultElevation = dimensionResource(id = R.dimen.xxs),
		),
		onClick = onClick,
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.subletrPalette.squaredTextFieldBackgroundColor,
			disabledContainerColor = MaterialTheme.subletrPalette.secondaryBackgroundColor,
			contentColor = MaterialTheme.subletrPalette.primaryTextColor,
			disabledContentColor = MaterialTheme.subletrPalette.darkerGrayButtonColor,
		),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth(ELEMENT_WIDTH)
				.padding(dimensionResource(id = R.dimen.s)),
		) {
			AsyncImage(
				modifier = Modifier
					.size(dimensionResource(id = R.dimen.xxl))
					.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xxl))),
				model = ImageRequest.Builder(LocalContext.current)
					.data(ownerDetails.avatar)
					.crossfade(false)
					.build(),
				fallback = painterResource(R.drawable.default_avatar),
				contentDescription = stringResource(R.string.avatar),
				contentScale = ContentScale.Crop,
			)
			Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.s)))
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.Start,
			) {
				Text(
					ownerDetails.name,
					style = SubletrTypography.titleSmall,
					overflow = TextOverflow.Clip,
					textAlign = TextAlign.Start,
					maxLines = 1,
					color = MaterialTheme.subletrPalette.primaryTextColor,
				)
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xxs)))
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.height(dimensionResource(id = R.dimen.m)),
					horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
					verticalAlignment = Alignment.CenterVertically,
				) {
					StaticRatingsBar(
						modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.xxxs)),
						ownerDetails.rating,
					)
					Text(
						text = if (ownerDetails.rating == 0.0f) stringResource(id = R.string.no_ratings)
						else ownerDetails.rating.toString(),
						style = SubletrTypography.displaySmall,
					)
				}
				if (ownerDetails.verified) {
					Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxs)),
						verticalAlignment = Alignment.CenterVertically,
					) {
						Icon(
							painter = painterResource(
								id = R.drawable.verify_outline_black_24,
							),
							contentDescription = stringResource(id = R.string.verified_student_short),
							tint = MaterialTheme.subletrPalette.secondaryTextColor,
						)
						Text(
							text = stringResource(id = R.string.verified_student_short),
							style = SubletrTypography.displaySmall,
						)
					}
				}
			}
		}
	}
}

private const val ELEMENT_WIDTH = 0.9f
private const val MAP_CIRCLE_RADIUS = 500.0

@Preview(showBackground = true)
@Composable
fun ListingDetailsPagePreview() {
	SubletrTheme {
		ListingDetailsPageView(
			uiState = ListingDetailsPageUiState.Loaded(
				listingDetails = ListingDetails(
					"10 University Ave.",
					1f,
					1f,
					2f,
					3,
					1,
					1,
					1,
					2,
					3,
					OffsetDateTime.now(),
					OffsetDateTime.now(),
					"description",
					listOf("image"),
					ResidenceType.apartment,
					stringResource(Gender.OTHER.stringId),
					0,
				),
				favourited = true,
				images = listOf(),
				isFetchingImages = false,
				ownerDetails = ListingDetailsPageUiState.OwnerDetails(
					name = "",
					rating = 0.0f,
					verified = false,
					avatar = null,
				),
			),
		)
	}
}

package org.uwaterloo.subletr.pages.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
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
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.rating.StaticRatingsBar
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.home.list.dateTimeFormatter
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.SubletrTypography
import org.uwaterloo.subletr.theme.listingTitleFont
import org.uwaterloo.subletr.theme.subletrPalette
import org.uwaterloo.subletr.theme.userRatingLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePageView(
	modifier: Modifier = Modifier,
	viewModel: ProfilePageViewModel = hiltViewModel(),
	uiState: ProfilePageUiState = viewModel.uiStateStream.subscribeAsState(
		ProfilePageUiState.Loading
	).value,
) {
	val scrollState = rememberScrollState()

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
	) {paddingValues ->
		if (uiState is ProfilePageUiState.Loading) {
			Column(
				modifier = Modifier
					.padding(paddingValues = paddingValues)
					.fillMaxSize()
					.imePadding(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center,
			) {
				CircularProgressIndicator()
			}
		} else if (uiState is ProfilePageUiState.Loaded) {
			Column(
				modifier = Modifier
					.padding(top = paddingValues.calculateTopPadding())
					.fillMaxSize()
					.verticalScroll(scrollState)
					.padding(horizontal = dimensionResource(id = R.dimen.m)),
				verticalArrangement = Arrangement.Top,
				horizontalAlignment = Alignment.Start,
			) {
				AsyncImage(
					modifier = Modifier
						.padding(top = dimensionResource(id = R.dimen.s))
						.size(dimensionResource(id = R.dimen.xxxxxl))
						.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xxxxxl))),
					model = ImageRequest.Builder(LocalContext.current)
						.data(uiState.avatar)
						.crossfade(false)
						.build(),
					fallback = painterResource(R.drawable.default_avatar),
					contentDescription = stringResource(R.string.avatar),
					contentScale = ContentScale.Crop,
				)

				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

				Text(
					text = uiState.name,
					style = SubletrTypography.titleMedium
				)

				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.height(dimensionResource(id = R.dimen.m)),
					horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
					verticalAlignment = Alignment.CenterVertically,
				) {
					StaticRatingsBar(
						modifier = Modifier.padding(dimensionResource(id = R.dimen.xxxs)),
						uiState.rating,
					)
					Text(
						text = if (uiState.rating == 0.0f) stringResource(id = R.string.no_ratings)
						else uiState.rating.toString(),
						style = userRatingLabel,
					)
				}

				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

				if (uiState.verification) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxs)),
						verticalAlignment = Alignment.CenterVertically,
					) {
						Icon(
							painter = painterResource(
								id = R.drawable.verify_outline_black_24,
							),
							contentDescription = stringResource(id = R.string.verified_student),
							tint =  MaterialTheme.subletrPalette.secondaryTextColor,
						)
						Text(
							text = stringResource(id = R.string.verified_student),
							style = SubletrTypography.displaySmall,
						)
					}
					Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
				}

				Divider(
					modifier = Modifier.fillMaxWidth(),
					thickness = dimensionResource(id = R.dimen.xxxxs),
					color =  MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
				)

				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

				Text(
					text = stringResource(id = R.string.listing),
					color =  MaterialTheme.subletrPalette.primaryTextColor,
					style = SubletrTypography.displayLarge,
				)

				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

				if (uiState.listingDetails == null) {
					Text(
						text = "${uiState.name} " + stringResource(id = R.string.no_active_listing),
						color =  MaterialTheme.subletrPalette.secondaryTextColor,
					)
				} else {
					Card(
						modifier = Modifier.fillMaxWidth(),
						shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
						elevation = CardDefaults.elevatedCardElevation(
							defaultElevation = dimensionResource(id = R.dimen.xxs),
						),
						colors = CardDefaults.cardColors(
							containerColor =  MaterialTheme.subletrPalette.squaredTextFieldBackgroundColor,
							disabledContainerColor =  MaterialTheme.subletrPalette.secondaryBackgroundColor,
							contentColor =  MaterialTheme.subletrPalette.primaryTextColor,
							disabledContentColor =  MaterialTheme.subletrPalette.darkerGrayButtonColor,
						),
						onClick = {
							viewModel.navHostController.navigate(
								route = "${NavigationDestination.LISTING_DETAILS.rootNavPath}/${uiState.listingId}"
							)
						},
					) {
						Column(
							modifier = Modifier
								.fillMaxWidth()
								.padding(
									start = dimensionResource(id = R.dimen.s),
									top = dimensionResource(id = R.dimen.s),
									bottom = dimensionResource(id = R.dimen.s),
									end = dimensionResource(id = R.dimen.xs),
								),
							verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
						) {
							Row(
								modifier = Modifier
									.fillMaxWidth(1.0f),
							) {
								if (uiState.listingImage != null) {
									Image(
										modifier = Modifier
											.height(dimensionResource(id = R.dimen.xxxl))
											.width(dimensionResource(id = R.dimen.xxxl)),
										bitmap = uiState.listingImage.asImageBitmap(),
										contentDescription = stringResource(id = R.string.listing_image),
										contentScale = ContentScale.Crop,
									)
								} else {
									Image(
										modifier = Modifier
											.height(dimensionResource(id = R.dimen.xxxl))
											.width(dimensionResource(id = R.dimen.xxxl)),
										painter = painterResource(
											id = R.drawable.default_listing_image,
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
									verticalArrangement = Arrangement.spacedBy(
										dimensionResource(id = R.dimen.xxxs)
									),

									) {
									Text(
										uiState.listingDetails.address,
										style = listingTitleFont,
										overflow = TextOverflow.Clip,
										textAlign = TextAlign.Start,
										maxLines = 1,
										color =  MaterialTheme.subletrPalette.primaryTextColor,
									)
									Text(
										stringResource(
											id = R.string.dollar_sign_n,
											uiState.listingDetails.price,
										),
										style = listingTitleFont,
										color =  MaterialTheme.subletrPalette.primaryTextColor,
									)
									Text(
										text = stringResource(
											id = R.string.short_for_all_date_range,
											dateTimeFormatter(uiState.listingDetails.leaseStart),
											dateTimeFormatter(offsetDateTime = uiState.listingDetails.leaseEnd),
										),
										style = MaterialTheme.typography.bodyLarge,
										color =  MaterialTheme.subletrPalette.primaryTextColor,
									)
								}
							}
						}
					}
				}

				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))

				if (viewModel.allowUserToReview()) {
					Text(
						text = stringResource(id = R.string.leave_a_review),
						color =  MaterialTheme.subletrPalette.primaryTextColor,
						style = SubletrTypography.displayLarge,
					)

					Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

					DynamicRatingBar(modifier = Modifier, rating = uiState.usersRating, viewModel)

					Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))

					PrimaryButton(
						modifier = Modifier
							.fillMaxWidth()
							.height(dimensionResource(id = R.dimen.xl)),
						onClick = {
							viewModel.ratingUpdateStream.onNext(uiState.usersRating)
						},
						enabled = uiState.usersRating != 0
					) {
						Text(
							text = stringResource(id = R.string.submit_rating),
							color =  MaterialTheme.subletrPalette.textOnSubletrPink,
						)
					}

					Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
				}
			}
		}
	}
}

@Composable
fun DynamicRatingBar(
	modifier: Modifier,
	rating: Int,
	viewModel: ProfilePageViewModel,
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxxs))
	) {
		(1..5).toList().forEach {
			IconButton(
				modifier = Modifier.size(dimensionResource(id = R.dimen.xl)),
				onClick = {
				  	viewModel.usersRatingStream.onNext(it)
				},
			) {
				Icon(
					modifier = Modifier.size(dimensionResource(id = R.dimen.xl)),
					painter = painterResource(
						id = if (rating >= it) R.drawable.star_solid_pink_26
						else R.drawable.star_outline_black_26
					),
					tint = if (rating >= it)  MaterialTheme.subletrPalette.subletrPink
					else  MaterialTheme.subletrPalette.textFieldBorderColor,
					contentDescription = stringResource(id = R.string.leave_a_review),
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun ProfilePageViewPreview() {
	ProfilePageView()
}

@Preview(showBackground = true)
@Composable
fun ProfilePageLoadedPreview() {
	SubletrTheme {
		ProfilePageView(
			uiState = ProfilePageUiState.Loaded(
				name = "",
				listingId = null,
				listingDetails = null,
				listingImage = null,
				avatar = null,
				rating = 0.0f,
				verification = false,
				usersRating = 0,
			)
		)
	}
}

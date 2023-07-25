package org.uwaterloo.subletr.pages.home.map.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.home.list.dateTimeFormatter
import org.uwaterloo.subletr.pages.home.map.HomeMapChildViewModel
import org.uwaterloo.subletr.theme.listingTitleFont
import org.uwaterloo.subletr.theme.subletrPalette
import kotlin.math.roundToInt

@Composable
fun HomeMapListingItemView(
	modifier: Modifier = Modifier,
	listingSummary: ListingSummary,
	listingImage: Bitmap?,
	selected: Boolean,
	timeToDestination: Float?,
	viewModel: HomeMapChildViewModel,
	setSelected: (Boolean) -> Unit,
) {
	Button(
		modifier = modifier
			.clip(RoundedCornerShape(size = dimensionResource(id = R.dimen.xxs)))
			.padding(all = dimensionResource(id = R.dimen.xs))
			.border(
				width = dimensionResource(id = R.dimen.xxxs),
				shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.xxs)),
				color = if (selected) MaterialTheme.subletrPalette.subletrPink else Color.Transparent,
			),
		onClick = {
		  	setSelected(!selected)
		},
		colors = ButtonDefaults.buttonColors(
			containerColor =
			if (selected) MaterialTheme.subletrPalette.primaryBackgroundColor
			else MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
			contentColor = MaterialTheme.subletrPalette.primaryTextColor,
		),
		shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.xxs)),
		contentPadding = PaddingValues(all = dimensionResource(id = R.dimen.zero))
	) {
		Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.s)))
		Column(
			modifier = Modifier
				.wrapContentHeight(),
		) {
			Row(
				modifier = Modifier
					.padding(top = dimensionResource(id = R.dimen.xxs)),
				verticalAlignment = Alignment.CenterVertically,
			) {
				if (listingImage != null) {
					Image(
						modifier = Modifier
							.height(dimensionResource(id = R.dimen.xxxl))
							.width(dimensionResource(id = R.dimen.xxxl))
							.clip(
								shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.xxs)),
							),
						bitmap = listingImage.asImageBitmap(),
						contentDescription = stringResource(id = R.string.listing_image),
						contentScale = ContentScale.Crop,
					)
				}
				else {
					Image(
						modifier = Modifier
							.height(dimensionResource(id = R.dimen.xxxl))
							.width(dimensionResource(id = R.dimen.xxxl))
							.clip(
								shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.xxs)),
							)
							.border(
								width = dimensionResource(id = R.dimen.xxxxs),
								color = MaterialTheme.subletrPalette.textFieldBorderColor,
								shape = RoundedCornerShape(
									size = dimensionResource(id = R.dimen.xxs),
								),
							),
						painter = painterResource(
							id = R.drawable.default_listing_image,
						),
						contentDescription = stringResource(id = R.string.listing_image),
						contentScale = ContentScale.Crop,
					)
				}
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)))
				Column(
					modifier = Modifier,
					horizontalAlignment = Alignment.Start,
					verticalArrangement = Arrangement.Center,
				) {
					Text(
						text = listingSummary.address,
						style = listingTitleFont,
						overflow = TextOverflow.Clip,
						textAlign = TextAlign.Start,
						maxLines = 1,
					)
					Text(
						stringResource(
							id = R.string.dollar_sign_n,
							listingSummary.price,
						),
						style = listingTitleFont,
					)
					Row(
						verticalAlignment = Alignment.CenterVertically,
					) {
						Icon(
							painter = painterResource(
								id = R.drawable.access_time_solid_gray_16,
							),
							contentDescription = stringResource(id = R.string.time),
							tint = MaterialTheme.subletrPalette.primaryTextColor,
						)
						Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xxs)))
						Text(
							text = if (timeToDestination != null) {
								stringResource(
									id = R.string.n_minutes,
									timeToDestination.roundToInt(),
								)
							} else {
								stringResource(
									id = R.string.time_not_available,
								)
						    },
							style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
							color = MaterialTheme.subletrPalette.primaryTextColor,
						)
					}
					Text(
						text = stringResource(
							id = R.string.short_for_all_date_range,
							dateTimeFormatter(
								offsetDateTime = listingSummary.leaseStart,
							),
							dateTimeFormatter(
								offsetDateTime = listingSummary.leaseEnd,
							),
						),
						style = MaterialTheme.typography.bodyLarge,
						color = MaterialTheme.subletrPalette.primaryTextColor,
					)
				}
			}
			Row(
				modifier = Modifier
					.height(height = dimensionResource(id = R.dimen.xl))
					.fillMaxWidth(fraction = 1.0f)
					.padding(
						bottom = dimensionResource(id = R.dimen.xs),
					),
				horizontalArrangement = Arrangement.SpaceEvenly,
			) {
				SecondaryButton(
					modifier = Modifier
						.fillMaxWidth(fraction = 0.5f)
						.fillMaxHeight(fraction = 1.0f),
					colors = ButtonDefaults.buttonColors(
						containerColor =
						if (selected) MaterialTheme.subletrPalette.primaryBackgroundColor
						else MaterialTheme.subletrPalette.darkerGrayButtonColor,
						contentColor = MaterialTheme.subletrPalette.primaryTextColor,
					),
					border = BorderStroke(
						width = dimensionResource(id = R.dimen.xxxs),
						color = if (selected) MaterialTheme.subletrPalette.primaryTextColor else Color.Transparent,
					),
					onClick = {
						viewModel.navHostController.navigate(
							route = "${NavigationDestination.LISTING_DETAILS.rootNavPath}/${listingSummary.listingId}"
						)
					},
				) {
					Text(
						stringResource(id = R.string.view_details),
						style = MaterialTheme.typography.bodyLarge,
						color = MaterialTheme.subletrPalette.primaryTextColor,
					)
				}
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)))
				Box(
					modifier = Modifier
						.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xl)))
						.border(
							width = dimensionResource(id = R.dimen.xxxs),
							shape = RoundedCornerShape(dimensionResource(id = R.dimen.xl)),
							color = if (selected) MaterialTheme.subletrPalette.primaryTextColor else Color.Transparent,
						)
						.weight(weight = 0.5f),
				) {
					IconButton(
						modifier = Modifier
							.background(
								color =
								if (selected) MaterialTheme.subletrPalette.primaryBackgroundColor
								else MaterialTheme.subletrPalette.darkerGrayButtonColor,
							)
							.fillMaxWidth(fraction = 1.0f),
						onClick = {
					    	viewModel.navigateToChatStream.onNext(listingSummary.listingId)
						},
					) {
						Icon(
							painter = painterResource(id = R.drawable.chat_bubble_outline_gray_24),
							contentDescription = stringResource(id = R.string.chat_icon),
						)
					}
				}
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)))
				Box(
					modifier = Modifier
						.weight(weight = 0.5f)
						.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xl)))
						.border(
							width = dimensionResource(id = R.dimen.xxxs),
							shape = RoundedCornerShape(dimensionResource(id = R.dimen.xl)),
							color = if (selected) MaterialTheme.subletrPalette.primaryTextColor else Color.Transparent,
						),
				) {
					IconButton(
						modifier = Modifier
							.background(
								color =
								if (selected) MaterialTheme.subletrPalette.primaryBackgroundColor
								else MaterialTheme.subletrPalette.darkerGrayButtonColor,
							)
							.fillMaxWidth(fraction = 1.0f),
						onClick = {
							  /*TODO*/
						},
					) {
						Icon(
							painter = painterResource(id = R.drawable.star_outline_black_26),
							contentDescription = stringResource(id = R.string.chat_icon),
						)
					}
				}
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)))
			}
		}
	}
}

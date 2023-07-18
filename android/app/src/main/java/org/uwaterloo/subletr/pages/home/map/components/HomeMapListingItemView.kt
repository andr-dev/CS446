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
import androidx.compose.foundation.shape.CircleShape
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
import org.uwaterloo.subletr.theme.darkerGrayButtonColor
import org.uwaterloo.subletr.theme.listingTitleFont
import org.uwaterloo.subletr.theme.primaryBackgroundColor
import org.uwaterloo.subletr.theme.primaryTextColor
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.subletrPink
import kotlin.math.roundToInt

@Composable
fun HomeMapListingItemView(
	modifier: Modifier = Modifier,
	listingSummary: ListingSummary,
	listingImage: Bitmap?,
	selected: Boolean,
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
				color = if (selected) subletrPink else Color.Transparent,
			),
		onClick = {
		  	setSelected(!selected)
		},
		colors = ButtonDefaults.buttonColors(
			containerColor = if (selected) primaryBackgroundColor else secondaryButtonBackgroundColor,
			contentColor = primaryTextColor,
		),
		shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.xxs)),
		contentPadding = PaddingValues(all = dimensionResource(id = R.dimen.zero))
	) {
		Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.s)))
		Column(
			modifier = Modifier
				.fillMaxWidth(fraction = 1.0f)
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
							),
						painter = painterResource(
							id = R.drawable.room,
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
							tint = primaryTextColor,
						)
						Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xxs)))
						Text(
							text = stringResource(
								id = R.string.n_minutes,
								// TODO: Fix variable when listingSummary is fixed
								listingSummary.distanceMeters.roundToInt(),
							),
							style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
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
					)
				}
			}
			Row(
				modifier = Modifier
					.wrapContentHeight()
					.fillMaxWidth(fraction = 1.0f),
				horizontalArrangement = Arrangement.SpaceEvenly,
			) {
				SecondaryButton(
					modifier = Modifier
						.fillMaxWidth(0.5f)
						.fillMaxHeight(1.0f),
					colors = ButtonDefaults.buttonColors(
						containerColor = if (selected) primaryBackgroundColor else darkerGrayButtonColor,
						contentColor = Color.Black,
					),
					border = BorderStroke(
						width = dimensionResource(id = R.dimen.xxxs),
						color = if (selected) primaryTextColor else Color.Transparent,
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
					)
				}
				Box(
					modifier = Modifier
						.clip(CircleShape)
						.border(
							width = dimensionResource(id = R.dimen.xxxs),
							shape = CircleShape,
							color = if (selected) primaryTextColor else Color.Transparent,
						),
				) {
					IconButton(
						modifier = Modifier
							.background(
								color = if (selected) primaryBackgroundColor else darkerGrayButtonColor,
							),
						onClick = { /*TODO*/ },
					) {
						Icon(
							painter = painterResource(id = R.drawable.chat_bubble_outline_gray_24),
							contentDescription = stringResource(id = R.string.chat_icon),
						)
					}
				}
				Box(
					modifier = Modifier
						.clip(CircleShape)
						.border(
							width = dimensionResource(id = R.dimen.xxxs),
							shape = CircleShape,
							color = if (selected) primaryTextColor else Color.Transparent,
						),
				) {
					IconButton(
						modifier = Modifier
							.background(
								color = if (selected) primaryBackgroundColor else darkerGrayButtonColor,
							),
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
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.s)))
			}
			Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)))
		}
	}
}

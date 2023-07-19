package org.uwaterloo.subletr.pages.home.list.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.pages.home.list.ButtonWithIcon
import org.uwaterloo.subletr.pages.home.list.dateTimeFormatter
import org.uwaterloo.subletr.theme.darkerGrayButtonColor
import org.uwaterloo.subletr.theme.listingDescriptionFont
import org.uwaterloo.subletr.theme.listingTitleFont
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.secondaryTextColor

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
				),
			)
			.background(secondaryButtonBackgroundColor)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth(1.0f)
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
							id = R.drawable.default_listing_image,
						),
						contentDescription = stringResource(id = R.string.listing_image),
						contentScale = ContentScale.Crop,
					)
				}
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.s)),)
				Column(
					modifier = Modifier
						.fillMaxWidth(1.0f),
					horizontalAlignment = Alignment.Start,
					verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxxs)),

				) {
					Text(
						listingSummary.address,
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
					Text(
						text = stringResource(
							id = R.string.short_for_all_date_range,
							dateTimeFormatter(listingSummary.leaseStart),
							dateTimeFormatter(offsetDateTime = listingSummary.leaseEnd),
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
					tint = secondaryTextColor,
				)
				Text(
					pluralStringResource(
						id = R.plurals.n_bedrooms,
						count = listingSummary.roomsAvailable,
						listingSummary.roomsAvailable,
					),
					style = listingDescriptionFont,
				)
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)),)
				Icon(
					painter = painterResource(
						id =
						when (listingSummary.residenceType) {
							ResidenceType.apartment -> R.drawable.apartment_solid_gray_16
							ResidenceType.other -> R.drawable.other_houses_solid_gray_16
							else -> R.drawable.home_outline_gray_16
						},

					),
					contentDescription = stringResource(R.string.home_icon),
					tint = secondaryTextColor,
				)
				Text(
					text = listingSummary.residenceType.value,
					style = listingDescriptionFont,
				)
			}
			Row(
				modifier = Modifier
					.height(40.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
			) {
				SecondaryButton(
					modifier = Modifier
						.fillMaxWidth(0.5f)
						.fillMaxHeight(1.0f),
					colors = ButtonDefaults.buttonColors(
						containerColor = darkerGrayButtonColor,
						contentColor = Color.Black,
					),
					onClick = detailsNavigation,
				) {

					Text(
						stringResource(id = R.string.view_details),
						style = MaterialTheme.typography.bodyLarge,
					)

				}
				ButtonWithIcon(
					modifier = Modifier
						.fillMaxHeight(1.0f)
						.width(dimensionResource(id = R.dimen.xxxl)),
					colors = ButtonDefaults.buttonColors(
						containerColor = darkerGrayButtonColor,
						contentColor = Color.Black,
					),
					iconId = R.drawable.chat_bubble_outline_gray_24,
					onClick = { /*TODO*/ },
					contentDescription = stringResource(id = R.string.chat_icon),
				)
				ButtonWithIcon(
					modifier = Modifier
						.fillMaxHeight(1.0f)
						.width(dimensionResource(id = R.dimen.xxxl)),
					colors = ButtonDefaults.buttonColors(
						containerColor = darkerGrayButtonColor,
						contentColor = Color.Black,
					),
					iconId = R.drawable.star_outline_black_26,
					onClick = { /*TODO*/ },
					contentDescription = stringResource(id = R.string.chat_icon),
				)
			}
		}
	}
}

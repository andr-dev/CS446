package org.uwaterloo.subletr.pages.individualchat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.pages.individualchat.IndividualChatPageUiState
import org.uwaterloo.subletr.theme.avatarTextFont
import org.uwaterloo.subletr.theme.headerPrimaryTitle
import org.uwaterloo.subletr.theme.headerSecondaryTitle
import org.uwaterloo.subletr.theme.primaryBackgroundColor
import org.uwaterloo.subletr.theme.secondaryBackgroundColor

@Composable
fun IndividualChatHeader(
	modifier: Modifier,
	basicInfo: IndividualChatPageUiState.BasicInfo,
	navHostController: NavHostController,
) {
	Box(
		modifier = modifier
			.wrapContentSize()
			.clip(
				RoundedCornerShape(
					topStart = dimensionResource(id = R.dimen.zero),
					topEnd = dimensionResource(id = R.dimen.zero),
					bottomEnd = dimensionResource(id = R.dimen.s),
					bottomStart = dimensionResource(id = R.dimen.s),
				)
			)
	) {
		Row(
			modifier = Modifier
				.background(
					color = secondaryBackgroundColor
				)
				.fillMaxWidth(fraction = 1.0f),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Start,
		) {
			IconButton(
				modifier = Modifier.padding(
					vertical = dimensionResource(id = R.dimen.m),
					horizontal = dimensionResource(id = R.dimen.s),
				),
				onClick = {
					navHostController.popBackStack()
				},
			) {
				Icon(
					painter = painterResource(
						id = R.drawable.arrow_back_solid_black_24,
					),
					contentDescription = stringResource(id = R.string.back_arrow)
				)
			}
			Box(
				modifier = Modifier
					.wrapContentSize()
					.clip(CircleShape),
				contentAlignment = Alignment.Center,
			) {
				Box(
					modifier = Modifier
						.width(dimensionResource(id = R.dimen.xl))
						.height(dimensionResource(id = R.dimen.xl))
						.background(color = primaryBackgroundColor),
					contentAlignment = Alignment.Center,
				) {
					Text(
						text = basicInfo.contactName.first().uppercase(),
						textAlign = TextAlign.Center,
						style = avatarTextFont,
					)
				}
			}
			Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)))
			Column(
				modifier = Modifier,
			) {
				Text(
					text = basicInfo.contactName,
					style = headerPrimaryTitle,
				)
				Text(
					text = basicInfo.address,
					style = headerSecondaryTitle,
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun IndividualChatHeaderPreview() {
	IndividualChatHeader(
		modifier = Modifier,
		basicInfo = IndividualChatPageUiState.BasicInfo(
			contactName = "Abhed Shwarma",
			address = "123 University Ave.",
		),
		navHostController = rememberNavController(),
	)
}

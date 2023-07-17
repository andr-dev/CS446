package org.uwaterloo.subletr.pages.watcardverification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.subletrPink

private const val ELEMENT_WIDTH = 0.75f
@Composable
fun WatcardVerificationPageView(
	modifier : Modifier = Modifier,
	viewModel : WatcardVerificationPageViewModel = hiltViewModel(),
	uiState: WatcardVerificationUiState = viewModel.uiStateStream.subscribeAsState(
		WatcardVerificationUiState.Loading
	).value
) {
	if (uiState is WatcardVerificationUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f)
				.imePadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			CircularProgressIndicator()
		}
	} else if (uiState is WatcardVerificationUiState.Loaded) {
		Column(
			modifier = modifier.fillMaxSize(1.0f),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Spacer(modifier = modifier.weight(8.0f))
			
			Text(
				text = stringResource(id = R.string.watcard_verification),
				style = MaterialTheme.typography.titleMedium,
			)

			Box(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.l))
					.fillMaxWidth()
					.height(dimensionResource(id = R.dimen.xxxxxxxxxl))
					.border(
						width = dimensionResource(id = R.dimen.xxxs),
						color = Color.Black,
						shape = RoundedCornerShape(dimensionResource(id = R.dimen.zero)),
					)
					.background(Color.Transparent),
			contentAlignment = Alignment.Center
			) {
				if (uiState.watcard == null) {
					Text(stringResource(id = R.string.no_watcard_uploaded))
				}
			}

			Button(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH)
					.height(dimensionResource(id = R.dimen.xl)),
				onClick = {},
				colors = ButtonDefaults.buttonColors(
					containerColor = subletrPink,
					contentColor = Color.White,
				)
			) {
				Text(
					stringResource(id = R.string.upload_image)
				)
			}

			Spacer(modifier = modifier.weight(0.5f))

			Button(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH)
					.height(dimensionResource(id = R.dimen.xl)),
				onClick = {},
				colors = ButtonDefaults.buttonColors(
					containerColor = subletrPink,
					contentColor = Color.White,
				)
			) {
				Text(
					stringResource(id = R.string.done)
				)
			}

			Spacer(modifier = modifier.weight(0.5f))

			Button(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH)
					.height(dimensionResource(id = R.dimen.xl)),
				onClick = {},
				colors = ButtonDefaults.buttonColors(
					containerColor = secondaryButtonBackgroundColor,
					contentColor = Color.White,
				)
			) {
				Text(
					stringResource(id = R.string.cancel)
				)
			}
			Spacer(modifier = modifier.weight(8.0f))
		}
	}
}

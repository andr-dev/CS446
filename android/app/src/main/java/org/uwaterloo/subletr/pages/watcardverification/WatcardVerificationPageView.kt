package org.uwaterloo.subletr.pages.watcardverification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.darkerGrayButtonColor
import org.uwaterloo.subletr.theme.lightGrayButtonColor
import org.uwaterloo.subletr.theme.primaryTextColor
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrBlack
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.textOnGray
import org.uwaterloo.subletr.theme.textOnSubletrPink

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
			Spacer(modifier = modifier.weight(6.0f))
			
			Text(
				text = stringResource(id = R.string.watcard_verification),
				style = MaterialTheme.typography.titleMedium,
				color = primaryTextColor
			)

			Column(modifier = modifier
				.fillMaxWidth(1.0f)
				.padding(horizontal = dimensionResource(id = R.dimen.l))
			) {
				Text(
					text = stringResource(id = R.string.watcard_verification_txt1),
					color = secondaryTextColor,
				)
				Text(
					text = stringResource(id = R.string.watcard_verification_txt2),
					color = secondaryTextColor,
				)
			}

			Spacer(modifier = modifier.weight(2.0f))

			SetContent(
				viewModel = viewModel,
				watcard = uiState.watcard
			)

			Spacer(modifier = modifier.weight(8.0f))

			SetSubmitButton(
				viewModel,
				uiState.watcard,
				uiState.submitted,
				uiState.verified,
			)

			Spacer(modifier = modifier.weight(3.0f))

			SetCancelButton(
				uiState.watcard,
				uiState.submitted,
				uiState.verified,
			)

			Spacer(modifier = modifier.weight(8.0f))
		}
	}
}

@Composable
fun SetContent(viewModel: WatcardVerificationPageViewModel, watcard : String?) {
	Box(
		modifier = Modifier
			.padding(dimensionResource(id = R.dimen.l))
			.fillMaxWidth()
			.height(dimensionResource(id = R.dimen.watcard_image_height))
			.border(
				width = dimensionResource(id = R.dimen.xxxs),
				color = subletrBlack,
				shape = RectangleShape,
			)
			.background(Color.Transparent)
			.clickable {
				viewModel.updateUiState(
					WatcardVerificationUiState.Loaded(
						watcard = "uploaded",
						submitted = false,
						verified = false,
					)
				)
			},
		contentAlignment = Alignment.Center,
	) {
		if (watcard == null) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center,
			) {
				Icon(
					modifier = Modifier.size(dimensionResource(id = R.dimen.xl)),
					painter = painterResource(
						id = R.drawable.add_filled_black_32
					),
					contentDescription = stringResource(id = R.string.add_sign),
				)
				Text(
					stringResource(id = R.string.add_watcard),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		} else {
			// TODO: show picture
		}
	}
}

@Composable
fun SetSubmitButton(
	viewModel: WatcardVerificationPageViewModel,
	watcard: String?,
	submitted: Boolean,
	verified : Boolean
) {
	var active by remember { mutableStateOf<Boolean>(true) }

	PrimaryButton(
		modifier = Modifier
			.fillMaxWidth(ELEMENT_WIDTH)
			.height(dimensionResource(id = R.dimen.xl)),
		enabled = active,

		onClick = { // TODO: verify
			if (watcard != null) {
				viewModel.updateUiState(
					WatcardVerificationUiState.Loaded(
						watcard = watcard,
						submitted = true,
						verified = false,
					)
				)

			}
		},
		colors = ButtonDefaults.buttonColors(
			containerColor = subletrPink,
			contentColor = textOnSubletrPink,
			disabledContainerColor = lightGrayButtonColor,
		)
	) {
		if (!submitted) {
			active = true
			Text(
				stringResource(id = R.string.submit)
			)
		} else if (submitted && !verified){
			active = false
			Text(
				stringResource(id = R.string.image_uploaded)
			)
		} else {
			active = false
			Text(
				stringResource(id = R.string.image_verified)
			)
		}
	}
}


@Composable
fun SetCancelButton(
	watcard: String?,
	submitted : Boolean,
	verified : Boolean,
) {
	var active by remember { mutableStateOf<Boolean>(true) }

	var color by remember { mutableStateOf<Color>(Color.Gray) }
	color = darkerGrayButtonColor


	val buttonAction = remember { mutableStateOf<()->Unit>(
		fun(){
			// TODO: implement onClick
		}
	) }

	SecondaryButton(
		modifier = Modifier
			.fillMaxWidth(ELEMENT_WIDTH)
			.height(dimensionResource(id = R.dimen.xl)),
		onClick = { buttonAction },
		colors = ButtonDefaults.buttonColors(
			containerColor = color,
			contentColor = textOnGray,
			disabledContainerColor = lightGrayButtonColor
		),
		enabled = active,
	) {
		if (watcard == null || !submitted) {
			active = true
			color = darkerGrayButtonColor
			Text(
				stringResource(id = R.string.cancel)
			)
		} else if (watcard != null && !verified && submitted) {
			active = false
			Text(
				stringResource(id = R.string.pending_verification)
			)
		} else if (watcard != null && verified){
			active = true
			color = subletrPink
			Text(
				stringResource(id = R.string.done)
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
fun WatcardVerificationPageViewLoadingPreview() {
	WatcardVerificationPageView()
}

@Preview(showBackground = true)
@Composable
fun WatcardVerificationPageViewLoadedPreview() {
	SubletrTheme() {
		WatcardVerificationPageView(
			uiState = WatcardVerificationUiState.Loaded(
				watcard = null,
				submitted = false,
				verified = false
			),
		)
	}
}


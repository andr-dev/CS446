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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.subletrPalette

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
			modifier = modifier.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Spacer(modifier = Modifier.weight(4.0f))
			
			Text(
				text = stringResource(id = R.string.watcard_verification),
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.subletrPalette.primaryTextColor
			)

			Column(modifier = Modifier
				.fillMaxWidth(1.0f)
				.padding(horizontal = dimensionResource(id = R.dimen.l))
			) {
				Text(
					text = stringResource(id = R.string.watcard_verification_txt),
					color = MaterialTheme.subletrPalette.secondaryTextColor,
				)
			}

			Spacer(modifier = Modifier.weight(4.0f))

			SetContent(
				viewModel = viewModel,
				watcard = uiState.watcard
			)

			Spacer(modifier = Modifier.weight(8.0f))

			SetSubmitButton(
				viewModel,
				uiState.watcard,
				uiState.submitted,
				uiState.verified,
			)

			Spacer(modifier = Modifier.weight(3.0f))

			SetCancelButton(
				uiState.watcard,
				uiState.submitted,
				uiState.verified,
			)

			Spacer(modifier = Modifier.weight(8.0f))
		}
	}
}

@Composable
fun SetContent(viewModel: WatcardVerificationPageViewModel, watcard : String?) {
	Box(
		modifier = Modifier
			.padding(dimensionResource(id = R.dimen.s))
			.fillMaxWidth()
			.height(dimensionResource(id = R.dimen.watcard_image_height))
			.clip(RoundedCornerShape(dimensionResource(id = R.dimen.s)))
			.background(MaterialTheme.subletrPalette.darkerGrayButtonColor)
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
					style = MaterialTheme.typography.titleSmall,
					color = MaterialTheme.subletrPalette.subletrBlack
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
			containerColor = MaterialTheme.subletrPalette.subletrPink,
			contentColor = MaterialTheme.subletrPalette.textOnSubletrPink,
			disabledContainerColor = MaterialTheme.subletrPalette.lightGrayButtonColor,
		)
	) {
		if (!submitted) {
			active = true
			Text(
				stringResource(id = R.string.submit),
				color = MaterialTheme.subletrPalette.textOnSubletrPink
			)
		} else if (submitted && !verified){
			active = false
			Icon(
				painter = painterResource(
					id = R.drawable.baseline_check_24
				),
				contentDescription = stringResource(id = R.string.checkmark_sign),
			)
			Text(
				modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.xs)),
				text = stringResource(id = R.string.image_uploaded)
			)
		} else {
			active = false
			Icon(
				painter = painterResource(
					id = R.drawable.baseline_check_24
				),
				contentDescription = stringResource(id = R.string.checkmark_sign),
			)
			Text(
				modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.xs)),
				text = stringResource(id = R.string.image_verified)
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
	color = MaterialTheme.subletrPalette.darkerGrayButtonColor

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
			contentColor = MaterialTheme.subletrPalette.textOnGray,
			disabledContainerColor = MaterialTheme.subletrPalette.lightGrayButtonColor
		),
		enabled = active,
	) {
		if (watcard == null || !submitted) {
			active = true
			color = MaterialTheme.subletrPalette.darkerGrayButtonColor
			Text(
				stringResource(id = R.string.cancel),
				color = MaterialTheme.subletrPalette.textOnGray
			)
		} else if (watcard != null && !verified && submitted) {
			active = false
			Text(
				stringResource(id = R.string.pending_verification),
				color = MaterialTheme.subletrPalette.textOnGray
			)
		} else if (watcard != null && verified){
			active = true
			color = MaterialTheme.subletrPalette.subletrPink
			Text(
				stringResource(id = R.string.done),
				color = MaterialTheme.subletrPalette.textOnSubletrPink
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


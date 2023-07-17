package org.uwaterloo.subletr.pages.watcardverification

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.darkerGrayButtonColor
import org.uwaterloo.subletr.theme.lightGrayButtonColor
import org.uwaterloo.subletr.theme.secondaryTextColor
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

			setContent(
				viewModel = viewModel,
				watcard = uiState.watcard
			)

			SetUploadButton(
				viewModel,
				uiState.watcard
			)

			Spacer(modifier = modifier.weight(0.5f))

			setCancelButton(
				uiState.watcard,
				uiState.verified,
			)

			Spacer(modifier = modifier.weight(8.0f))
		}
	}
}

@Composable
fun setContent(viewModel: WatcardVerificationPageViewModel, watcard : String?) {
	Box(
		modifier = Modifier
			.padding(dimensionResource(id = R.dimen.l))
			.fillMaxWidth()
			.height(dimensionResource(id = R.dimen.xxxxxxxxxl))
			.border(
				width = dimensionResource(id = R.dimen.xxxs),
				color = Color.Gray,
				shape = RoundedCornerShape(dimensionResource(id = R.dimen.zero)),
			)
			.background(Color.Transparent)
			.clickable {
				viewModel.updateUiState(
					WatcardVerificationUiState.Loaded(
						watcard = "uploaded",
						verified = false
					)
				)},
		contentAlignment = Alignment.Center,
	) {
		if (watcard == null) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center,
			) {
				Text(
					stringResource(id = R.string.plus),
					style = MaterialTheme.typography.titleSmall,
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
fun SetUploadButton(
	viewModel: WatcardVerificationPageViewModel,
	watcard: String?,
) {
	Button(
		modifier = Modifier
			.fillMaxWidth(ELEMENT_WIDTH)
			.height(dimensionResource(id = R.dimen.xl)),
		onClick = { // TODO: verify
			viewModel.updateUiState(
				WatcardVerificationUiState.Loaded(
					watcard = watcard,
					verified = true
				)
			)
		},
		colors = ButtonDefaults.buttonColors(
			containerColor = subletrPink,
			contentColor = Color.White,
		)
	) {
		Text(
			stringResource(id = R.string.submit)
		)
	}
}


@Composable
fun setCancelButton(
	watcard: String?,
	verified : Boolean,
) {
	val active = remember { mutableStateOf<Boolean>(true) }

	val color = remember { mutableStateOf<Color>(Color.Gray) }
	color.value = darkerGrayButtonColor

	val buttonAction = remember { mutableStateOf<()->Unit>(
		fun(){
			// TODO: implement onClick
		}
	) }

	Button(
		modifier = Modifier
			.fillMaxWidth(ELEMENT_WIDTH)
			.height(dimensionResource(id = R.dimen.xl)),
		onClick = { buttonAction },
		colors = ButtonDefaults.buttonColors(
			containerColor = color.value,
			contentColor = Color.White,
			disabledContainerColor = lightGrayButtonColor
		),
		enabled = active.value,
	) {
		if (watcard == null) {
			active.value = true
			color.value = darkerGrayButtonColor
			Text(
				stringResource(id = R.string.cancel)
			)
		} else if (watcard != null && !verified) {
			active.value = false
			Text(
				stringResource(id = R.string.pending_verification)
			)
		} else {
			active.value = true
			color.value = subletrPink
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
	SubletrTheme {
		WatcardVerificationPageView(
			uiState = WatcardVerificationUiState.Loaded(
				watcard = null,
				verified = false
			),
		)
	}
}


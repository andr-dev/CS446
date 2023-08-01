package org.uwaterloo.subletr.pages.watcardverification


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.bottomsheet.ImagePickerBottomSheet
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.subletrPalette
import org.uwaterloo.subletr.utils.ComposeFileProvider

private const val ELEMENT_WIDTH = 0.75f
@Composable
fun WatcardVerificationPageView(
	modifier: Modifier = Modifier,
	viewModel: WatcardVerificationPageViewModel = hiltViewModel(),
	uiState: WatcardVerificationUiState = viewModel.uiStateStream.subscribeAsState(
		WatcardVerificationUiState.Loading
	).value,
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
				uiState = uiState,
				viewModel = viewModel,
			)

			Spacer(modifier = Modifier.weight(8.0f))

			SetSubmitButton(
				uiState = uiState,
				viewModel = viewModel,
			)

			Spacer(modifier = Modifier.weight(3.0f))

			SetCancelButton(
				uiState = uiState,
				viewModel = viewModel,
			)

			Spacer(modifier = Modifier.weight(3.0f))

			SecondaryButton(onClick = {
				viewModel.navHostController.navigate(NavigationDestination.HOME.fullNavPath)
			}) {
				Text(text = stringResource(id = R.string.cancel))
			}

			Spacer(modifier = Modifier.weight(8.0f))
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetContent(
	viewModel: WatcardVerificationPageViewModel,
	uiState: WatcardVerificationUiState.Loaded,
) {
	val coroutineScope = rememberCoroutineScope()
	val context = LocalContext.current

	var imageUri by remember { mutableStateOf<Uri?>(null) }
	var hasImage by remember { mutableStateOf(false) }
	var openImagePicker by rememberSaveable { mutableStateOf(false) }
	val imagePickerBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

	val imageSelectorLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetContent()
	) {
		imageUri = it
		hasImage = true
	}

	val cameraLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.TakePicture(),
	) {
		hasImage = it
	}
	Box(
		modifier = Modifier
			.padding(dimensionResource(id = R.dimen.s))
			.fillMaxWidth()
			.height(dimensionResource(id = R.dimen.watcard_image_height))
			.clip(RoundedCornerShape(dimensionResource(id = R.dimen.s)))
			.border(
				shape = RoundedCornerShape(dimensionResource(id = R.dimen.s)),
				color = MaterialTheme.subletrPalette.textFieldBorderColor,
				width = dimensionResource(id = R.dimen.xxxs),
			)
			.background(MaterialTheme.subletrPalette.secondaryButtonBackgroundColor)
			.clickable(
				enabled = !uiState.submitted
			) {
				openImagePicker = true
			},
		contentAlignment = Alignment.Center,
	) {
		if (uiState.watcard == null) {
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
					color = MaterialTheme.subletrPalette.primaryTextColor
				)
			}
		} else {
			AsyncImage(
				modifier = Modifier
					.fillMaxWidth()
					.height(dimensionResource(id = R.dimen.watcard_image_height)),
				model = ImageRequest.Builder(LocalContext.current)
					.data(uiState.watcard)
					.crossfade(false)
					.build(),
				fallback = painterResource(R.drawable.baseline_image_not_supported_24),
				contentDescription = stringResource(R.string.image_not_supported),
				contentScale = ContentScale.Fit,
			)
		}
	}
	if (openImagePicker) {
		ImagePickerBottomSheet(
			bottomSheetState = imagePickerBottomSheetState,
			onDismissRequest = { openImagePicker = false },
			onTakePhotoClick = {
				coroutineScope.launch { imagePickerBottomSheetState.hide() }.invokeOnCompletion {
					if (!imagePickerBottomSheetState.isVisible) {
						openImagePicker = false
					}
					val uri = ComposeFileProvider.getImageUri(context)
					imageUri = uri
					cameraLauncher.launch(uri)
				}
			},
			onChooseImageClick = {
				coroutineScope.launch { imagePickerBottomSheetState.hide() }.invokeOnCompletion {
					if (!imagePickerBottomSheetState.isVisible) {
						openImagePicker = false
					}
					imageSelectorLauncher.launch("image/*")
				}
			},
		)
	}

	if (imageUri != null && hasImage) {
		viewModel.updateWatcardBitmap(context, imageUri!!)
		imageUri = null
		hasImage = false
	}
}

@Composable
fun SetSubmitButton(
	viewModel: WatcardVerificationPageViewModel,
	uiState: WatcardVerificationUiState.Loaded,
	) {
	PrimaryButton(
		modifier = Modifier
			.fillMaxWidth(ELEMENT_WIDTH)
			.height(dimensionResource(id = R.dimen.xl)),
		enabled = uiState.watcard != null && !uiState.submitted,

		onClick = {
			viewModel.uploadWatcard(uiState.watcard!!)
			viewModel.submittedStream.onNext(true)
		},
		colors = ButtonDefaults.buttonColors(
			containerColor =
			if (!uiState.submitted) MaterialTheme.subletrPalette.subletrPink
			else MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
			contentColor = MaterialTheme.subletrPalette.textOnSubletrPink,
		)
	) {
		if (uiState.watcard == null || !uiState.submitted) {
			Text(
				stringResource(id = R.string.submit),
				color = MaterialTheme.subletrPalette.textOnSubletrPink
			)
		} else if (!uiState.verified){
			Icon(
				painter = painterResource(
					id = R.drawable.baseline_check_24
				),
				contentDescription = stringResource(id = R.string.checkmark_sign),
			)
			Text(
				modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.xs)),
				text = stringResource(id = R.string.image_uploaded),
				color = MaterialTheme.subletrPalette.secondaryTextColor,
			)
		} else {
			Icon(
				painter = painterResource(
					id = R.drawable.baseline_check_24
				),
				contentDescription = stringResource(id = R.string.checkmark_sign),
			)
			Text(
				modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.xs)),
				text = stringResource(id = R.string.image_verified),
				color = MaterialTheme.subletrPalette.secondaryTextColor,
			)
		}
	}
}


@Composable
fun SetCancelButton(
	uiState: WatcardVerificationUiState.Loaded,
	viewModel: WatcardVerificationPageViewModel,
) {
	var active by remember { mutableStateOf(true) }

	var color by remember { mutableStateOf(Color.Transparent) }
	color = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor

	SecondaryButton(
		modifier = Modifier
			.fillMaxWidth(ELEMENT_WIDTH)
			.height(dimensionResource(id = R.dimen.xl)),
		onClick = {
			viewModel.navHostController.navigate(NavigationDestination.HOME.fullNavPath)
		},
		colors = ButtonDefaults.buttonColors(
			containerColor = if (active && uiState.watcard == null || !uiState.submitted)
				MaterialTheme.subletrPalette.darkerGrayButtonColor
			else MaterialTheme.subletrPalette.subletrPink,
			contentColor = MaterialTheme.subletrPalette.primaryTextColor,
		),
		enabled = active,
	) {
		if (uiState.watcard == null || !uiState.submitted) {
			active = true
			Text(
				stringResource(id = R.string.skip),
				color = MaterialTheme.subletrPalette.primaryTextColor
			)
		} else if (!uiState.verified) {
			active = false
			Text(
				stringResource(id = R.string.pending_verification),
				color = MaterialTheme.subletrPalette.secondaryTextColor
			)
		} else {
			active = true
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
				verified = false,
				submitted = false,
				watcard = null,
			),
		)
	}
}


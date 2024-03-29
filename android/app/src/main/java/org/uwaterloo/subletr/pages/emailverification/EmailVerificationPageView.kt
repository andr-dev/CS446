package org.uwaterloo.subletr.pages.emailverification
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import okhttp3.internal.immutableListOf
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun EmailVerificationPageView(
	modifier: Modifier = Modifier,
	@Suppress("UnusedParameter")
	userId: Int?,
	viewModel: EmailVerificationPageViewModel = hiltViewModel(),
	uiState: EmailVerificationPageUiState = viewModel.uiStateStream.subscribeAsState(
		EmailVerificationPageUiState.Loading
	).value,
) {
	if (uiState is EmailVerificationPageUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f)
				.imePadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			CircularProgressIndicator()
		}
	} else if (uiState is EmailVerificationPageUiState.Loaded) {
		fun onTextViewValueChange(index: Int, value: String) {
			val newCode = uiState.verificationCode.mapIndexed { i, element ->
				if (i == index) {
					value
				} else {
					element
				}
			}
			viewModel.updateUiState(
				EmailVerificationPageUiState.Loaded(
					verificationCode = newCode
				)
			)
		}
		Column(
			modifier = modifier.fillMaxSize(1.0f),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Top,
		) {
			Spacer(
				modifier = Modifier.weight(weight = 8.0f)
			)
			Text(
				text = stringResource(id = R.string.email_verification),
				style = MaterialTheme.typography.titleMedium,
			)
			Spacer(
				modifier = Modifier.height(dimensionResource(id = R.dimen.xxs))
			)
			Text(
				text = stringResource(id = R.string.email_verification_instruction),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.fillMaxWidth(ELEMENT_WIDTH),
			)
			Spacer(
				modifier = Modifier.weight(weight = 20.0f)
			)
			VerificationCodeTextField(uiState.verificationCode, ::onTextViewValueChange)
			Spacer(
				modifier = Modifier.weight(weight = 20.0f)
			)
			Button(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH)
					.height(dimensionResource(id = R.dimen.xl)),
				onClick = { viewModel.navHostController.navigate("home") },
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.subletrPalette.subletrPink,
					contentColor = MaterialTheme.subletrPalette.textOnSubletrPink,
				)
			) {
				Text(
					text = stringResource(id = R.string.verify),
					color = MaterialTheme.subletrPalette.textOnSubletrPink,
					style = MaterialTheme.typography.labelMedium,
					textAlign = TextAlign.Justify,
				)
			}
			Spacer(
				modifier = Modifier.height(dimensionResource(id = R.dimen.s))
			)
			Button(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH)
					.height(dimensionResource(id = R.dimen.xl)),
				onClick = {},
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
					contentColor = MaterialTheme.subletrPalette.primaryTextColor,
				)
			) {
				Text(
					text = stringResource(id = R.string.resend_code),
					color = MaterialTheme.subletrPalette.secondaryTextColor,
					style = MaterialTheme.typography.labelMedium,

					)
			}
			Spacer(
				modifier = Modifier.weight(weight = 13.0f)
			)
		}
	}
}

private const val ELEMENT_WIDTH = 0.75f

@Composable
fun TextFieldBox(
	index: Int,
	focusManager: FocusManager,
	verificationCodeValue: List<String>,
	onTextViewValueChange: (Int, String) -> Unit,
) {
	val isFocused by remember{ mutableStateOf(false) }

	TextField(
		modifier = Modifier
			.width(dimensionResource(id = R.dimen.xxl))
			.height(dimensionResource(id = R.dimen.xxl))
			.wrapContentSize(align = Alignment.Center)
			.border(
				BorderStroke(
					width = dimensionResource(id = R.dimen.xxxs),
					color =
					if (isFocused) MaterialTheme.subletrPalette.subletrPink
					else MaterialTheme.subletrPalette.textFieldBorderColor,
				),
				shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxs))
			)
			.onFocusChanged {
				it.isFocused
			},
		shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.xs)),
		colors = TextFieldDefaults.colors(
			unfocusedContainerColor = MaterialTheme.subletrPalette.primaryBackgroundColor,
			focusedContainerColor = MaterialTheme.subletrPalette.primaryBackgroundColor,
			unfocusedIndicatorColor = Color.Transparent,
			focusedIndicatorColor = Color.Transparent,
		),
		singleLine = true,
		textStyle = MaterialTheme.typography.displayMedium,

		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		value = verificationCodeValue[index],
		onValueChange = {
			if (it.length <= 1) {
				onTextViewValueChange(index, it)
				if (it != " " && it != "") {
					focusManager.moveFocus(FocusDirection.Right)
				}
			}
		}
	)
}

@Composable
fun VerificationCodeTextField(
	verificationCodeValue: List<String>,
	onTextViewValueChange: (Int, String) -> Unit,
) {
	val focusManager = LocalFocusManager.current
	Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs))) {
		for (i in 0..4) {
			TextFieldBox(
				index = i,
				focusManager,
				verificationCodeValue,
				onTextViewValueChange
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
fun EmailVerificationPageViewLoadingPreview() {
	EmailVerificationPageView(
		userId = 0,
	)
}

@Preview(showBackground = true)
@Composable
fun EmailVerificationPageViewLoadedPreview() {
	SubletrTheme {
		EmailVerificationPageView(
			uiState = EmailVerificationPageUiState.Loaded(
				verificationCode = immutableListOf("", "", "", "", "")
			),
			userId = 0,
		)
	}
}


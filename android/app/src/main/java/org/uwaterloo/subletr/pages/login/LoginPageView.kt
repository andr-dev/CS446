package org.uwaterloo.subletr.pages.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.buttonBackgroundColor
import org.uwaterloo.subletr.theme.secondarySubletrPink
import org.uwaterloo.subletr.theme.subletrPink

@Composable
fun LoginPageView(
	modifier: Modifier = Modifier,
	viewModel: LoginPageViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiStateStream.subscribeAsState(
		LoginPageUiState.Loading
	)
	if (uiState is LoginPageUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f)
				.imePadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Text(
				text = stringResource(id = R.string.loading)
			)
		}
	} else {
		val castedUiState = uiState as LoginPageUiState.Loaded

		Column(
			modifier = modifier
				.fillMaxSize(1.0f)
				.imePadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Bottom,
		) {
			Row {
				Text(
					text = stringResource(id = castedUiState.titleBlackStringId),
					style = MaterialTheme.typography.titleLarge,
				)
				Text(
					text = stringResource(id = castedUiState.titlePinkStringId),
					style = MaterialTheme.typography.titleLarge,
					color = subletrPink,
				)
			}

			Spacer(
				modifier = Modifier.height(80.dp)
			)

			TextField(
				value = "",
				onValueChange = {},
				placeholder = {
					Text(
						text = stringResource(id = castedUiState.emailStringId)
					)
				},
				colors = TextFieldDefaults.colors(
					focusedContainerColor = Color.Transparent,
					unfocusedContainerColor = Color.Transparent,
					disabledContainerColor = Color.Transparent,
					focusedIndicatorColor = subletrPink,
					unfocusedIndicatorColor = secondarySubletrPink,
					disabledIndicatorColor = Color.Transparent
				)
			)

			Spacer(
				modifier = Modifier.height(40.dp)
			)

			TextField(
				value = "",
				onValueChange = {},
				placeholder = {
					Text(
						text = stringResource(id = castedUiState.passwordStringId)
					)
				},
				colors = TextFieldDefaults.colors(
					focusedContainerColor = Color.Transparent,
					unfocusedContainerColor = Color.Transparent,
					disabledContainerColor = Color.Transparent,
					focusedIndicatorColor = subletrPink,
					unfocusedIndicatorColor = secondarySubletrPink,
					disabledIndicatorColor = Color.Transparent
				)
			)

			Spacer(
				modifier = Modifier.height(40.dp)
			)

			Button(
				colors = ButtonDefaults.buttonColors(
					containerColor = buttonBackgroundColor
				),
				onClick = {},
			) {
				Text(
					text = stringResource(id = castedUiState.loginStringId)
				)
			}

			Spacer(
				modifier = Modifier.height(30.dp)
			)

			Row(
				modifier = Modifier,
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Spacer(
					modifier = Modifier.width(80.dp)
				)

				Box(
					modifier
						.height(2.dp)
						.background(color = Color.Gray)
						.weight(1.0f)
				)

				Text(
					text = stringResource(id = castedUiState.orStringId)
				)

				Box(
					modifier
						.height(2.dp)
						.background(color = Color.Gray)
						.weight(1.0f)
				)

				Spacer(
					modifier = Modifier.width(80.dp)
				)
			}

			Spacer(
				modifier = Modifier.height(30.dp)
			)

			Button(
				onClick = {},
				colors = ButtonDefaults.buttonColors(
					containerColor = buttonBackgroundColor
				),
			) {
				Text(
					text = stringResource(id = castedUiState.createAccountStringId)
				)
			}

			Spacer(
				modifier = Modifier.height(70.dp)
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
fun LoginPageViewPreview() {
	LoginPageView()
}

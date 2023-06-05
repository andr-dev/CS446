package org.uwaterloo.subletr.pages.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.models.ConcavePentagon
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.buttonBackgroundColor
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink

@Composable
fun LoginPageView(
	modifier: Modifier = Modifier,
	navHostController: NavHostController,
	viewModel: LoginPageViewModel = hiltViewModel(),
	uiState: LoginPageUiState = viewModel.uiStateStream.subscribeAsState(
		LoginPageUiState.Loading
	).value,
) {
	var passwordVisible by rememberSaveable { mutableStateOf(false) }

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
	} else if (uiState is LoginPageUiState.Loaded) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Image(
				painter = painterResource(
					id = R.drawable.uwaterloo_overview,
				),
				contentDescription = stringResource(id = R.string.uw_overview),
				contentScale = ContentScale.FillWidth,
				modifier = Modifier
					.fillMaxWidth(1.0f)
					.clip(ConcavePentagon())
			)

			Spacer(
				modifier = Modifier.weight(weight = 20.0f)
			)

			Row {
				Text(
					text = stringResource(id = R.string.app_name_black_part),
					style = MaterialTheme.typography.titleLarge,
				)
				Text(
					text = stringResource(id = R.string.app_name_pink_part),
					style = MaterialTheme.typography.titleLarge,
					color = subletrPink,
				)
			}

			Spacer(
				modifier = Modifier.weight(weight = 10.0f)
			)

			TextField(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH),
				shape = RoundedCornerShape(size = 100.dp),
				colors = TextFieldDefaults.colors(
					unfocusedContainerColor = buttonBackgroundColor,
					focusedContainerColor = buttonBackgroundColor,
					unfocusedIndicatorColor = Color.Transparent,
					focusedIndicatorColor = Color.Transparent,
				),
				placeholder = {
					Text(
						text = stringResource(id = R.string.email),
						color = secondaryTextColor,
					)
				},
				value = uiState.email,
				onValueChange = {
					viewModel.updateUiState(
						LoginPageUiState.Loaded(
							email = it,
							password = uiState.password,
						)
					)
				}
			)

			Spacer(
				modifier = Modifier.weight(weight = 2.0f)
			)

			TextField(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH),
				shape = RoundedCornerShape(size = 100.dp),
				colors = TextFieldDefaults.colors(
					unfocusedContainerColor = buttonBackgroundColor,
					focusedContainerColor = buttonBackgroundColor,
					unfocusedIndicatorColor = Color.Transparent,
					focusedIndicatorColor = Color.Transparent,
				),
				visualTransformation =
				if (passwordVisible) VisualTransformation.None
				else PasswordVisualTransformation(),
				placeholder = {
					Text(
						text = stringResource(id = R.string.password),
						color = secondaryTextColor,
					)
				},
				trailingIcon = {
		  			IconButton(onClick = { passwordVisible = !passwordVisible }) {
						Icon(
							imageVector =
							if (passwordVisible) Icons.Filled.Visibility
							else Icons.Filled.VisibilityOff,
							contentDescription =
							if (passwordVisible) stringResource(
								id = R.string.hide_password
							)
							else stringResource(
								id = R.string.show_password
							),
						)
					}
				},
				value = uiState.password,
				onValueChange = {
					viewModel.updateUiState(
						LoginPageUiState.Loaded(
							email = uiState.email,
							password = it,
						)
					)
				}
			)

			Spacer(
				modifier = Modifier.weight(weight = 2.0f)
			)

			Button(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH)
					.height(50.dp),
				onClick = {},
				colors = ButtonDefaults.buttonColors(
					containerColor = subletrPink,
					contentColor = Color.White,
				)
			) {
				Text(
					text = stringResource(id = R.string.log_in),
					color = Color.White,
				)
			}

			Spacer(
				modifier = Modifier.weight(weight = 15.0f)
			)

			Row(verticalAlignment = Alignment.CenterVertically) {
				Text(
					text = stringResource(id = R.string.dont_have_an_account),
					color = secondaryTextColor,
				)
				Button(
					contentPadding = PaddingValues(1.dp),
					onClick = {
						navHostController.navigate(NavigationDestination.CREATE_ACCOUNT.rootNavPath)
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = Color.Transparent,
						contentColor = Color.Transparent,
					)
				) {
					Text(
						text = stringResource(id = R.string.sign_up),
						color = subletrPink,
					)
				}
			}

			Spacer(
				modifier = Modifier
					.weight(weight = 5.0f)
					.imePadding()
			)
		}
	}
}

const val ELEMENT_WIDTH = 0.75f

@Preview(showBackground = true)
@Composable
fun LoginPageViewLoadingPreview() {
	LoginPageView(
		navHostController = rememberNavController(),
	)
}

@Preview(showBackground = true)
@Composable
fun LoginPageViewLoadedPreview() {
	SubletrTheme {
		LoginPageView(
			navHostController = rememberNavController(),
			uiState = LoginPageUiState.Loaded(
				email = "",
				password = "",
			)
		)
	}
}

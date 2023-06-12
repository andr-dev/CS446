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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.textfield.RoundedPasswordTextField
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.models.ConcavePentagon
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.textOnSubletrPink

@Composable
fun LoginPageView(
	modifier: Modifier = Modifier,
	viewModel: LoginPageViewModel = hiltViewModel(),
	navHostController: NavHostController,
	uiState: LoginPageUiState = viewModel.uiStateStream.subscribeAsState(
		LoginPageUiState.Loading
	).value,
) {
	if (uiState is LoginPageUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f)
				.imePadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			CircularProgressIndicator()
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

			RoundedTextField(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH),
				placeholder = {
					Text(
						text = stringResource(id = R.string.email),
						color = secondaryTextColor,
					)
				},
				value = uiState.email,
				onValueChange = {
					viewModel.emailStream.onNext(it)
				}
			)

			Spacer(
				modifier = Modifier.weight(weight = 2.0f)
			)

			RoundedPasswordTextField(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH),
				placeholder = {
					Text(
						text = stringResource(id = R.string.password),
						color = secondaryTextColor,
					)
				},
				value = uiState.password,
				onValueChange = {
					viewModel.passwordStream.onNext(it)
				}
			)

			Spacer(
				modifier = Modifier.weight(weight = 2.0f)
			)

			PrimaryButton(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH)
					.height(dimensionResource(id = R.dimen.xl)),
				onClick = {
			        viewModel.loginStream.onNext(uiState)
//					navHostController.navigate(NavigationDestination.HOME.rootNavPath)
				},
			) {
				Text(
					text = stringResource(id = R.string.log_in),
					color = textOnSubletrPink,
				)
			}

			if (uiState.infoTextStringId != null) {
				Text(
					text = stringResource(id = uiState.infoTextStringId)
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
					contentPadding = PaddingValues(dimensionResource(id = R.dimen.xxxxs)),
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

private const val ELEMENT_WIDTH = 0.75f

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
			uiState = LoginPageUiState.Loaded(
				email = "",
				password = "",
				infoTextStringId = null,
			),
			navHostController = rememberNavController(),
		)
	}
}

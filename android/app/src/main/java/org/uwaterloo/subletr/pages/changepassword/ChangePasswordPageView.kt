package org.uwaterloo.subletr.pages.changepassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.components.textfield.UnderlinedPasswordTextField
import org.uwaterloo.subletr.theme.changePasswordTopBarTitle
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun ChangePasswordPageView(
	modifier: Modifier = Modifier,
	viewModel: ChangePasswordPageViewModel = hiltViewModel(),
	uiState: ChangePasswordPageUiState = viewModel.uiStateStream.subscribeAsState(
		initial = ChangePasswordPageUiState.Loading,
	).value,
) {
	Scaffold(
		modifier = modifier,
		topBar = {
			Row(
				modifier = Modifier,
				verticalAlignment = Alignment.CenterVertically,
			) {
				IconButton(
					onClick = {
						viewModel.navHostController.popBackStack()
					}
				) {
					Icon(
						painter = painterResource(
							id = R.drawable.arrow_back_solid_black_24
						),
						contentDescription = stringResource(id = R.string.back_arrow),
					)
				}
				
				Text(
					text = stringResource(
						id = R.string.change_your_password
					),
					style = changePasswordTopBarTitle,
				)
			}
		}
	) { paddingValues ->
		if (uiState is ChangePasswordPageUiState.Loading) {
			Column(
				modifier = Modifier
					.padding(paddingValues = paddingValues)
					.fillMaxSize(fraction = 1.0f)
					.imePadding(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				CircularProgressIndicator()
			}
		}
		else if (uiState is ChangePasswordPageUiState.Loaded) {
			Column(
				modifier = Modifier
					.padding(paddingValues = paddingValues)
					.fillMaxSize(fraction = 1.0f),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Spacer(
					modifier = Modifier
						.height(dimensionResource(id = R.dimen.l)),
				)

				UnderlinedPasswordTextField(
					modifier = Modifier.fillMaxWidth(fraction = ELEMENT_WIDTH),
					value = uiState.oldPassword,
					onValueChange = {
						viewModel.oldPasswordStream.onNext(it)
					},
					label = {
						Text(
							modifier = Modifier,
							text = stringResource(id = R.string.old_password),
							color = MaterialTheme.subletrPalette.secondaryTextColor,
						)
					},
				)

				Spacer(
					modifier = Modifier
						.height(dimensionResource(id = R.dimen.l)),
				)

				UnderlinedPasswordTextField(
					modifier = Modifier.fillMaxWidth(fraction = ELEMENT_WIDTH),
					value = uiState.newPassword,
					onValueChange = {
						viewModel.newPasswordStream.onNext(it)
					},
					label = {
						Text(
							text = stringResource(id = R.string.new_password),
							color = MaterialTheme.subletrPalette.secondaryTextColor,
						)
					},
				)

				Spacer(
					modifier = Modifier
						.height(dimensionResource(id = R.dimen.l)),
				)

				UnderlinedPasswordTextField(
					modifier = Modifier.fillMaxWidth(fraction = ELEMENT_WIDTH),
					value = uiState.confirmNewPassword,
					onValueChange = {
						viewModel.confirmNewPasswordStream.onNext(it)
					},
					label = {
						Text(
							text = stringResource(id = R.string.confirm_new_password),
							color = MaterialTheme.subletrPalette.secondaryTextColor,
						)
					},
				)

				Spacer(
					modifier = Modifier
						.height(dimensionResource(id = R.dimen.l)),
				)

				SecondaryButton(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH)
						.height(dimensionResource(id = R.dimen.xl)),
					onClick = {
				  		viewModel.changePasswordStream.onNext(uiState)
					},
				) {
					Text(
						text = stringResource(id = R.string.change_password)
					)
				}

				if (uiState.infoTextStringId != null) {
					Text(
						text = stringResource(id = uiState.infoTextStringId)
					)
				}
			}
		}
	}
}

private const val ELEMENT_WIDTH = 0.80f

@Preview(showBackground = true)
@Composable
fun ChangePasswordPageViewPreview() {
	ChangePasswordPageView()
}

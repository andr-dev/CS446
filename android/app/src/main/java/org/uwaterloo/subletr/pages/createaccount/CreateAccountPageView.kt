package org.uwaterloo.subletr.pages.createaccount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.navOptions
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.dropdown.RoundedDropdown
import org.uwaterloo.subletr.components.textfield.RoundedPasswordTextField
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.SubletrTypography
import org.uwaterloo.subletr.theme.subletrPalette


@Composable
fun CreateAccountPageView(
	modifier: Modifier = Modifier,
	viewModel: CreateAccountPageViewModel = hiltViewModel(),
	uiState: CreateAccountPageUiState = viewModel.uiStateStream.subscribeAsState(
		CreateAccountPageUiState.Loading
	).value,
) {
	val scrollState = rememberScrollState()
	if (uiState is CreateAccountPageUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			CircularProgressIndicator()
		}
	} else if (uiState is CreateAccountPageUiState.Loaded) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f)
				.imePadding()
				.verticalScroll(scrollState),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {

				Spacer(
					modifier = Modifier.width(dimensionResource(id = R.dimen.xl))
				)

				Column(
					modifier = modifier,
				) {
					Text(
						text = stringResource(id = R.string.welcome_to),
						style = SubletrTypography.bodyMedium,
						color = MaterialTheme.subletrPalette.primaryTextColor,
					)
					Row {
						Text(
							text = stringResource(id = R.string.app_name_black_part),
							style = MaterialTheme.typography.titleLarge,
							color = MaterialTheme.subletrPalette.primaryTextColor,
						)
						Text(
							text = stringResource(id = R.string.app_name_pink_part),
							style = MaterialTheme.typography.titleLarge,
							color = MaterialTheme.subletrPalette.subletrPink,
						)
					}
				}

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
				)
			}

			Spacer(
				modifier = Modifier.weight(weight = 2.0f)
			)

			RoundedTextField(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH),
				placeholder = {
					Text(
						text = stringResource(id = R.string.first_name),
						color = MaterialTheme.subletrPalette.secondaryTextColor,
					)
				},
				value = uiState.firstName,
				onValueChange = {
					viewModel.firstNameStream.onNext(it)
				},
				supportingText = {
					if (uiState.firstNameInfoTextStringId != null) {
						Text(
							text = stringResource(id = uiState.firstNameInfoTextStringId)
						)
					}
				},
			)

			Spacer(
				modifier = Modifier.weight(weight = 2.0f)
			)

			RoundedTextField(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH),
				placeholder = {
					Text(
						text = stringResource(id = R.string.last_name),
						color = MaterialTheme.subletrPalette.secondaryTextColor,
					)
				},
				value = uiState.lastName,
				onValueChange = {
					viewModel.lastNameStream.onNext(it)
				},
				supportingText = {
					if (uiState.lastNameInfoTextStringId != null) {
						Text(
							text = stringResource(id = uiState.lastNameInfoTextStringId)
						)
					}
				},
			)

			Spacer(
				modifier = Modifier.weight(weight = 2.0f)
			)

			RoundedTextField(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Email,
					autoCorrect = false,
				),
				placeholder = {
					Text(
						text = stringResource(id = R.string.email),
						color = MaterialTheme.subletrPalette.secondaryTextColor,
					)
				},
				value = uiState.email,
				onValueChange = {
					viewModel.emailStream.onNext(it)
				},
				supportingText = {
					if (uiState.emailInfoTextStringId != null) {
						Text(
							text = stringResource(id = uiState.emailInfoTextStringId)
						)
					}
				},
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
						color = MaterialTheme.subletrPalette.secondaryTextColor,
					)
				},
				value = uiState.password,
				onValueChange = {
					viewModel.passwordStream.onNext(it)
				},
				supportingText = {
					if (uiState.passwordInfoTextStringId != null) {
						Text(
							text = stringResource(id = uiState.passwordInfoTextStringId)
						)
					}
				},
			)

			Spacer(
				modifier = Modifier.weight(weight = 2.0f)
			)

			RoundedPasswordTextField(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH),
				placeholder = {
					Text(
						text = stringResource(id = R.string.confirm_password),
						color = MaterialTheme.subletrPalette.secondaryTextColor,
					)
				},
				value = uiState.confirmPassword,
				onValueChange = {
					viewModel.confirmPasswordStream.onNext(it)
				},
				supportingText = {
					if (uiState.confirmPasswordInfoTextStringId != null) {
						Text(
							text = stringResource(id = uiState.confirmPasswordInfoTextStringId)
						)
					}
				},
			)

			Spacer(
				modifier = Modifier.weight(weight = 2.0f)
			)

			RoundedDropdown(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH),
				dropdownItems = Gender.values(),
				selectedDropdownItem = uiState.gender,
				dropdownItemToString = { stringResource(id = it.stringId)},
				setSelectedDropdownItem = {
					viewModel.genderStream.onNext(it)
				},
			)

			Spacer(
				modifier = Modifier.weight(weight = 2.0f)
			)

			PrimaryButton(
				modifier = Modifier
					.fillMaxWidth(ELEMENT_WIDTH)
					.height(dimensionResource(id = R.dimen.xl)),
				onClick = {
					viewModel.createAccountStream.onNext(uiState)
				},
			) {
				Text(
					text = stringResource(id = R.string.create_account),
					color = MaterialTheme.subletrPalette.textOnSubletrPink,
				)
			}

			Spacer(
				modifier = Modifier.weight(weight = 2.0f)
			)

			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = stringResource(id = R.string.already_have_an_account),
					color = MaterialTheme.subletrPalette.secondaryTextColor,
				)
				Button(
					contentPadding = PaddingValues(dimensionResource(id = R.dimen.xxxxs)),
					onClick = {
						viewModel.navHostController.navigate(
							NavigationDestination.LOGIN.rootNavPath,
							navOptions = navOptions {
								popUpTo(viewModel.navHostController.graph.id)
							}
						)
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = Color.Transparent,
						contentColor = Color.Transparent,
					)
				) {
					Text(
						text = stringResource(id = R.string.log_in),
						color = MaterialTheme.subletrPalette.subletrPink,
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
fun CreateAccountPagePreview() {
	SubletrTheme {
		CreateAccountPageView(
			uiState = CreateAccountPageUiState.Loaded(
				firstName = "",
				lastName = "",
				email = "",
				password = "",
				confirmPassword = "",
				gender = Gender.OTHER,
				firstNameInfoTextStringId = null,
				lastNameInfoTextStringId = null,
				emailInfoTextStringId = null,
				passwordInfoTextStringId = null,
				confirmPasswordInfoTextStringId = null,
			)
		)
	}
}

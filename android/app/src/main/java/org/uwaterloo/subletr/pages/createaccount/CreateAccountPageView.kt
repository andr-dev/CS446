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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.pages.login.ELEMENT_WIDTH
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.SubletrTypography
import org.uwaterloo.subletr.theme.buttonBackgroundColor
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink


@Composable
fun CreateAccountPageView(
	modifier: Modifier = Modifier,
	onNavigateToLogin: () -> Unit,
	viewModel: CreateAccountPageViewModel = hiltViewModel(),
	uiState: CreateAccountPageUiState = viewModel.uiStateStream.subscribeAsState(
		CreateAccountPageUiState.NewAccount
	).value,
) {
	var passwordVisible by rememberSaveable { mutableStateOf(false) }
	if (uiState is CreateAccountPageUiState.NewAccountInfo) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f)
				.imePadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Column(
				modifier = modifier,
				horizontalAlignment = Alignment.Start,
				verticalArrangement = Arrangement.Center,
			) {
				Text(
					text = stringResource(id = R.string.welcome_to),
					style = SubletrTypography.bodyMedium,
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
			}

			Spacer(
				modifier = Modifier.weight(weight = 1.0f)
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
						text = stringResource(id = R.string.first_name),
						color = secondaryTextColor,
					)
				},
				value = uiState.firstName,
				onValueChange = {
					viewModel.updateUiState(
						CreateAccountPageUiState.NewAccountInfo(
							firstName = it,
							lastName = uiState.lastName,
							email = uiState.email,
							password = uiState.password,
							confirmPassword = uiState.confirmPassword,
							gender = uiState.gender,
						)
					)
				}
			)

			Spacer(
				modifier = Modifier.weight(weight = 1.0f)
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
						text = stringResource(id = R.string.last_name),
						color = secondaryTextColor,
					)
				},
				value = uiState.lastName,
				onValueChange = {
					viewModel.updateUiState(
						CreateAccountPageUiState.NewAccountInfo(
							firstName = uiState.firstName,
							lastName = it,
							email = uiState.email,
							password = uiState.password,
							confirmPassword = uiState.confirmPassword,
							gender = uiState.gender,
						)
					)
				}
			)

			Spacer(
				modifier = Modifier.weight(weight = 1.0f)
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
						CreateAccountPageUiState.NewAccountInfo(
							firstName = uiState.firstName,
							lastName = uiState.lastName,
							email = it,
							password = uiState.password,
							confirmPassword = uiState.confirmPassword,
							gender = uiState.gender,
						)
					)
				}
			)

			Spacer(
				modifier = Modifier.weight(weight = 1.0f)
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
						CreateAccountPageUiState.NewAccountInfo(
							firstName = uiState.firstName,
							lastName = uiState.lastName,
							email = uiState.email,
							password = it,
							confirmPassword = uiState.confirmPassword,
							gender = uiState.gender,
						)
					)
				}
			)

			Spacer(
				modifier = Modifier.weight(weight = 1.0f)
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
						text = stringResource(id = R.string.confirm_password),
						color = secondaryTextColor,
					)
				},
				value = uiState.confirmPassword,
				onValueChange = {
					viewModel.updateUiState(
						CreateAccountPageUiState.NewAccountInfo(
							firstName = uiState.firstName,
							lastName = uiState.confirmPassword,
							email = uiState.email,
							password = uiState.password,
							confirmPassword = it,
							gender = uiState.gender,
						)
					)
				}
			)

			Spacer(
				modifier = Modifier.weight(weight = 1.0f)
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
						text = stringResource(id = R.string.gender),
						color = secondaryTextColor,
					)
				},
				value = uiState.gender.gender,
				onValueChange = {
					viewModel.updateUiState(
						CreateAccountPageUiState.NewAccountInfo(
							firstName = uiState.firstName,
							lastName = uiState.confirmPassword,
							email = uiState.email,
							password = uiState.password,
							confirmPassword = uiState.confirmPassword,
							gender = CreateAccountPageUiState.Gender.valueOf(it),
						)
					)
				}
			)

			Spacer(
				modifier = Modifier.weight(weight = 1.0f)
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
					text = stringResource(id = R.string.create_account),
					color = Color.White,
				)
			}

			Spacer(
				modifier = Modifier.weight(weight = 1.0f)
			)

			Row(verticalAlignment = Alignment.CenterVertically) {
				Text(
					text = stringResource(id = R.string.already_have_an_account),
					color = secondaryTextColor,
				)
				Button(
					contentPadding = PaddingValues(1.dp),
					onClick = onNavigateToLogin,
					colors = ButtonDefaults.buttonColors(
						containerColor = Color.Transparent,
						contentColor = Color.Transparent,
					)
				) {
					Text(
						text = stringResource(id = R.string.log_in),
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

@Preview(showBackground = true)
@Composable
fun CreateAccountPagePreview() {
	SubletrTheme {
		CreateAccountPageView(
			onNavigateToLogin = {},
			uiState = CreateAccountPageUiState.NewAccountInfo(
				firstName = "",
				lastName = "",
				email = "",
				password = "",
				confirmPassword = "",
				gender = CreateAccountPageUiState.Gender.OTHER,
			)
		)
	}
}

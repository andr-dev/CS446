package org.uwaterloo.subletr.pages.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.components.dropdown.UnderlinedDropdown
import org.uwaterloo.subletr.components.switch.PrimarySwitch
import org.uwaterloo.subletr.components.textfield.UnderlinedTextField
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.textOnSubletrPink

@Composable
fun AccountPageView(
	modifier: Modifier = Modifier,
	viewModel: AccountPageViewModel = hiltViewModel(),
	uiState: AccountPageUiState = viewModel.uiStateStream.subscribeAsState(
		AccountPageUiState.Loading
	).value,
) {
	if (uiState is AccountPageUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			CircularProgressIndicator()
		}
	}
	else if (uiState is AccountPageUiState.Loaded) {
		LazyColumn(
			modifier = modifier
				.fillMaxSize(fraction = 1.0f)
				.imePadding(),
			verticalArrangement = Arrangement.Top,
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			item {
				Box(
					modifier = Modifier
						.padding(dimensionResource(id = R.dimen.l))
						.clip(CircleShape),
				) {
					Box(
						modifier = Modifier
							.width(dimensionResource(id = R.dimen.xxl))
							.height(dimensionResource(id = R.dimen.xxl))
							.background(Color.Gray),
					)
				}
			}
			item {
				Row(
					modifier = Modifier.fillMaxWidth(ELEMENT_WIDTH)
				) {
					Text(
						modifier = Modifier,
						text = stringResource(id = R.string.last_name),
						color = secondaryTextColor,
					)
				}
				UnderlinedTextField(
					modifier = Modifier.fillMaxWidth(ELEMENT_WIDTH),
					placeholder = {
						Text(
							text = stringResource(id = R.string.last_name),
							color = secondaryTextColor,
						)
					},
					value = "",
					onValueChange = {
						viewModel.updateUiState(
							AccountPageUiState.Loaded(
								lastName = it,
								firstName = uiState.firstName,
								gender = uiState.gender,
								settings = uiState.settings,
							)
						)
					},
				)
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.l)))
			}
			item {
				Row(
					modifier = Modifier.fillMaxWidth(ELEMENT_WIDTH)
				) {
					Text(
						modifier = Modifier,
						text = stringResource(id = R.string.first_name),
						color = secondaryTextColor,
					)
				}
				UnderlinedTextField(
					modifier = Modifier.fillMaxWidth(ELEMENT_WIDTH),
					placeholder = {
						Text(
							text = stringResource(id = R.string.first_name),
							color = secondaryTextColor,
						)
					},
					value = "",
					onValueChange = {
						viewModel.updateUiState(
							AccountPageUiState.Loaded(
								lastName = uiState.lastName,
								firstName = it,
								gender = uiState.gender,
								settings = uiState.settings,
							)
						)
					},
				)
			}
			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.l)))
			}
			item {
				Row(
					modifier = Modifier.fillMaxWidth(ELEMENT_WIDTH)
				) {
					Text(
						modifier = Modifier,
						text = stringResource(id = R.string.gender),
						color = secondaryTextColor,
					)
				}
				UnderlinedDropdown(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH),
					dropdownItems = Gender.values(),
					dropdownItemToString = {
				        stringResource(id = it.stringId)
					},
					selectedDropdownItem = uiState.gender,
					setSelectedDropdownItem = {
						viewModel.updateUiState(
							AccountPageUiState.Loaded(
								lastName = uiState.lastName,
								firstName = uiState.firstName,
								gender = it,
								settings = uiState.settings,
							)
						)
					}
				)
			}
			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.l)))
			}
			item {
				SecondaryButton(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH)
						.height(dimensionResource(id = R.dimen.xl)),
					onClick = {},
				) {
					Text(
						text = stringResource(id = R.string.change_password),
					)
				}
			}
			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.l)))
			}
			item {
				PrimaryButton(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH)
						.height(dimensionResource(id = R.dimen.xl)),
					onClick = {},
				) {
					Text(
						text = stringResource(id = R.string.manage_by_sublet),
						color = textOnSubletrPink,
					)
				}
			}
			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.l)))
			}
			item {
				Row(
					modifier = Modifier.fillMaxWidth(ELEMENT_WIDTH)
				) {
					Text(
						text = stringResource(id = R.string.other_settings),
						style = MaterialTheme.typography.titleSmall,
					)
				}
			}
			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))
			}
			itemsIndexed(
				items = uiState.settings,
			) { itemIndex, item ->
				Row(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(id = item.textStringId),
					)
					PrimarySwitch(
						checked = item.toggleState,
						onCheckedChange = {
							viewModel.updateUiState(
								uiState = AccountPageUiState.Loaded(
									lastName = uiState.lastName,
									firstName = uiState.firstName,
									gender = uiState.gender,
									settings = uiState.settings.mapIndexed { mapIndex, setting ->
										if (mapIndex == itemIndex) {
											AccountPageUiState.Setting(
												textStringId = setting.textStringId,
												it,
											)
										}
										else {
											setting
										}
									}
								)
							)
						}
					)
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.l)))
			}

			item {
				PrimaryButton(
					modifier = Modifier
						.fillMaxWidth(ELEMENT_WIDTH)
						.height(dimensionResource(id = R.dimen.xl)),
					onClick = {
						viewModel.logout()
					},
				) {
					Text(
						text = stringResource(id = R.string.log_out),
						color = textOnSubletrPink,
					)
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.l)))
			}
		}
	}
}

private const val ELEMENT_WIDTH = 0.75f

@Preview(showBackground = true)
@Composable
fun AccountViewLoadingPreview() {
	AccountPageView()
}

@Preview(showBackground = true)
@Composable
fun AccountPageViewLoadedPreview() {
	SubletrTheme {
		AccountPageView(
			uiState = AccountPageUiState.Loaded(
				lastName = "",
				firstName = "",
				gender = Gender.OTHER,
				settings = listOf(
					AccountPageUiState.Setting(
						textStringId = R.string.setting_1,
						toggleState = false,
					),
					AccountPageUiState.Setting(
						textStringId = R.string.setting_1,
						toggleState = true,
					)
				),
			)
		)
	}
}


@file:Suppress("CyclomaticComplexMethod", "ForbiddenComment")

package org.uwaterloo.subletr.pages.account

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.bottomsheet.ImagePickerBottomSheet
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.components.switch.PrimarySwitch
import org.uwaterloo.subletr.components.textfield.SquaredTextField
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.home.list.dateTimeFormatter
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.SubletrTypography
import org.uwaterloo.subletr.theme.accountHeadingSmallFont
import org.uwaterloo.subletr.theme.filterBoldFont
import org.uwaterloo.subletr.theme.listingTitleFont
import org.uwaterloo.subletr.theme.subletrPalette
import org.uwaterloo.subletr.utils.ComposeFileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPageView(
	modifier: Modifier = Modifier,
	viewModel: AccountPageViewModel = hiltViewModel(),
	uiState: AccountPageUiState = viewModel.uiStateStream.subscribeAsState(
		AccountPageUiState.Loading
	).value,
) {
	var nameExpanded by remember { mutableStateOf(false) }
	var genderExpanded by remember { mutableStateOf(false) }
	var initialPersonalInformation by remember {
		mutableStateOf(
			AccountPageUiState.PersonalInformation(
				lastName = "",
				firstName = "",
				gender = "",
			)
		)
	}

	if (uiState is AccountPageUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			CircularProgressIndicator()
		}
	} else if (uiState is AccountPageUiState.Loaded) {
		LazyColumn(
			modifier = modifier
				.padding(horizontal = dimensionResource(id = R.dimen.m))
				.fillMaxSize()
				.imePadding(),
			verticalArrangement = Arrangement.Top,
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			item {
				Box(
					modifier = Modifier
						.padding(dimensionResource(id = R.dimen.l))
				) {
					AsyncImage(
						modifier = Modifier
							.size(dimensionResource(id = R.dimen.xxxxxl))
							.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xxxxxl))),
						model = ImageRequest.Builder(LocalContext.current)
							.data(uiState.avatarBitmap)
							.crossfade(false)
							.build(),
						fallback = painterResource(R.drawable.default_avatar),
						contentDescription = stringResource(R.string.avatar),
						contentScale = ContentScale.Crop,
						alpha = if (!nameExpanded && !genderExpanded) 1.0f else 0.3F,
					)
					UploadAvatar(
						viewModel = viewModel,
						isEnabled = !nameExpanded && !genderExpanded,
						modifier = Modifier
							.size(dimensionResource(id = R.dimen.l))
							.align(Alignment.BottomEnd),
					)
				}
			}

			item {
				Card(
					modifier = Modifier.fillMaxWidth(),
					shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
					elevation = CardDefaults.elevatedCardElevation(
						defaultElevation = dimensionResource(id = R.dimen.xxs),
					),
					onClick = {
						// TODO navigate to profile page
					},
					colors = CardDefaults.cardColors(
						containerColor = MaterialTheme.subletrPalette.squaredTextFieldBackgroundColor,
						disabledContainerColor = MaterialTheme.subletrPalette.secondaryBackgroundColor,
						contentColor = MaterialTheme.subletrPalette.primaryTextColor,
						disabledContentColor = MaterialTheme.subletrPalette.darkerGrayButtonColor,
					),
					enabled = !nameExpanded && !genderExpanded,
				) {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(dimensionResource(id = R.dimen.s)),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically,
					) {
						Text(
							modifier = Modifier.fillMaxWidth(0.9f),
							text = stringResource(id = R.string.view_public_profile),
							color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.primaryTextColor
							else MaterialTheme.subletrPalette.darkerGrayButtonColor
						)
						Icon(
							painter = painterResource(
								id = R.drawable.arrow_right_solid_black_24
							),
							contentDescription = stringResource(id = R.string.view_public_profile),
						)
					}
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}


			item {
				PageDivider()
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Start
				) {
					Text(
						text = stringResource(id = R.string.personal_info),
						style = SubletrTypography.displaySmall,
						color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.secondaryTextColor
						else MaterialTheme.subletrPalette.darkerGrayButtonColor,
					)
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				Card(
					modifier = Modifier
						.fillMaxWidth(),
					onClick = {
						if (nameExpanded) {
							viewModel.personalInformationStream.onNext(
								initialPersonalInformation
							)
						} else {
							initialPersonalInformation = uiState.personalInformation
						}
						nameExpanded = !nameExpanded
					},
					colors = CardDefaults.cardColors(
						containerColor = Color.Transparent,
						disabledContainerColor = Color.Transparent,
						contentColor = MaterialTheme.subletrPalette.primaryTextColor,
						disabledContentColor = MaterialTheme.subletrPalette.darkerGrayButtonColor,
					),
					enabled = !genderExpanded,
				) {
					Column(
						verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxxxs)),
					) {
						Row(
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								modifier = Modifier.fillMaxWidth(0.9f),
								text = stringResource(id = R.string.full_name),
								style = accountHeadingSmallFont,
								color = if (!genderExpanded) MaterialTheme.subletrPalette.secondaryTextColor
								else MaterialTheme.subletrPalette.darkerGrayButtonColor,
							)
							AnimatedContent(
								targetState = nameExpanded,
								contentAlignment = Alignment.Center,
								transitionSpec = {
									scaleIn(animationSpec = tween(durationMillis = 150)) togetherWith
										scaleOut(animationSpec = tween(durationMillis = 150))
								}
							) { state ->
								Icon(
									painter = when (state) {
										true -> {
											painterResource(
												id = R.drawable.close_solid_black_24
											)
										}

										else -> {
											painterResource(
												id = R.drawable.edit_outline_black_24
											)
										}
									},
									contentDescription = stringResource(id = R.string.edit),
								)
							}
						}
						Text(
							text = if (!nameExpanded)
								"${uiState.personalInformation.firstName} ${uiState.personalInformation.lastName}"
							else stringResource(id = R.string.name_information_label),
							color =
							if (!genderExpanded) MaterialTheme.subletrPalette.primaryTextColor
							else MaterialTheme.subletrPalette.darkerGrayButtonColor,
						)
					}
				}
			}

			item {
				val density = LocalDensity.current
				AnimatedVisibility(
					visible = nameExpanded,
					enter = slideInVertically { with(density) { -40.dp.roundToPx() } }
						+ expandVertically(expandFrom = Alignment.Top)
						+ fadeIn(initialAlpha = 0.3f),
					exit = shrinkVertically() + fadeOut(),
				) {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = dimensionResource(id = R.dimen.s))
					) {
						SquaredTextField(
							modifier = Modifier
								.fillMaxWidth()
								.border(
									width = dimensionResource(id = R.dimen.xxxxs),
									color = MaterialTheme.subletrPalette.textFieldBorderColor,
									shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
								),
							label = {
								Text(
									text = stringResource(id = R.string.first_name),
									color = MaterialTheme.subletrPalette.secondaryTextColor,
								)
							},
							value = uiState.personalInformation.firstName,
							onValueChange = {
								viewModel.personalInformationStream.onNext(
									AccountPageUiState.PersonalInformation(
										lastName = uiState.personalInformation.lastName,
										firstName = it,
										gender = uiState.personalInformation.gender,
									)
								)
							},
						)
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

						SquaredTextField(
							modifier = Modifier
								.fillMaxWidth()
								.border(
									width = dimensionResource(id = R.dimen.xxxxs),
									color = MaterialTheme.subletrPalette.textFieldBorderColor,
									shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs))
								),
							label = {
								Text(
									text = stringResource(id = R.string.last_name),
									color = MaterialTheme.subletrPalette.secondaryTextColor,
								)
							},
							value = uiState.personalInformation.lastName,
							onValueChange = {
								viewModel.personalInformationStream.onNext(
									AccountPageUiState.PersonalInformation(
										lastName = it,
										firstName = uiState.personalInformation.firstName,
										gender = uiState.personalInformation.gender,
									)
								)
							},
						)
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

						SaveCancelButtonsRow(
							onSaveClick = {
								viewModel.userUpdateStream.onNext(uiState)
								nameExpanded = !nameExpanded
							},
							onCancelClick = {
								viewModel.personalInformationStream.onNext(
									initialPersonalInformation
								)
								nameExpanded = !nameExpanded
							},
						)

						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

						PageDivider()
					}
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				Card(
					modifier = Modifier
						.fillMaxWidth(),
					onClick = {
						if (genderExpanded) {
							viewModel.personalInformationStream.onNext(initialPersonalInformation)
						} else {
							initialPersonalInformation = uiState.personalInformation
						}
						genderExpanded = !genderExpanded
					},
					colors = CardDefaults.cardColors(
						containerColor = Color.Transparent,
						disabledContainerColor = Color.Transparent,
						contentColor = MaterialTheme.subletrPalette.primaryTextColor,
						disabledContentColor = MaterialTheme.subletrPalette.darkerGrayButtonColor,
					),
					enabled = !nameExpanded,
				) {
					Column(
						verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxxxs)),
					) {
						Row(
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								modifier = Modifier.fillMaxWidth(0.9f),
								text = stringResource(id = R.string.gender),
								style = accountHeadingSmallFont,
								color = if (!nameExpanded) MaterialTheme.subletrPalette.secondaryTextColor
								else MaterialTheme.subletrPalette.darkerGrayButtonColor,
							)
							AnimatedContent(
								targetState = genderExpanded,
								contentAlignment = Alignment.Center,
								transitionSpec = {
									scaleIn(animationSpec = tween(durationMillis = 150)) togetherWith
										scaleOut(animationSpec = tween(durationMillis = 150))
								}
							) { state ->
								Icon(
									painter = when (state) {
										true -> {
											painterResource(
												id = R.drawable.close_solid_black_24
											)
										}

										else -> {
											painterResource(
												id = R.drawable.edit_outline_black_24
											)
										}
									},
									contentDescription = stringResource(id = R.string.edit),
								)
							}
						}
						Text(
							text = if (!genderExpanded) uiState.personalInformation.gender
							else stringResource(id = R.string.gender_select_label),
							color =
							if (!nameExpanded) MaterialTheme.subletrPalette.primaryTextColor
							else MaterialTheme.subletrPalette.darkerGrayButtonColor,
						)
					}

				}
			}

			item {
				val density = LocalDensity.current
				AnimatedVisibility(
					visible = genderExpanded,
					enter = slideInVertically { with(density) { -40.dp.roundToPx() } }
						+ expandVertically(expandFrom = Alignment.Top)
						+ fadeIn(initialAlpha = 0.3f),
					exit = shrinkVertically() + fadeOut(),
				) {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(top = dimensionResource(id = R.dimen.s))
					) {
						GenderRadioButtons(
							uiState.personalInformation.gender,
							onChangeState = {
								viewModel.personalInformationStream.onNext(
									AccountPageUiState.PersonalInformation(
										lastName = uiState.personalInformation.lastName,
										firstName = uiState.personalInformation.firstName,
										gender = it,
									)
								)
							},
						)

						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

						SaveCancelButtonsRow(
							onSaveClick = {
								viewModel.userUpdateStream.onNext(uiState)
								genderExpanded = !genderExpanded
							},
							onCancelClick = {
								viewModel.personalInformationStream.onNext(
									initialPersonalInformation
								)
								genderExpanded = !genderExpanded
							},
						)

						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

						PageDivider()
					}
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				Card(
					modifier = Modifier
						.fillMaxWidth(),
					onClick = {
						viewModel.navHostController.navigate(
							NavigationDestination.CHANGE_PASSWORD.rootNavPath,
						)
					},
					colors = CardDefaults.cardColors(
						containerColor = Color.Transparent,
						disabledContainerColor = Color.Transparent,
						contentColor = MaterialTheme.subletrPalette.primaryTextColor,
						disabledContentColor = MaterialTheme.subletrPalette.darkerGrayButtonColor,
					),
					enabled = !nameExpanded && !genderExpanded,
				) {
					Row(
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Column(
							verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxxxs)),
							modifier = Modifier.fillMaxWidth(0.9f),
						) {
							Text(
								text = stringResource(id = R.string.password),
								style = accountHeadingSmallFont,
								color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.secondaryTextColor
								else MaterialTheme.subletrPalette.darkerGrayButtonColor,
							)
							Text(
								text = stringResource(id = R.string.change_password),
								color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.primaryTextColor
								else MaterialTheme.subletrPalette.darkerGrayButtonColor,
							)
						}
						Icon(
							painter = painterResource(
								id = R.drawable.arrow_right_solid_black_24
							),
							contentDescription = stringResource(id = R.string.change_password),
						)
					}
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				PageDivider()
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Start
				) {
					Text(
						text = stringResource(id = R.string.my_sublet),
						style = SubletrTypography.displaySmall,
						color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.secondaryTextColor
						else MaterialTheme.subletrPalette.darkerGrayButtonColor,
					)
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				Card(
					modifier = Modifier.fillMaxWidth(),
					shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
					elevation = CardDefaults.elevatedCardElevation(
						defaultElevation = dimensionResource(id = R.dimen.xxs),
					),
					colors = CardDefaults.cardColors(
						containerColor = MaterialTheme.subletrPalette.squaredTextFieldBackgroundColor,
						disabledContainerColor = MaterialTheme.subletrPalette.secondaryBackgroundColor,
						contentColor = MaterialTheme.subletrPalette.primaryTextColor,
						disabledContentColor = MaterialTheme.subletrPalette.darkerGrayButtonColor,
					),
				) {
					if (uiState.listingId == null) {
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(dimensionResource(id = R.dimen.s)),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically,
						) {
							Column(
								verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
								modifier = Modifier.fillMaxWidth(),
							) {
								Text(
									text = stringResource(id = R.string.sublet_your_place),
									style = SubletrTypography.displayLarge,
									color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.primaryTextColor
									else MaterialTheme.subletrPalette.darkerGrayButtonColor,
								)
								Text(
									text = stringResource(id = R.string.sublet_your_place_label),
									color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.primaryTextColor
									else MaterialTheme.subletrPalette.darkerGrayButtonColor,
								)
								Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
								PrimaryButton(
									modifier = Modifier
										.fillMaxWidth()
										.align(Alignment.Start),
									shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
									onClick = {
										viewModel.navHostController.navigate(
											NavigationDestination.CREATE_LISTING.fullNavPath
										)
									},
									enabled = !nameExpanded && !genderExpanded,
								) {
									Text(
										stringResource(id = R.string.create_listing),
										color = MaterialTheme.subletrPalette.textOnSubletrPink,
									)
								}
							}
						}
					} else if (uiState.listingDetails != null) {
						Column(
							modifier = Modifier
								.fillMaxWidth()
								.padding(
									start = dimensionResource(id = R.dimen.s),
									top = dimensionResource(id = R.dimen.s),
									bottom = dimensionResource(id = R.dimen.s),
									end = dimensionResource(id = R.dimen.xs),
								),
							verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
						) {
							Row(
								modifier = Modifier
									.fillMaxWidth(1.0f),
							) {
								if (uiState.listingImage != null) {
									Image(
										modifier = Modifier
											.height(dimensionResource(id = R.dimen.xxxl))
											.width(dimensionResource(id = R.dimen.xxxl)),
										bitmap = uiState.listingImage.asImageBitmap(),
										contentDescription = stringResource(id = R.string.listing_image),
										contentScale = ContentScale.Crop,
										alpha = if (!nameExpanded && !genderExpanded) 1.0f else 0.3F,
									)
								} else {
									Image(
										modifier = Modifier
											.height(dimensionResource(id = R.dimen.xxxl))
											.width(dimensionResource(id = R.dimen.xxxl)),
										painter = painterResource(
											id = R.drawable.default_listing_image,
										),
										contentDescription = stringResource(id = R.string.listing_image),
										contentScale = ContentScale.Crop,
										alpha = if (!nameExpanded && !genderExpanded) 1.0f else 0.3F,
									)
								}
								Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.s)))
								Column(
									modifier = Modifier
										.fillMaxWidth(1.0f),
									horizontalAlignment = Alignment.Start,
									verticalArrangement = Arrangement.spacedBy(
										dimensionResource(id = R.dimen.xxxs)
									),

									) {
									Text(
										uiState.listingDetails.address,
										style = listingTitleFont,
										overflow = TextOverflow.Clip,
										textAlign = TextAlign.Start,
										maxLines = 1,
										color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.primaryTextColor
										else MaterialTheme.subletrPalette.darkerGrayButtonColor,
									)
									Text(
										stringResource(
											id = R.string.dollar_sign_n,
											uiState.listingDetails.price,
										),
										style = listingTitleFont,
										color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.primaryTextColor
										else MaterialTheme.subletrPalette.darkerGrayButtonColor,
									)
									Text(
										text = stringResource(
											id = R.string.short_for_all_date_range,
											dateTimeFormatter(uiState.listingDetails.leaseStart),
											dateTimeFormatter(offsetDateTime = uiState.listingDetails.leaseEnd),
										),
										style = MaterialTheme.typography.bodyLarge,
										color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.primaryTextColor
										else MaterialTheme.subletrPalette.darkerGrayButtonColor,
									)
								}
							}
						}
					}
				}
			}

			if (uiState.listingId != null) {
				item {
					Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
				}

				item {
					PrimaryButton(
						modifier = Modifier
							.fillMaxWidth()
							.height(dimensionResource(id = R.dimen.xl)),
						onClick = {
							// TODO: navigate to my sublet page
						},
						enabled = !nameExpanded && !genderExpanded,
					) {
						Text(
							stringResource(id = R.string.manage_my_sublet),
							color = MaterialTheme.subletrPalette.textOnSubletrPink,
						)
					}
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				PageDivider()
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Start
				) {
					Text(
						text = stringResource(id = R.string.display),
						style = SubletrTypography.displaySmall,
						color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.secondaryTextColor
						else MaterialTheme.subletrPalette.darkerGrayButtonColor,
					)
				}
			}

			item {
				Row(
					modifier = Modifier
						.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(id = R.string.use_device_settings),
						color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.primaryTextColor
						else MaterialTheme.subletrPalette.darkerGrayButtonColor,
					)
					PrimarySwitch(
						checked = uiState.settings.useDeviceTheme,
						onCheckedChange = {
							viewModel.settingsStream.onNext(
								AccountPageUiState.Settings(
									useDeviceTheme = it,
									useDarkMode = uiState.settings.useDarkMode,
									allowChatNotifications = uiState.settings.allowChatNotifications,
								)
							)
							viewModel.setDefaultDisplayTheme(it)
						},
						enabled = !nameExpanded && !genderExpanded,
					)
				}
			}

			item {
				Row(
					modifier = Modifier
						.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(id = R.string.dark_mode),
						color = if (!nameExpanded && !genderExpanded && !uiState.settings.useDeviceTheme)
							MaterialTheme.subletrPalette.primaryTextColor
						else MaterialTheme.subletrPalette.darkerGrayButtonColor,
					)
					PrimarySwitch(
						checked = uiState.settings.useDarkMode,
						onCheckedChange = {
							viewModel.settingsStream.onNext(
								AccountPageUiState.Settings(
									useDeviceTheme = uiState.settings.useDeviceTheme,
									useDarkMode = it,
									allowChatNotifications = uiState.settings.allowChatNotifications,
								)
							)
							viewModel.setDisplayMode(it)
						},
						enabled = !nameExpanded && !genderExpanded && !uiState.settings.useDeviceTheme,
					)
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				PageDivider()
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Start
				) {
					Text(
						text = stringResource(id = R.string.notifications),
						style = SubletrTypography.displaySmall,
						color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.secondaryTextColor
						else MaterialTheme.subletrPalette.darkerGrayButtonColor,
					)
				}
			}

			item {
				Row(
					modifier = Modifier
						.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(id = R.string.allow_chat_notifications),
						color = if (!nameExpanded && !genderExpanded) MaterialTheme.subletrPalette.primaryTextColor
						else MaterialTheme.subletrPalette.darkerGrayButtonColor,
					)
					PrimarySwitch(
						checked = uiState.settings.allowChatNotifications,
						onCheckedChange = {
							viewModel.settingsStream.onNext(
								AccountPageUiState.Settings(
									useDeviceTheme = uiState.settings.useDeviceTheme,
									useDarkMode = uiState.settings.useDarkMode,
									allowChatNotifications = it,
								)
							)
							viewModel.setChatNotifications(it)
						},
						enabled = !nameExpanded && !genderExpanded,
					)
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))
			}

			item {
				PrimaryButton(
					modifier = Modifier
						.fillMaxWidth()
						.height(dimensionResource(id = R.dimen.xl)),
					onClick = {
						viewModel.logout()
					},
					enabled = !nameExpanded && !genderExpanded
				) {
					Text(
						text = stringResource(id = R.string.log_out),
						color = MaterialTheme.subletrPalette.textOnSubletrPink,
					)
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}
		}
	}
}

@Composable
fun PageDivider() {
	Divider(
		modifier = Modifier.fillMaxWidth(),
		thickness = dimensionResource(id = R.dimen.xxxxs),
		color = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
	)
}

@Composable
fun SaveCancelButtonsRow(onSaveClick: () -> Unit, onCancelClick: () -> Unit) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.m)),
		verticalAlignment = Alignment.CenterVertically,
	) {
		SecondaryButton(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.height(dimensionResource(id = R.dimen.xl)),
			shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
			onClick = onCancelClick,
		) {
			Text(
				stringResource(
					id = R.string.cancel,
				),
				style = filterBoldFont,
			)
		}
		PrimaryButton(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.height(dimensionResource(id = R.dimen.xl)),
			shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
			onClick = onSaveClick,
		) {
			Text(
				stringResource(
					id = R.string.save,
				),
				color = MaterialTheme.subletrPalette.textOnSubletrPink,
				style = filterBoldFont,
			)
		}
	}
}

@Composable
fun GenderRadioButtons(selectedValue: String, onChangeState: (String) -> Unit) {
	val options = listOf(
		stringResource(id = Gender.MALE.stringId),
		stringResource(id = Gender.FEMALE.stringId),
		stringResource(id = Gender.UNKNOWN.stringId),
		stringResource(id = Gender.OTHER.stringId)
	)

	val other = stringResource(id = Gender.OTHER.stringId)
	val isSelectedItem: (String) -> Boolean = {
		if (options.contains(selectedValue)) {
			selectedValue == it
		} else {
			other == it
		}
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(end = dimensionResource(id = R.dimen.xs)),
		verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
	) {
		options.forEach { option ->
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.selectable(
						selected = isSelectedItem(option),
						onClick = { onChangeState(option) },
					),
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
			) {
				Text(
					text = option,
					modifier = Modifier.fillMaxWidth(0.9f)
				)
				RadioButton(
					selected = isSelectedItem(option),
					onClick = null,
					colors = RadioButtonDefaults.colors(
						selectedColor = MaterialTheme.subletrPalette.subletrPink,
					)
				)
			}
		}

		val density = LocalDensity.current
		AnimatedVisibility(
			visible = isSelectedItem(other),
			enter = slideInVertically { with(density) { -40.dp.roundToPx() } }
				+ expandVertically(expandFrom = Alignment.Top)
				+ fadeIn(initialAlpha = 0.3f),
			exit = shrinkVertically() + fadeOut(),
		) {
			SquaredTextField(
				modifier = Modifier
					.fillMaxWidth()
					.border(
						dimensionResource(id = R.dimen.xxxxs),
						MaterialTheme.subletrPalette.textFieldBorderColor,
						RoundedCornerShape(dimensionResource(id = R.dimen.xs))
					),
				label = {
					Text(
						text = stringResource(id = R.string.other_gender),
						color = MaterialTheme.subletrPalette.secondaryTextColor,
					)
				},
				value = if (options.contains(selectedValue)) "" else selectedValue,
				onValueChange = {
					if (it == "") onChangeState(other)
					else onChangeState(it)
				},
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadAvatar(
	viewModel: AccountPageViewModel,
	isEnabled: Boolean,
	modifier: Modifier,
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

	Button(
		modifier = modifier,
		shape = CircleShape,
		contentPadding = PaddingValues(dimensionResource(id = R.dimen.xs)),
		onClick = {
			coroutineScope.launch {
				openImagePicker = true
			}
		},
		colors = ButtonDefaults.buttonColors(
			containerColor = MaterialTheme.subletrPalette.subletrPink,
			contentColor = MaterialTheme.subletrPalette.textOnSubletrPink,
			disabledContainerColor = MaterialTheme.subletrPalette.darkerGrayButtonColor,
		),
		enabled = isEnabled,
	) {
		Icon(
			modifier = Modifier.fillMaxSize(),
			painter = painterResource(
				id = R.drawable.edit_round_black_24
			),
			contentDescription = stringResource(id = R.string.upload_images),
			tint = MaterialTheme.subletrPalette.textOnSubletrPink
		)
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
		viewModel.updateAvatar(context, imageUri!!)
		imageUri = null
		hasImage = false
	}
}

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
				personalInformation = AccountPageUiState.PersonalInformation(
					lastName = "",
					firstName = "",
					gender = "",
				),
				settings = AccountPageUiState.Settings(
					useDeviceTheme = false,
					useDarkMode = false,
					allowChatNotifications = false,
				),
				listingId = null,
				listingDetails = null,
				listingImage = null,
				avatarBitmap = null,
			)
		)
	}
}

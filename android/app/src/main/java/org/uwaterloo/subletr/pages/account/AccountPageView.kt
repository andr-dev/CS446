package org.uwaterloo.subletr.pages.account

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.components.textfield.SquaredTextField
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.createlisting.CreateListingPageUiState
import org.uwaterloo.subletr.pages.home.list.dateTimeFormatter
import org.uwaterloo.subletr.theme.SubletrLightColorScheme
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.SubletrTypography
import org.uwaterloo.subletr.theme.accountHeadingSmallFont
import org.uwaterloo.subletr.theme.darkerGrayButtonColor
import org.uwaterloo.subletr.theme.filterBoldFont
import org.uwaterloo.subletr.theme.listingTitleFont
import org.uwaterloo.subletr.theme.primaryTextColor
import org.uwaterloo.subletr.theme.secondaryBackgroundColor
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.squaredTextFieldBackgroundColor
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.textFieldBackgroundColor
import org.uwaterloo.subletr.theme.textFieldBorderColor
import org.uwaterloo.subletr.theme.textOnSubletrPink
import org.uwaterloo.subletr.utils.ComposeFileProvider
import java.util.Optional

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
	val initialPersonalInformation = remember { mutableStateOf(
		AccountPageUiState.PersonalInformation(
			lastName = "",
			firstName = "",
			gender = "",
		)
	)}

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
				.padding(horizontal = dimensionResource(id = R.dimen.m))
				.fillMaxSize()
				.imePadding(),
			verticalArrangement = Arrangement.Top,
//			verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			// TODO IMAGE
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
						placeholder = painterResource(R.drawable.room),
						contentDescription = stringResource(R.string.avatar),
						contentScale = ContentScale.Crop,
						alpha = if (!nameExpanded && !genderExpanded) 1.0f else 0.3F,
					)
					UploadAvatar(
						viewModel = viewModel,
						uiState = uiState,
						isEnabled = !nameExpanded && !genderExpanded,
						modifier = Modifier
							.size(dimensionResource(id = R.dimen.l))
							.align(Alignment.BottomEnd),
					)
				}
			}
//			item {
//				Box(
//					modifier = Modifier
//						.padding(dimensionResource(id = R.dimen.l))
//				) {
//					Image(
//						modifier = Modifier
//							.size(dimensionResource(id = R.dimen.xxxxxl))
//							.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xxxxxl))),
//						painter = painterResource(
//							id = R.drawable.room,
//						),
//						contentDescription = stringResource(id = R.string.avatar),
//						contentScale = ContentScale.Crop,
//						alpha = if (!nameExpanded && !genderExpanded) 1.0f else 0.3F,
//					)
//					Button(
//						modifier = Modifier
//							.size(dimensionResource(id = R.dimen.l))
////							.padding(dimensionResource(id = R.dimen.xs))
//							.align(Alignment.BottomEnd),
//						shape = CircleShape,
//						contentPadding = PaddingValues(dimensionResource(id = R.dimen.xs)),
//						onClick = {}, //TODO
//						colors = ButtonDefaults.buttonColors(
//							containerColor = subletrPink,
//							contentColor = textOnSubletrPink,
//							disabledContainerColor = darkerGrayButtonColor,
//						),
//						enabled = !nameExpanded && !genderExpanded,
//					) {
//						Icon(
//							modifier = Modifier.fillMaxSize(),
//							painter = painterResource(
//								id = R.drawable.edit_round_black_24
//							),
//							contentDescription = stringResource(id = R.string.upload_images),
//							tint = textOnSubletrPink
//						)
//					}
//				}
//			}

			item {
				Card(
					modifier = Modifier.fillMaxWidth(),
					shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
					elevation = CardDefaults.elevatedCardElevation(
						defaultElevation = dimensionResource(id = R.dimen.xxs),
					),
					onClick = {
						// TODO
					},
					colors = CardDefaults.cardColors(
						containerColor = squaredTextFieldBackgroundColor,
						disabledContainerColor = secondaryBackgroundColor,
						contentColor = primaryTextColor,
						disabledContentColor = darkerGrayButtonColor,
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
							color = if (!nameExpanded && !genderExpanded) primaryTextColor else darkerGrayButtonColor
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
				Divider(
					modifier = Modifier.fillMaxWidth(),
					thickness = dimensionResource(id = R.dimen.xxxxs),
					color = secondaryButtonBackgroundColor,
				)
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
						color = if (!nameExpanded && !genderExpanded) secondaryTextColor else darkerGrayButtonColor,
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
							viewModel.personalInformationStream.onNext(initialPersonalInformation.value)
						} else {
							initialPersonalInformation.value = uiState.personalInformation
						}
						nameExpanded = !nameExpanded
				  	},
					colors = CardDefaults.cardColors(
						containerColor = Color.Transparent,
						disabledContainerColor = Color.Transparent,
						contentColor = primaryTextColor,
						disabledContentColor = darkerGrayButtonColor,
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
								color = if (!genderExpanded) secondaryTextColor else darkerGrayButtonColor,
							)
							AnimatedContent(
								targetState = nameExpanded,
								contentAlignment = Alignment.Center,
								transitionSpec  = {
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
							text = if (!nameExpanded) "${uiState.personalInformation.firstName} ${uiState.personalInformation.lastName}" else "Enter the name that matches your student ID",
							color = if (!genderExpanded) primaryTextColor else darkerGrayButtonColor,
						)
					}
				}
			}

			item {
				val density = LocalDensity.current
				AnimatedVisibility(
					visible = nameExpanded,
					enter = slideInVertically {
						// Slide in from 40 dp from the top.
						with(density) { -40.dp.roundToPx() }
					} + expandVertically(
						// Expand from the top.
						expandFrom = Alignment.Top
					) + fadeIn(
						// Fade in with the initial alpha of 0.3f.
						initialAlpha = 0.3f
					),
					exit = shrinkVertically() + fadeOut(),
				)  {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = dimensionResource(id = R.dimen.s))
					) {
						SquaredTextField(
							modifier = Modifier
								.fillMaxWidth()
								.border(
									dimensionResource(id = R.dimen.xxxxs),
									textFieldBorderColor,
									RoundedCornerShape(dimensionResource(id = R.dimen.xs))
								),
							label = {
								Text(
									text = stringResource(id = R.string.first_name),
									color = secondaryTextColor,
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
									dimensionResource(id = R.dimen.xxxxs),
									textFieldBorderColor,
									RoundedCornerShape(dimensionResource(id = R.dimen.xs))
								),
							label = {
								Text(
									text = stringResource(id = R.string.last_name),
									color = secondaryTextColor,
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
								viewModel.personalInformationStream.onNext(initialPersonalInformation.value)
								nameExpanded = !nameExpanded
							},
						)

						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

						Divider(
							modifier = Modifier.fillMaxWidth(),
							thickness = dimensionResource(id = R.dimen.xxxxs),
							color = secondaryButtonBackgroundColor,
						)
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
							viewModel.personalInformationStream.onNext(initialPersonalInformation.value)
						} else {
							initialPersonalInformation.value = uiState.personalInformation
						}
						genderExpanded = !genderExpanded
				  	},
					colors = CardDefaults.cardColors( // TODO
						containerColor = Color.Transparent,
						disabledContainerColor = Color.Transparent,
						contentColor = primaryTextColor,
						disabledContentColor = darkerGrayButtonColor,
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
								color = if (!nameExpanded) secondaryTextColor else darkerGrayButtonColor,
							)
							AnimatedContent(
								targetState = genderExpanded,
								contentAlignment = Alignment.Center,
								transitionSpec  = {
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
							text = if (!genderExpanded) uiState.personalInformation.gender else "Select your gender",
							color = if (!nameExpanded) primaryTextColor else darkerGrayButtonColor,
						)
					}

				}
			}

			item {
				val density = LocalDensity.current
				AnimatedVisibility(
					visible = genderExpanded,
					enter = slideInVertically {
						// Slide in from 40 dp from the top.
						with(density) { -40.dp.roundToPx() }
					} + expandVertically(
						// Expand from the top.
						expandFrom = Alignment.Top
					) + fadeIn(
						// Fade in with the initial alpha of 0.3f.
						initialAlpha = 0.3f
					),
					exit = shrinkVertically() + fadeOut(),
				) {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(top = 16.dp)
					) {
						GenderRadioButtons(
							uiState.personalInformation.gender,
							onChangeState = {
//								viewModel.genderStream.onNext(it)
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
								viewModel.personalInformationStream.onNext(initialPersonalInformation.value)
								genderExpanded = !genderExpanded
							},
						)

						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

						Divider(
							modifier = Modifier.fillMaxWidth(),
							thickness = dimensionResource(id = R.dimen.xxxxs),
							color = secondaryButtonBackgroundColor,
						)
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
						contentColor = primaryTextColor,
						disabledContentColor = darkerGrayButtonColor,
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
								color = if (!nameExpanded && !genderExpanded) secondaryTextColor else darkerGrayButtonColor,
							)
							Text(
								text = stringResource(id = R.string.change_password),
								color = if (!nameExpanded && !genderExpanded) primaryTextColor else darkerGrayButtonColor,
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
				Divider(
					modifier = Modifier.fillMaxWidth(),
					thickness = dimensionResource(id = R.dimen.xxxxs),
					color = secondaryButtonBackgroundColor,
				)
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
						color = if (!nameExpanded && !genderExpanded) secondaryTextColor else darkerGrayButtonColor,
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
						containerColor = squaredTextFieldBackgroundColor,
						disabledContainerColor = secondaryBackgroundColor,
						contentColor = primaryTextColor,
						disabledContentColor = darkerGrayButtonColor,
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
									text = "Sublet your place",
									style = SubletrTypography.displayLarge,
									color = if (!nameExpanded && !genderExpanded) primaryTextColor else darkerGrayButtonColor,
								)
								Text(
									text = "Start subletting your apartment to the Waterloo community",
									color = if (!nameExpanded && !genderExpanded) primaryTextColor else darkerGrayButtonColor,
								)
								Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
								PrimaryButton(
									modifier = Modifier
										.fillMaxWidth()
										.align(Alignment.Start),
									shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
									onClick = {
										viewModel.navHostController.navigate(NavigationDestination.CREATE_LISTING.fullNavPath)
									},
									enabled = !nameExpanded && !genderExpanded,
								) {
									Text(
										stringResource(id = R.string.create_listing),
										color = textOnSubletrPink,
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
								Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.s)),)
								Column(
									modifier = Modifier
										.fillMaxWidth(1.0f),
									horizontalAlignment = Alignment.Start,
									verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxxs)),

									) {
									Text(
										uiState.listingDetails.address,
										style = listingTitleFont,
										overflow = TextOverflow.Clip,
										textAlign = TextAlign.Start,
										maxLines = 1,
										color = if (!nameExpanded && !genderExpanded) primaryTextColor else darkerGrayButtonColor,
									)
									Text(
										stringResource(
											id = R.string.dollar_sign_n,
											uiState.listingDetails.price,
										),
										style = listingTitleFont,
										color = if (!nameExpanded && !genderExpanded) primaryTextColor else darkerGrayButtonColor,
									)
									Text(
										text = stringResource(
											id = R.string.short_for_all_date_range,
											dateTimeFormatter(uiState.listingDetails.leaseStart),
											dateTimeFormatter(offsetDateTime = uiState.listingDetails.leaseEnd),
										),
										style = MaterialTheme.typography.bodyLarge,
										color = if (!nameExpanded && !genderExpanded) primaryTextColor else darkerGrayButtonColor,
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
//						shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
						onClick = { }, // TODO
						enabled = !nameExpanded && !genderExpanded,
					) {
						Text(
							stringResource(id = R.string.manage_my_sublet),
							color = textOnSubletrPink,
						)
					}
				}
			}

			item {
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				Divider(
					modifier = Modifier.fillMaxWidth(),
					thickness = dimensionResource(id = R.dimen.xxxxs),
					color = secondaryButtonBackgroundColor,
				)
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
						color = if (!nameExpanded && !genderExpanded) secondaryTextColor else darkerGrayButtonColor,
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
						color = if (!nameExpanded && !genderExpanded) primaryTextColor else darkerGrayButtonColor,
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
						color = if (!nameExpanded && !genderExpanded && !uiState.settings.useDeviceTheme) primaryTextColor else darkerGrayButtonColor,
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
				Divider(
					modifier = Modifier.fillMaxWidth(),
					thickness = dimensionResource(id = R.dimen.xxxxs),
					color = secondaryButtonBackgroundColor,
				)
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
						color = if (!nameExpanded && !genderExpanded) secondaryTextColor else darkerGrayButtonColor,
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
						color = if (!nameExpanded && !genderExpanded) primaryTextColor else darkerGrayButtonColor,
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
						color = textOnSubletrPink,
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
				color = textOnSubletrPink,
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
		stringResource(id = Gender.OTHER.stringId))

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
						selectedColor = subletrPink,
					)
				)
			}
		}

		val density = LocalDensity.current
		AnimatedVisibility(
			visible = isSelectedItem(other),
			enter = slideInVertically {
				// Slide in from 40 dp from the top.
				with(density) { -40.dp.roundToPx() }
			} + expandVertically(
				// Expand from the top.
				expandFrom = Alignment.Top
			) + fadeIn(
				// Fade in with the initial alpha of 0.3f.
				initialAlpha = 0.3f
			),
			exit = shrinkVertically() + fadeOut(),
		)  {
			SquaredTextField(
				modifier = Modifier
					.fillMaxWidth()
					.border(
						dimensionResource(id = R.dimen.xxxxs),
						textFieldBorderColor,
						RoundedCornerShape(dimensionResource(id = R.dimen.xs))
					),
				label = {
					Text(
						text = stringResource(id = R.string.other_gender),
						color = secondaryTextColor,
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
	uiState: AccountPageUiState.Loaded,
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
	var avatarBitmap = uiState.avatarBitmap


//		if (avatarBitmap != null) {
//			Image(
//				modifier = Modifier
//					.size(dimensionResource(id = R.dimen.xxxxxl))
//					.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xxxxxl))),
//				bitmap = avatarBitmap!!.asImageBitmap(),
//				contentDescription = stringResource(id = R.string.avatar),
//				contentScale = ContentScale.Crop,
//				alpha = if (isEnabled) 1.0f else 0.3F,
//			)
//		} else {
//			Image(
//				modifier = Modifier
//					.size(dimensionResource(id = R.dimen.xxxxxl))
//					.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xxxxxl))),
//				painter = painterResource(
//					id = R.drawable.room,
//				),
//				contentDescription = stringResource(id = R.string.avatar),
//				contentScale = ContentScale.Crop,
//				alpha = if (isEnabled) 1.0f else 0.3F,
//			)
//		}
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
			containerColor = subletrPink,
			contentColor = textOnSubletrPink,
			disabledContainerColor = darkerGrayButtonColor,
		),
		enabled = isEnabled,
	) {
		Icon(
			modifier = Modifier.fillMaxSize(),
			painter = painterResource(
				id = R.drawable.edit_round_black_24
			),
			contentDescription = stringResource(id = R.string.upload_images),
			tint = textOnSubletrPink
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
//		if (Build.VERSION.SDK_INT < 28) {
//			avatarBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
//			viewModel.newAvatarBitmapStream.onNext(
//				Optional.of(avatarBitmap)
//			)
//		} else {
//			val source = ImageDecoder
//				.createSource(context.contentResolver, imageUri!!)
//			avatarBitmap = ImageDecoder.decodeBitmap(source)
//			viewModel.newAvatarBitmapStream.onNext(Optional.of(avatarBitmap))
//		}
		viewModel.userUpdateStream.onNext(uiState)
		imageUri = null
		hasImage = false
	}
	Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)))

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
				newAvatarBitmap = null,
				newAvatarString = null,
			)
		)
	}
}


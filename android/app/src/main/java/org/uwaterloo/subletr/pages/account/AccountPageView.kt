package org.uwaterloo.subletr.pages.account

import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
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
	val initialFirstName = remember { mutableStateOf("") }
	val initialLastName = remember { mutableStateOf("") }
	val initialGender = remember { mutableStateOf("") }

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
//						.clip(CircleShape),
				) {
					Image(
						modifier = Modifier
							.size(dimensionResource(id = R.dimen.xxxxxl))
							.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xxxxxl))),
						painter = painterResource(
							id = R.drawable.room,
						),
						contentDescription = stringResource(id = R.string.avatar),
						contentScale = ContentScale.Crop,
						alpha = if (!nameExpanded && !genderExpanded) 1.0f else 0.3F,
					)
					Button(
						modifier = Modifier
							.size(dimensionResource(id = R.dimen.l))
//							.padding(dimensionResource(id = R.dimen.xs))
							.align(Alignment.BottomEnd),
						shape = CircleShape,
						contentPadding = PaddingValues(dimensionResource(id = R.dimen.xs)),
						onClick = {}, //TODO
						colors = ButtonDefaults.buttonColors(
							containerColor = subletrPink,
							contentColor = textOnSubletrPink,
						),
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
						// TODO
					},
					colors = CardDefaults.cardColors(
						containerColor = squaredTextFieldBackgroundColor,
						disabledContainerColor = secondaryBackgroundColor,
						contentColor = primaryTextColor,
						disabledContentColor = darkerGrayButtonColor,
					),
					enabled = !nameExpanded && !genderExpanded, // TODO
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
							// TODO: disabled color
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
				Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
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
							viewModel.firstNameStream.onNext(initialFirstName.value)
							viewModel.lastNameStream.onNext(initialLastName.value)
						} else {
							initialFirstName.value = uiState.firstName
							initialLastName.value = uiState.lastName
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
							text = if (!nameExpanded) "${uiState.firstName} ${uiState.lastName}" else "Enter the name that matches your student ID",
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
//							.background(Color.Black)
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
							value = uiState.firstName,
							onValueChange = {
								viewModel.firstNameStream.onNext(it)
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
							value = uiState.lastName,
							onValueChange = {
								viewModel.lastNameStream.onNext(it)
							},
						)
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

						SaveCancelButtonsRow(
							onSaveClick = {
								viewModel.userUpdateStream.onNext(uiState)
								nameExpanded = !nameExpanded
							},
							onCancelClick = {
								viewModel.lastNameStream.onNext(initialLastName.value)
								viewModel.firstNameStream.onNext(initialFirstName.value)
								nameExpanded = !nameExpanded
							},
						)

						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

						Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
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
							viewModel.genderStream.onNext(initialGender.value)
						} else {
							initialGender.value = uiState.gender
						}
						genderExpanded = !genderExpanded
				  	},
					colors = CardDefaults.cardColors( // TODO
						containerColor = Color.Transparent,
						disabledContainerColor = Color.Transparent,
						contentColor = primaryTextColor,
						disabledContentColor = darkerGrayButtonColor,
					),
					enabled = !nameExpanded, // TODO
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
							text = if (!genderExpanded) uiState.gender else "Select your gender",
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
						GenderRadioButtons(uiState.gender, onChangeState = { viewModel.genderStream.onNext(it) } )

						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

						SaveCancelButtonsRow(
							onSaveClick = {
								viewModel.userUpdateStream.onNext(uiState)
								genderExpanded = !genderExpanded
							},
							onCancelClick = {
								viewModel.genderStream.onNext(initialGender.value)
								genderExpanded = !genderExpanded
							},
						)

						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))

						Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
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
				Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
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
					onClick = {
						Log.d("here", "${uiState.listingId}")
					},
					colors = CardDefaults.cardColors(
						containerColor = squaredTextFieldBackgroundColor,
						disabledContainerColor = secondaryBackgroundColor,
						contentColor = primaryTextColor,
						disabledContentColor = darkerGrayButtonColor,
					),
					enabled = !nameExpanded && !genderExpanded,
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
	//									.height(dimensionResource(id = R.dimen.l)),
									shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
									onClick = { }, // TODO
									enabled = !nameExpanded && !genderExpanded,
								) {
									Text(
										stringResource(id = R.string.create_listing),
										color = textOnSubletrPink,
	//									style = filterBoldFont,
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
											id = R.drawable.room,
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
				Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
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
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

			item {
				Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
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
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
			}

//			item {
//				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))
//			}
//			itemsIndexed(
//				items = uiState.settings,
//			) { itemIndex, item ->
//				Row(
//					modifier = Modifier
//						.fillMaxWidth(ELEMENT_WIDTH),
//					verticalAlignment = Alignment.CenterVertically,
//					horizontalArrangement = Arrangement.SpaceBetween,
//				) {
//					Text(
//						text = stringResource(id = item.textStringId),
//					)
//					PrimarySwitch(
//						checked = item.toggleState,
//						onCheckedChange = {
////							viewModel.updateUiState(
////								uiState = AccountPageUiState.Loaded(
////									lastName = uiState.lastName,
////									firstName = uiState.firstName,
////									gender = uiState.gender,
////									settings = uiState.settings.mapIndexed { mapIndex, setting ->
////										if (mapIndex == itemIndex) {
////											AccountPageUiState.Setting(
////												textStringId = setting.textStringId,
////												it,
////											)
////										}
////										else {
////											setting
////										}
////									}
////								)
////							)
//						}
//					)
//				}
//			}

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
				Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))
			}
		}
	}
}

private const val ELEMENT_WIDTH = 0.75f

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
				gender = "",
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
				listingId = null,
				listingDetails = null,
				listingImage = null,
			)
		)
	}
}


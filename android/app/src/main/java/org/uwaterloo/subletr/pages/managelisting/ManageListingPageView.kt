package org.uwaterloo.subletr.pages.managelisting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.api.models.UpdateListingRequest
import org.uwaterloo.subletr.components.bottomsheet.DatePickerBottomSheet
import org.uwaterloo.subletr.components.button.DateInputButton
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.components.dropdown.RoundedExposedDropdown
import org.uwaterloo.subletr.components.subletdetailsimage.SubletDetailsImageDisplay
import org.uwaterloo.subletr.components.textfield.NumericalInputTextField
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.enums.EnsuiteBathroomOption
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.enums.ListingForGenderOption
import org.uwaterloo.subletr.enums.getGender
import org.uwaterloo.subletr.enums.getKey
import org.uwaterloo.subletr.enums.toHousingType
import org.uwaterloo.subletr.enums.toResidenceType
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.SubletrTypography
import org.uwaterloo.subletr.theme.subletrPalette
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ManageListingPageView(
	modifier: Modifier = Modifier,
	viewModel: ManageListingPageViewModel = hiltViewModel(),
	uiState: ManageListingPageUiState = viewModel.uiStateStream.subscribeAsState(
		ManageListingPageUiState.Loading
	).value,
) {
	val scrollState = rememberScrollState()
	val coroutineScope = rememberCoroutineScope()
	val openDatePicker = rememberSaveable { mutableStateOf(false) }
	val openDeleteDialog = rememberSaveable { mutableStateOf(false) }
	val numberPattern = remember { Regex("^\\d+\$") }
	var attemptCreate by remember { mutableStateOf(false) }

	val snackbarHostState = remember { SnackbarHostState() }


	Scaffold(
		modifier = modifier,
		topBar = {
			Row(
				modifier = Modifier
					.padding(
						top = dimensionResource(id = R.dimen.s)
					),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				IconButton(
					modifier = Modifier,
					onClick = {
						viewModel.navigationService.navHostController.popBackStack()
					},
				) {
					Icon(
						painter = painterResource(
							id = R.drawable.arrow_back_solid_black_24
						),
						contentDescription = stringResource(id = R.string.back_arrow),
					)
				}
				Text(
					text = stringResource(id = R.string.your_listing),
					style = MaterialTheme.typography.titleSmall,
					color = MaterialTheme.subletrPalette.primaryTextColor,
				)
			}
		},

		snackbarHost = {
			SnackbarHost(snackbarHostState) {
				Snackbar(
					containerColor = MaterialTheme.subletrPalette.bottomSheetColor,
					contentColor = MaterialTheme.subletrPalette.primaryTextColor,
				) {
					Text(
						text = it.visuals.message,
						style = SubletrTypography.bodyLarge,
					)
				}
			}
	   	},

		) { paddingValues ->
		if (uiState is ManageListingPageUiState.Loading) {
			Column(
				modifier = Modifier
					.padding(paddingValues = paddingValues)
					.fillMaxSize()
					.imePadding(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center,
			) {
				CircularProgressIndicator()
			}
		} else if (uiState is ManageListingPageUiState.Loaded) {

			val imageCount = uiState.images.size
			val pagerState = rememberPagerState(
				initialPage = 0,
				initialPageOffsetFraction = 0f,
			) { imageCount }


			Column(
				modifier = Modifier
					.padding(paddingValues)
					.fillMaxSize()
					.verticalScroll(state = scrollState)
					.padding(horizontal = dimensionResource(id = R.dimen.s)),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Box(
					modifier = Modifier
				) {
					if (uiState.isFetchingImages) {
						Box(
							modifier = Modifier
								.padding(dimensionResource(id = R.dimen.xs))
								.size(dimensionResource(id = R.dimen.listing_details_image)),
							contentAlignment = Alignment.Center
						) {
							CircularProgressIndicator()
						}
					} else if (uiState.images.size == 1) {
						SubletDetailsImageDisplay(imageBitmap = uiState.images[0].asImageBitmap())
					} else if (uiState.images.isNotEmpty()) {
						Column(
							modifier = Modifier,
							verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
						) {
							HorizontalPager(
								modifier = Modifier,
								state = pagerState,
								contentPadding = PaddingValues(start = dimensionResource(id = R.dimen.xxl)),
							) { page ->
								SubletDetailsImageDisplay(imageBitmap = uiState.images[page].asImageBitmap())
							}

							Row(
								modifier = Modifier
									.height(dimensionResource(id = R.dimen.s))
									.fillMaxWidth(),
								horizontalArrangement = Arrangement.Center,
								verticalAlignment = Alignment.CenterVertically,
							) {
								repeat(imageCount) { iteration ->
									val dotColor =
										if (pagerState.currentPage == iteration)
											MaterialTheme.subletrPalette.primaryTextColor
										else MaterialTheme.subletrPalette.secondaryTextColor
									Box(
										modifier = Modifier
											.padding(dimensionResource(id = R.dimen.xxs))
											.clip(CircleShape)
											.background(dotColor)
											.size(dimensionResource(id = R.dimen.xs)),
									)
								}
							}
						}

						IconButton(
							modifier = Modifier.align(Alignment.CenterStart),
							onClick = {
								coroutineScope.launch {
									pagerState.animateScrollToPage(pagerState.currentPage - 1)
								}
							},
						) {
							Icon(
								painter = painterResource(
									id = R.drawable.arrow_left_solid_black_24
								),
								contentDescription = stringResource(id = R.string.prev_arrow),
							)
						}

						IconButton(
							modifier = Modifier.align(Alignment.CenterEnd),
							onClick = {
								coroutineScope.launch {
									pagerState.animateScrollToPage(pagerState.currentPage + 1)
								}
							},
						) {
							Icon(
								painter = painterResource(
									id = R.drawable.arrow_right_solid_black_24
								),
								contentDescription = stringResource(id = R.string.next_arrow),
							)
						}
					} else {
						SubletDetailsImageDisplay()
					}
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				Row(
					modifier = Modifier
						.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center,
				) {
					Text(
						text = uiState.address,
						style = SubletrTypography.titleSmall,
						color = MaterialTheme.subletrPalette.primaryTextColor,
						textAlign = TextAlign.Center,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.l)),
				)

				NumericalInputTextField(
					labelId = R.string.price,
					uiStateValue = uiState.editableFields.price!!,
					attemptCreate = attemptCreate,
					onValueChange = {
						if (it.isEmpty()) {
							viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(price = 0))
						} else if (it.matches(numberPattern)) {
							viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(price = it.toInt()))
						}
					},
					prefix = {
						Icon(
							painter = painterResource(
								id = R.drawable.dollar_solid_black_24
							),
							contentDescription = stringResource(id = R.string.price),
							tint = MaterialTheme.subletrPalette.primaryTextColor
						)
					},
				)

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement =  Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
					verticalAlignment = Alignment.CenterVertically,
				) {
					DateInputButton(
						modifier = modifier
							.fillMaxWidth()
							.weight(1f)
							.border(
								width = dimensionResource(id = R.dimen.xxxs),
								color =
								if (!attemptCreate || uiState.startDateDisplay != "")
									MaterialTheme.subletrPalette.textFieldBorderColor
								else
									MaterialTheme.subletrPalette.warningColor,
								shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
							),
						labelStringId = R.string.start_date,
						labelColor =
						if (!attemptCreate || uiState.startDateDisplay != "")
							MaterialTheme.subletrPalette.secondaryTextColor
						else
							MaterialTheme.subletrPalette.warningColor,
						value = uiState.startDateDisplay,
						onClick = {
							coroutineScope.launch {
								openDatePicker.value = true
							}
						}
					)
					DateInputButton(
						modifier = modifier
							.fillMaxWidth()
							.weight(1f)
							.border(
								width = dimensionResource(id = R.dimen.xxxs),
								color =
								if (!attemptCreate || uiState.endDateDisplay != "")
									MaterialTheme.subletrPalette.textFieldBorderColor
								else
									MaterialTheme.subletrPalette.warningColor,
								shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
							),
						labelStringId = R.string.start_date,
						labelColor =
						if (!attemptCreate || uiState.endDateDisplay != "")
							MaterialTheme.subletrPalette.secondaryTextColor
						else
							MaterialTheme.subletrPalette.warningColor,
						value = uiState.endDateDisplay,
						onClick = {
							coroutineScope.launch {
								openDatePicker.value = true
							}
						}
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(id = R.string.bedrooms),
						color = MaterialTheme.subletrPalette.primaryTextColor,
						style = SubletrTypography.displaySmall,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.xs)),
				)

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement =  Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
					verticalAlignment = Alignment.CenterVertically,
				) {
					NumericalInputTextField(
						modifier = Modifier.weight(1f),
						labelId = R.string.num_bedrooms,
						uiStateValue = uiState.editableFields.roomsAvailable!!,
						onValueChange = {
							if (it.isEmpty()) {
								viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(roomsAvailable = 0))
							} else if (it.matches(numberPattern)) {
								viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(roomsAvailable = it.toInt()))
							}
						},
						attemptCreate = attemptCreate,
						prefix = null,
					)
					NumericalInputTextField(
						modifier = Modifier.weight(1f),
						labelId = R.string.bedrooms_in_unit,
						uiStateValue = uiState.editableFields.roomsTotal!!,
						onValueChange = {
							if (it.isEmpty()) {
								viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(roomsTotal = 0))
							} else if (it.matches(numberPattern)) {
								viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(roomsTotal = it.toInt()))
							}
						},
						attemptCreate = attemptCreate,
						prefix = null,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(id = R.string.bathrooms),
						color = MaterialTheme.subletrPalette.primaryTextColor,
						style = SubletrTypography.displaySmall,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.xs)),
				)

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement =  Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
					verticalAlignment = Alignment.CenterVertically,
				) {
					NumericalInputTextField(
						modifier = Modifier.weight(1f),
						labelId = R.string.num_bathrooms,
						uiStateValue = uiState.editableFields.bathroomsAvailable!!,
						onValueChange = {
							if (it.isEmpty()) {
								viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(bathroomsAvailable = 0))
							} else if (it.matches(numberPattern)) {
								viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(bathroomsAvailable = it.toInt()))
							}
						},
						attemptCreate = attemptCreate,
						prefix = null,
					)
					NumericalInputTextField(
						modifier = Modifier.weight(1f),
						labelId = R.string.bathrooms_in_unit,
						uiStateValue = uiState.editableFields.bathroomsTotal!!,
						onValueChange = {
							if (it.isEmpty()) {
								viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(bathroomsTotal = 0))
							} else if (it.matches(numberPattern)) {
								viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(bathroomsTotal = it.toInt()))
							}
						},
						attemptCreate = attemptCreate,
						prefix = null,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.s)),
				)

				RoundedExposedDropdown(
					dropdownItems = EnsuiteBathroomOption.values(),
					labelId = R.string.ensuite_bathroom,
					selectedDropdownItem = if (uiState.editableFields.bathroomsEnsuite!! == 1)
						EnsuiteBathroomOption.YES
					else EnsuiteBathroomOption.NO,
					dropdownItemToString = { stringResource(id = it.stringId) },
					setSelectedDropdownItem = {
						viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(bathroomsEnsuite =
						if (it == EnsuiteBathroomOption.YES) 1 else 0))
					},
				)

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						text = stringResource(id = R.string.additional_information),
						color = MaterialTheme.subletrPalette.primaryTextColor,
						style = SubletrTypography.displaySmall,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.xs)),
				)

				RoundedExposedDropdown(
					dropdownItems = ListingForGenderOption.values(),
					labelId = R.string.gender,
					selectedDropdownItem = uiState.editableFields.gender!!.getGender(),
					dropdownItemToString = { stringResource(id = it.stringId) },
					setSelectedDropdownItem = {
						viewModel.editableFieldsStream.onNext(
							uiState.editableFields.copy(gender = it.getKey())
						)
					},
				)

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.s)),
				)

				RoundedExposedDropdown(
					dropdownItems = HousingType.values(),
					labelId = R.string.housing_type,
					selectedDropdownItem = uiState.editableFields.residenceType!!.toHousingType(),
					dropdownItemToString = { stringResource(id = it.stringId) },
					setSelectedDropdownItem = {
						viewModel.editableFieldsStream.onNext(
							uiState.editableFields.copy(residenceType = it.toResidenceType())
						)
					},
				)

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.s)),
				)

				RoundedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.height(dimensionResource(id = R.dimen.xxxxxxl))
						.border(
							width = dimensionResource(id = R.dimen.xxxs),
							color = MaterialTheme.subletrPalette.textFieldBorderColor,
							shape = RoundedCornerShape(dimensionResource(id = R.dimen.s))
						),
					placeholder = {
						Text(
							text = stringResource(id = R.string.description),
							color = MaterialTheme.subletrPalette.secondaryTextColor,
						)
					},
					label = {
						Text(
							text = stringResource(id = R.string.description),
							color = MaterialTheme.subletrPalette.secondaryTextColor,
						)
					},
					shape = RoundedCornerShape(dimensionResource(id = R.dimen.s)),
					singleLine = false,
					value = uiState.editableFields.description!!,
					onValueChange = {
						viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(description = it))
					}
				)

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement =  Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
					verticalAlignment = Alignment.CenterVertically,
				) {
					SecondaryButton(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f)
							.height(dimensionResource(id = R.dimen.xxl))
							.padding(bottom = dimensionResource(id = R.dimen.xs)),
						onClick = {
						  	openDeleteDialog.value = true
						},
					) {
						Text(
							text = stringResource(id = R.string.delete),
							color = MaterialTheme.subletrPalette.primaryTextColor,
						)
					}
					PrimaryButton(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f)
							.height(dimensionResource(id = R.dimen.xxl))
							.padding(bottom = dimensionResource(id = R.dimen.xs)),
						onClick = {
							coroutineScope.launch {
								snackbarHostState.showSnackbar("Updated listing")
						  	}
							viewModel.updateListing(uiState.editableFields)
						},
					) {
						Text(
							text = stringResource(id = R.string.save),
							color = MaterialTheme.subletrPalette.textOnSubletrPink,
						)
					}
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.l)),
				)

				if (openDatePicker.value) {
					DatePicker(
						uiState = uiState,
						viewModel = viewModel,
						coroutineScope = coroutineScope,
						openDatePicker = openDatePicker,
					)
				}

				if (openDeleteDialog.value) {
					DeleteDialog(
						onDismissRequest = { openDeleteDialog.value = false },
						onConfirmClick = { openDeleteDialog.value = false },
					)
				}
			}
		}
	}
}

private val storeDateFormatISO: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

@OptIn(ExperimentalMaterial3Api::class)
private val displayDateFormatter = DatePickerDefaults.dateFormatter(selectedDateSkeleton = "MM/dd/yyyy")

@Composable
fun DeleteDialog(
	onDismissRequest: () -> Unit,
	onConfirmClick: () -> Unit,
) {
	AlertDialog(
		onDismissRequest = onDismissRequest,
		title = {
			Text(
				text = "Delete listing",
				style = SubletrTypography.titleSmall,
			)
		},
		text = {
			Text(
				text = "Are you sure you want to delete your listing? This action can not be undone.",
				style = SubletrTypography.bodyLarge,
			)
		},
		confirmButton = {
			TextButton(
				onClick = onConfirmClick,
			) {
				Text(
					text = stringResource(id = R.string.delete),
					color = MaterialTheme.subletrPalette.subletrPink
				)
			}
		},
		dismissButton = {
			TextButton(
				onClick = onDismissRequest,
			) {
				Text(text = stringResource(id = R.string.cancel))
			}
		},
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
	uiState: ManageListingPageUiState.Loaded,
	viewModel: ManageListingPageViewModel,
	coroutineScope: CoroutineScope,
	openDatePicker: MutableState<Boolean>,
) {
	val datePickerBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val dateRangePickerState = rememberDateRangePickerState()
	DatePickerBottomSheet(
		datePickerBottomSheetState,
		dateRangePickerState,
		onDismissRequest = { openDatePicker.value = false },
		onClick = {
			coroutineScope.launch { datePickerBottomSheetState.hide() }.invokeOnCompletion {
				if (!datePickerBottomSheetState.isVisible) {
					openDatePicker.value = false
				}
				if (dateRangePickerState.selectedStartDateMillis != null) {
					val startButtonText =
						displayDateFormatter.formatDate(dateRangePickerState.selectedStartDateMillis, locale = Locale.getDefault())!!
					viewModel.startDateDisplayTextStream.onNext(startButtonText)
					val startDate = SimpleDateFormat("MM/dd/yyyy").parse(startButtonText)
					viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(
						leaseStart = if (startDate is Date)
							startDate.toInstant().atOffset(ZoneOffset.UTC).format(storeDateFormatISO)
						else uiState.editableFields.leaseStart
					))

				} else {
					viewModel.startDateDisplayTextStream.onNext("")
					viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(leaseStart = ""))
				}
				if (dateRangePickerState.selectedEndDateMillis != null) {
					val endButtonText =
						displayDateFormatter.formatDate(dateRangePickerState.selectedEndDateMillis, locale = Locale.getDefault())!!
					viewModel.endDateDisplayTextStream.onNext(endButtonText)
					val endDate = SimpleDateFormat("MM/dd/yyyy").parse(endButtonText)
					viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(
						leaseEnd = if (endDate is Date)
							endDate.toInstant().atOffset(ZoneOffset.UTC).format(storeDateFormatISO)
						else uiState.editableFields.leaseEnd
					))

				} else {
					viewModel.endDateDisplayTextStream.onNext("")
					viewModel.editableFieldsStream.onNext(uiState.editableFields.copy(leaseEnd = ""))
				}
			}
		}
	)
}

@Preview(showBackground = true)
@Composable
fun ManageListingPagePreview() {
	ManageListingPageView()
}

@Preview(showBackground = true)
@Composable
fun ManageListingPageLoadedPreview() {
	SubletrTheme {
		ManageListingPageView(
			uiState = ManageListingPageUiState.Loaded(
				address = "",
				images = listOf(),
				isFetchingImages = false,
				editableFields = UpdateListingRequest(
					price = 0,
					roomsAvailable = 0,
					roomsTotal = 0,
					bathroomsAvailable = 0,
					bathroomsEnsuite = 0,
					bathroomsTotal = 0,
					leaseStart = "",
					leaseEnd = "",
					description = "",
					residenceType = ResidenceType.other,
					gender = "",
				),
				startDateDisplay = "",
				endDateDisplay = "",
			)
		)
	}
}
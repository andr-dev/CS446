@file:Suppress("CyclomaticComplexMethod", "ForbiddenComment")

package org.uwaterloo.subletr.pages.createlisting

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.bottomsheet.DatePickerBottomSheet
import org.uwaterloo.subletr.components.bottomsheet.ImagePickerBottomSheet
import org.uwaterloo.subletr.components.button.DateInputButton
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.enums.EnsuiteBathroomOption
import org.uwaterloo.subletr.enums.ListingForGenderOption
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.SubletrTypography
import org.uwaterloo.subletr.theme.subletrPalette
import org.uwaterloo.subletr.utils.ComposeFileProvider
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingPageView(
	modifier: Modifier = Modifier,
	viewModel: CreateListingPageViewModel = hiltViewModel(),
	uiState: CreateListingPageUiState = viewModel.uiStateStream.subscribeAsState(
		CreateListingPageUiState.Loading
	).value,
) {
	val scrollState = rememberScrollState()
	val coroutineScope = rememberCoroutineScope()

	val dateRangePickerState = rememberDateRangePickerState()

	var autocompleteExpanded by remember { mutableStateOf(false) }
	val focusManager = LocalFocusManager.current

	var openDatePicker by rememberSaveable { mutableStateOf(false) }
	val datePickerBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


	var attemptCreate by remember { mutableStateOf(false) }
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
					text = stringResource(id = R.string.create_new_listing),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.subletrPalette.primaryTextColor,
				)
			}
		},
	) { paddingValues ->
		if (uiState is CreateListingPageUiState.Loading) {
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
		} else if (uiState is CreateListingPageUiState.Loaded) {
			Column(
				modifier = Modifier
					.padding(top = paddingValues.calculateTopPadding())
					.fillMaxSize()
					.verticalScroll(scrollState)
					.padding(horizontal = dimensionResource(id = R.dimen.s)),
				verticalArrangement = Arrangement.Center,
			) {

				Spacer(
					modifier = Modifier.weight(weight = 1.0f)
				)
				if (attemptCreate && !canCreate(uiState = uiState)) {
					Text(
						text = stringResource(id = R.string.update_fields),
						color = MaterialTheme.subletrPalette.warningColor
					)
				}

				Spacer(
					modifier = Modifier.weight(weight = 1.0f)
				)

				ExposedDropdownMenuBox(
					modifier = Modifier,
					expanded = autocompleteExpanded,
					onExpandedChange = { autocompleteExpanded = true },
				) {
					RoundedTextField(
						modifier = Modifier
							.fillMaxWidth()
							.menuAnchor()
							.border(
								width = dimensionResource(id = R.dimen.xxxs),
								color =
								if (!attemptCreate || !adressIsEmpty(uiState.address))
									MaterialTheme.subletrPalette.textFieldBorderColor
								else
									MaterialTheme.subletrPalette.warningColor,
								shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
							),
						placeholder = {
							Text(
								text = stringResource(id = R.string.address_format_label),
								color = MaterialTheme.subletrPalette.secondaryTextColor,

							)
						},
						label = {
							Text(
								text = stringResource(id = R.string.address),
								color =
								if (!attemptCreate || !adressIsEmpty(uiState.address))
									MaterialTheme.subletrPalette.secondaryTextColor
								else
									MaterialTheme.subletrPalette.warningColor,
							)
						},
						value = uiState.address.fullAddress,
						onValueChange = {
							viewModel.fullAddressStream.onNext(it)
						}
					)

					ExposedDropdownMenu(
						modifier = Modifier,
						expanded = autocompleteExpanded,
						onDismissRequest = { autocompleteExpanded = false },
					) {
						uiState.addressAutocompleteOptions.map { prediction ->
							DropdownMenuItem(
								modifier = Modifier,
								text = { Text(text = prediction.getFullText(null).toString()) },
								onClick = {
									viewModel.fullAddressStream.onNext(prediction.getFullText(null).toString())
									focusManager.clearFocus()
									autocompleteExpanded = false
								},
							)
						}
					}
				}

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
				)

				RoundedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.border(
							width = dimensionResource(id = R.dimen.xxxs),
							color =
							if (!attemptCreate || uiState.price > 0)
								MaterialTheme.subletrPalette.textFieldBorderColor
							else
								MaterialTheme.subletrPalette.warningColor,
							shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
						),
					placeholder = {
						Text(
							text = stringResource(id = R.string.price),
							color =
							if (!attemptCreate || canCreate(uiState))
								MaterialTheme.subletrPalette.secondaryTextColor
							else
								MaterialTheme.subletrPalette.warningColor,
						)
					},
					label = {
						Text(
							text = stringResource(id = R.string.price),
							color =
							if (!attemptCreate || uiState.price > 0)
								MaterialTheme.subletrPalette.secondaryTextColor
							else
								MaterialTheme.subletrPalette.warningColor,
						)
					},
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					value = if (uiState.price == 0) "" else uiState.price.toString(),
					onValueChange = {
						if (it.isEmpty()) {
							viewModel.priceStream.onNext(0)
						} else if (it.matches(numberPattern)) {
							viewModel.priceStream.onNext(it.toInt())
						}
					}
				)

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				CreateListingNumericalInputs(
					labelId = R.string.price,
					viewModelStream = viewModel.priceStream,
					uiStateValue = uiState.price,
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
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f)
							.border(
								width = dimensionResource(id = R.dimen.xxxs),
								color =
								if (!attemptCreate || uiState.startDate != "")
									MaterialTheme.subletrPalette.textFieldBorderColor
								else
									MaterialTheme.subletrPalette.warningColor,
								shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
							),
						labelStringId = R.string.start_date,
						value = uiState.startDateDisplayText,
						onClick = {
							coroutineScope.launch {
								openDatePicker = true
							}
						})

					DateInputButton(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f)
							.border(
								width = dimensionResource(id = R.dimen.xxxs),
								color =
								if (!attemptCreate || uiState.endDate != "")
									MaterialTheme.subletrPalette.textFieldBorderColor
								else
									MaterialTheme.subletrPalette.warningColor,
								shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
							),
						labelStringId = R.string.end_date,
						value = uiState.endDateDisplayText,
						onClick = {
							coroutineScope.launch {
								openDatePicker = true
							}
						})
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
					CreateListingNumericalInputs(
						modifier = Modifier.weight(1f),
						labelId = R.string.num_bedrooms,
						viewModelStream = viewModel.numBedroomsStream,
						uiStateValue = uiState.numBedrooms,
					)
					CreateListingNumericalInputs(
						modifier = Modifier.weight(1f),
						labelId = R.string.bedrooms_in_unit,
						viewModelStream = viewModel.totalNumBedroomsStream,
						uiStateValue = uiState.totalNumBedrooms,
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
					CreateListingNumericalInputs(
						modifier = Modifier.weight(1f),
						labelId = R.string.num_bathrooms,
						viewModelStream = viewModel.numBathroomsStream,
						uiStateValue = uiState.numBathrooms,
					)
					CreateListingNumericalInputs(
						modifier = Modifier.weight(1f),
						labelId = R.string.bathrooms_in_unit,
						viewModelStream = viewModel.totalNumBathroomsStream,
						uiStateValue = uiState.totalNumBathrooms,
					)
				}

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.s)),
				)

				CreateListingSelectionInput(
					dropdownItems = EnsuiteBathroomOption.values(),
					labelId = R.string.ensuite_bathroom,
					selectedDropdownItem = uiState.bathroomsEnsuite,
					dropdownItemToString = { stringResource(id = it.stringId) },
					setSelectedDropdownItem = {
						viewModel.bathroomsEnsuiteStream.onNext(it)
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

				CreateListingSelectionInput(
					dropdownItems = ListingForGenderOption.values(),
					labelId = R.string.gender,
					selectedDropdownItem = uiState.gender,
					dropdownItemToString = { stringResource(id = it.stringId) },
					setSelectedDropdownItem = {
						viewModel.genderStream.onNext(it)
					},
				)

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.s)),
				)

				CreateListingSelectionInput(
					dropdownItems = HousingType.values(),
					labelId = R.string.housing_type,
					selectedDropdownItem = uiState.housingType,
					dropdownItemToString = { stringResource(id = it.stringId) },
					setSelectedDropdownItem = {
						viewModel.housingTypeStream.onNext(it)
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
					value = uiState.description,
					onValueChange = {
						viewModel.descriptionStream.onNext(it)
					}
				)

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				Text(
					text = stringResource(id = R.string.upload_images),
					style = MaterialTheme.typography.titleSmall,
					color = MaterialTheme.subletrPalette.primaryTextColor,
				)

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				UploadImages(viewModel, uiState, coroutineScope)

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				if (openDatePicker) {
					DatePickerBottomSheet(
						datePickerBottomSheetState,
						dateRangePickerState,
						onDismissRequest = { openDatePicker = false },
						onClick = {
							coroutineScope.launch { datePickerBottomSheetState.hide() }.invokeOnCompletion {
								if (!datePickerBottomSheetState.isVisible) {
									openDatePicker = false
								}
								if (dateRangePickerState.selectedStartDateMillis != null) {
									val startButtonText =
										displayDateFormatter.formatDate(dateRangePickerState.selectedStartDateMillis, locale = Locale.getDefault())!!
									viewModel.startDateDisplayTextStream.onNext(startButtonText)
									val startDate = SimpleDateFormat("MM/dd/yyyy").parse(startButtonText)
									viewModel.startDateStream.onNext(
										if (startDate is Date)
											startDate.toInstant().atOffset(ZoneOffset.UTC).format(
												storeDateFormatISO
											)
										else uiState.startDate
									)
								} else {
									viewModel.startDateDisplayTextStream.onNext("")
									viewModel.startDateStream.onNext("")
								}
								if (dateRangePickerState.selectedEndDateMillis != null) {
									val endButtonText =
										displayDateFormatter.formatDate(dateRangePickerState.selectedEndDateMillis, locale = Locale.getDefault())!!
									viewModel.endDateDisplayTextStream.onNext(endButtonText)
									val endDate = SimpleDateFormat("MM/dd/yyyy").parse(endButtonText)
									viewModel.endDateStream.onNext(
											endDate.toInstant().atOffset(ZoneOffset.UTC).format(
												storeDateFormatISO
											)
									)
								} else {
									viewModel.endDateDisplayTextStream.onNext("")
									viewModel.endDateStream.onNext("")
								}
							}
						}
					)
				}

				PrimaryButton(
					modifier = Modifier
						.fillMaxWidth()
						.height(dimensionResource(id = R.dimen.xxl))
						.padding(bottom = dimensionResource(id = R.dimen.xs)),
					onClick = {
						attemptCreate = true
						if (canCreate(uiState)) {
							viewModel.createListingStream.onNext(uiState)
						}
					},
				) {
					Text(
						text = stringResource(id = R.string.create_post),
						color = MaterialTheme.subletrPalette.textOnSubletrPink,
					)
				}
			}
		}
	}
}


private val storeDateFormatISO: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

// TODO: localize date format
@OptIn(ExperimentalMaterial3Api::class)
private val displayDateFormatter = DatePickerDefaults.dateFormatter(selectedDateSkeleton = "MM/dd/yyyy")

fun canCreate(uiState: CreateListingPageUiState.Loaded) : Boolean {
	return !adressIsEmpty(uiState.address) &&
		uiState.price != 0 &&
		uiState.startDate != "" &&
		uiState.endDate != "" &&
		uiState.numBedrooms != 0
}

fun adressIsEmpty(addressModel: CreateListingPageUiState.AddressModel) : Boolean {
	return addressModel.addressCity == "" &&
		addressModel.addressLine == "" &&
		addressModel.addressPostalCode == "" &&
		addressModel.fullAddress == ""
}

@Composable
fun CreateListingNumericalInputs(
	modifier: Modifier = Modifier,
	labelId: Int,
	viewModelStream: BehaviorSubject<Int>,
	uiStateValue: Int,
) {
	val numberPattern = remember { Regex("^\\d+\$") }

	RoundedTextField(
		modifier = modifier
			.fillMaxWidth()
			.border(
				dimensionResource(id = R.dimen.xxxs),
				MaterialTheme.subletrPalette.textFieldBorderColor,
				RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
			),
		placeholder = {
			Text(
				text = stringResource(id = labelId),
				color = MaterialTheme.subletrPalette.secondaryTextColor,
			)
		},
		label = {
			Text(
				text = stringResource(id = labelId),
				color = MaterialTheme.subletrPalette.secondaryTextColor,
			)
		},
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		value = if (uiStateValue == 0) "" else uiStateValue.toString(),
		onValueChange = {
			if (it.isEmpty()) {
				viewModelStream.onNext(0)
			} else if (it.matches(numberPattern)) {
				viewModelStream.onNext(it.toInt())
			}
		}
	)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CreateListingSelectionInput(
	modifier: Modifier = Modifier,
	dropdownItems: Array<T>,
	labelId: Int,
	selectedDropdownItem: T,
	dropdownItemToString: @Composable (T) -> String,
	setSelectedDropdownItem: (T) -> Unit,
) {
	var expanded by remember {
		mutableStateOf(false)
	}

	ExposedDropdownMenuBox(
		modifier = modifier,
		expanded = expanded,
		onExpandedChange = { expanded = !expanded },
	) {
		RoundedTextField(
			modifier = modifier
				.fillMaxWidth()
				.menuAnchor()
				.border(
					dimensionResource(id = R.dimen.xxxs),
					MaterialTheme.subletrPalette.textFieldBorderColor,
					RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
				),
			readOnly = true,
			value = dropdownItemToString(selectedDropdownItem),
			onValueChange = {},
			label = { Text(text = stringResource(id = labelId)) },
			trailingIcon = {
				ExposedDropdownMenuDefaults.TrailingIcon(
					expanded = expanded
				)
			},
		)
		DropdownMenu(
			modifier = modifier.exposedDropdownSize(),
			expanded = expanded,
			onDismissRequest = { expanded = false },
		) {
			dropdownItems.forEach {
				DropdownMenuItem(
					modifier = modifier,
					onClick = {
						setSelectedDropdownItem(it)
						expanded = false
					},
					text = { Text(text = dropdownItemToString(it)) },
				)
			}
		}
	}
}

@Composable
fun ImageUploadMethodButton(
	iconId: Int,
	buttonText: String,
	onClick: () -> Unit,
) {
	Button(
		modifier = Modifier,
		onClick = onClick,
		colors = ButtonDefaults.buttonColors(
			containerColor = MaterialTheme.subletrPalette.primaryBackgroundColor,
		),
	) {
		Column(
			modifier = Modifier,
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s))
		) {
			Icon(
				modifier = Modifier.size(dimensionResource(id = R.dimen.xxl)),
				painter = painterResource(
					id = iconId,
				),
				contentDescription = buttonText,
				tint = MaterialTheme.subletrPalette.secondaryTextColor,
			)
			Text(
				text = buttonText,
				style = MaterialTheme.typography.bodyLarge,
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImages(
	viewModel: CreateListingPageViewModel,
	uiState: CreateListingPageUiState.Loaded,
	coroutineScope: CoroutineScope,
) {
	val context = LocalContext.current

	var imageUris by remember { mutableStateOf<List<Uri?>>(ArrayList()) }
	var hasImage by remember { mutableStateOf(false) }

	var openImagePicker by rememberSaveable { mutableStateOf(false) }
	val imagePickerBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

	val imageSelectorLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetMultipleContents()
	) {
		imageUris = it
		hasImage = true
	}

	val cameraLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.TakePicture(),
	) {
		hasImage = it
	}

	LazyRow(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
	) {
		item {
			Button(
				modifier = Modifier
					.size(dimensionResource(id = R.dimen.xxxxxxl))
					.border(
						width = dimensionResource(id = R.dimen.xxxs),
						color = MaterialTheme.subletrPalette.textFieldBorderColor,
						shape = RoundedCornerShape(dimensionResource(id = R.dimen.s))
					),
				shape = RoundedCornerShape(dimensionResource(id = R.dimen.s)),
				onClick = {
					coroutineScope.launch {
						openImagePicker = true
					}
				},
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.subletrPalette.textFieldBackgroundColor,
					contentColor = MaterialTheme.subletrPalette.unselectedGray,
				),
			) {
				Column(
					modifier = Modifier,
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs))
				) {
					Icon(
						painter = painterResource(
							id = R.drawable.add_filled_black_32
						),
						contentDescription = stringResource(id = R.string.upload_images),
					)
					Text(
						text = stringResource(id = R.string.add_photo),
						color = MaterialTheme.subletrPalette.secondaryTextColor
					)
				}
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
							imageUris = listOf(uri)
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

			// TODO: move to ViewModel to retrieve images
			if (imageUris.isNotEmpty() && hasImage) {
				imageUris.filterNotNull().map {
					if (Build.VERSION.SDK_INT < 28) {
						uiState.imagesBitmap.add(
							MediaStore.Images
								.Media.getBitmap(context.contentResolver, it)
						)
						viewModel.imagesBitmapStream.onNext(uiState.imagesBitmap)
					} else {
						val source = ImageDecoder
							.createSource(context.contentResolver, it)
						uiState.imagesBitmap.add(ImageDecoder.decodeBitmap(source))
						viewModel.imagesBitmapStream.onNext(uiState.imagesBitmap)
					}
				}
				imageUris = emptyList<Uri>()
				hasImage = false
			}
			Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)))
		}

		itemsIndexed(uiState.imagesBitmap.filterNotNull()) { _, item ->
			Box {
				Image(
					bitmap = item.asImageBitmap(),
					contentDescription = null,
					modifier = Modifier
						.size(dimensionResource(id = R.dimen.xxxxxxl))
						.clip(RoundedCornerShape(dimensionResource(id = R.dimen.s))),
					contentScale = ContentScale.FillBounds
				)
				Button(
					modifier = Modifier
						.size(dimensionResource(id = R.dimen.xl))
						.padding(dimensionResource(id = R.dimen.xs))
						.align(Alignment.TopEnd),
					shape = CircleShape,
					contentPadding = PaddingValues(dimensionResource(id = R.dimen.xxs)),
					onClick = {
						uiState.imagesBitmap.remove(item)
						viewModel.imagesBitmapStream.onNext(uiState.imagesBitmap)
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.subletrPalette.textFieldBackgroundColor,
						contentColor = MaterialTheme.subletrPalette.primaryTextColor,
					),
				) {
					Icon(
						modifier = Modifier.fillMaxSize(),
						painter = painterResource(
							id = R.drawable.close_solid_black_24
						),
						contentDescription = stringResource(id = R.string.upload_images),
					)
				}
			}
			Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.xs)))
		}
	}
}

@Preview(showBackground = true)
@Composable
fun CreateListingPagePreview() {
	CreateListingPageView()
}

@Preview(showBackground = true)
@Composable
fun CreateListingPageLoadedPreview() {
	SubletrTheme {
		CreateListingPageView(
			uiState = CreateListingPageUiState.Loaded(
				address = CreateListingPageUiState.AddressModel(
					fullAddress = "",
					addressLine = "",
					addressCity = "",
					addressPostalCode = "",
					addressCountry = stringResource(id = R.string.canada),
				),
				addressAutocompleteOptions = ArrayList(),
				description = "",
				price = 0,
				numBedrooms = 0,
				totalNumBedrooms = 0,
				numBathrooms = 0,
				bathroomsEnsuite = EnsuiteBathroomOption.NO,
				totalNumBathrooms = 0,
				gender = ListingForGenderOption.FEMALE,
				startDate = "",
				startDateDisplayText = "",
				endDate = "",
				endDateDisplayText = "",
				housingType = HousingType.OTHER,
				imagesBitmap = ArrayList(),
				images = ArrayList()
			)
		)
	}
}

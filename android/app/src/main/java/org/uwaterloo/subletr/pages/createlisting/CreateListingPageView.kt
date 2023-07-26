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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.addressautocomplete.AddressAutocomplete
import org.uwaterloo.subletr.components.bottomsheet.DatePickerBottomSheet
import org.uwaterloo.subletr.components.bottomsheet.ImagePickerBottomSheet
import org.uwaterloo.subletr.components.button.DateInputButton
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.dropdown.RoundedExposedDropdown
import org.uwaterloo.subletr.components.textfield.NumericalInputTextField
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.enums.EnsuiteBathroomOption
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.enums.ListingForGenderOption
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.SubletrTypography
import org.uwaterloo.subletr.theme.subletrPalette
import org.uwaterloo.subletr.utils.ComposeFileProvider
import org.uwaterloo.subletr.utils.canCreate
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
	val openDatePicker = rememberSaveable { mutableStateOf(false) }
	var attemptCreate by remember { mutableStateOf(false) }
	val dateRangePickerState = rememberDateRangePickerState()


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
					style = MaterialTheme.typography.titleSmall,
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

				WarnText(
					uiState = uiState,
					attemptCreate = attemptCreate,
				)

				AddressAutocomplete(
					fullAddress = uiState.address.fullAddress,
					addressAutocompleteOptions = uiState.addressAutocompleteOptions,
					setAddress = { inputAddress -> viewModel.fullAddressStream.onNext(inputAddress) },
					attemptingCreate = attemptCreate,
				)

				Spacer(
					modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
				)

				NumericalInputTextField(
					labelId = R.string.price,
					uiStateValue = uiState.price,
					onValueChange = {
						if (it.isEmpty()) {
							viewModel.priceStream.onNext(0)
						} else if (it.matches(numberPattern)) {
							viewModel.priceStream.onNext(it.toInt())
						}
					},
					attemptCreate = attemptCreate,
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

					DateInputBox(
						modifier = Modifier.weight(1f),
						labelStringId = R.string.start_date,
						attemptCreate = attemptCreate,
						coroutineScope = coroutineScope,
						dateString = uiState.startDate,
						displayString = uiState.startDateDisplayText,
						openDatePicker = openDatePicker,
					)
					DateInputBox(
						modifier = Modifier.weight(1f),
						labelStringId = R.string.end_date,
						attemptCreate = attemptCreate,
						coroutineScope = coroutineScope,
						dateString = uiState.endDate,
						displayString = uiState.endDateDisplayText,
						openDatePicker = openDatePicker,
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
						uiStateValue = uiState.numBedrooms,
						onValueChange = {
							if (it.isEmpty()) {
								viewModel.numBedroomsStream.onNext(0)
							} else if (it.matches(numberPattern)) {
								viewModel.numBedroomsStream.onNext(it.toInt())
							}
						},
						attemptCreate = attemptCreate,
						prefix = null,
					)
					NumericalInputTextField(
						modifier = Modifier.weight(1f),
						labelId = R.string.bedrooms_in_unit,
						uiStateValue = uiState.totalNumBedrooms,
						onValueChange = {
							if (it.isEmpty()) {
								viewModel.totalNumBedroomsStream.onNext(0)
							} else if (it.matches(numberPattern)) {
								viewModel.totalNumBedroomsStream.onNext(it.toInt())
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
						uiStateValue = uiState.numBathrooms,
						onValueChange = {
							if (it.isEmpty()) {
								viewModel.numBathroomsStream.onNext(0)
							} else if (it.matches(numberPattern)) {
								viewModel.numBathroomsStream.onNext(it.toInt())
							}
						},
						attemptCreate = attemptCreate,
						prefix = null,
					)
					NumericalInputTextField(
						modifier = Modifier.weight(1f),
						labelId = R.string.bathrooms_in_unit,
						uiStateValue = uiState.totalNumBathrooms,
						onValueChange = {
							if (it.isEmpty()) {
								viewModel.totalNumBathroomsStream.onNext(0)
							} else if (it.matches(numberPattern)) {
								viewModel.totalNumBathroomsStream.onNext(it.toInt())
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

				RoundedExposedDropdown(
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

				RoundedExposedDropdown(
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

				if (openDatePicker.value) {
					DatePicker(
						uiState = uiState,
						viewModel = viewModel,
						coroutineScope = coroutineScope,
						openDatePicker = openDatePicker,
						dateRangePickerState = dateRangePickerState,
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

@Composable
fun WarnText(
	modifier: Modifier = Modifier,
	attemptCreate: Boolean,
	uiState: CreateListingPageUiState.Loaded,
) {
	if (attemptCreate && !canCreate(uiState = uiState)) {
		Spacer(
			modifier = Modifier.height(dimensionResource(id = R.dimen.xxs)),
		)
		Column(
			modifier = modifier
				.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = stringResource (id = R.string.update_fields),
				color = MaterialTheme.subletrPalette.warningColor
			)
		}
		Spacer(
			modifier = Modifier.height(dimensionResource(id = R.dimen.xs)),
		)
	} else {
		Spacer(
			modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
	uiState: CreateListingPageUiState.Loaded,
	viewModel: CreateListingPageViewModel,
	coroutineScope: CoroutineScope,
	openDatePicker: MutableState<Boolean>,
	dateRangePickerState: DateRangePickerState,
) {
	val datePickerBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val placeholderDate = stringResource(id = R.string.placeholder_date)
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
						displayDateFormatter.formatDate(
							dateRangePickerState.selectedStartDateMillis,
							locale = Locale.getDefault()
						) ?: placeholderDate
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
						displayDateFormatter.formatDate(
							dateRangePickerState.selectedEndDateMillis,locale = Locale.getDefault()
						) ?: placeholderDate
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
@Composable
fun DateInputBox(
	modifier: Modifier = Modifier,
	labelStringId: Int,
	attemptCreate: Boolean,
	coroutineScope: CoroutineScope,
	dateString: String,
	displayString: String,
	openDatePicker: MutableState<Boolean>,
) {
	DateInputButton(
		modifier = modifier
			.fillMaxWidth()
			.border(
				width = dimensionResource(id = R.dimen.xxxs),
				color =
				if (!attemptCreate || dateString != "")
					MaterialTheme.subletrPalette.textFieldBorderColor
				else
					MaterialTheme.subletrPalette.warningColor,
				shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
			),
		labelStringId = labelStringId,
		labelColor =
		if (!attemptCreate || dateString != "")
			MaterialTheme.subletrPalette.secondaryTextColor
		else
			MaterialTheme.subletrPalette.warningColor,
		value = displayString,
		onClick = {
			coroutineScope.launch {
				openDatePicker.value = true
			}
		})
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

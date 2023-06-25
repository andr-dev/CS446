package org.uwaterloo.subletr.pages.createlisting

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.textFieldBackgroundColor
import org.uwaterloo.subletr.theme.textFieldBorderColor
import org.uwaterloo.subletr.theme.textOnSubletrPink
import java.text.SimpleDateFormat
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
	var startButtonText by remember { mutableStateOf("") }
	var endButtonText by remember { mutableStateOf("") }

	var openBottomSheet by rememberSaveable { mutableStateOf(false) }
	val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

	val numberPattern = remember { Regex("^\\d+\$") }


	Scaffold(
		modifier = modifier,
		topBar = {
			Row(
				modifier = Modifier,
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				IconButton(
					modifier = Modifier,
					onClick = {
						viewModel.navigationService.getNavHostController().popBackStack()
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
					style = MaterialTheme.typography.titleMedium
				)
			}
		},
	) { paddingValues ->
		if (uiState is CreateListingPageUiState.Loading) {
			Column(
				modifier = Modifier
					.padding(paddingValues = paddingValues)
					.fillMaxSize(1.0f)
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
					.fillMaxSize(fraction = 1.0f)
					.verticalScroll(scrollState)
					.padding(start = 20.dp, end = 20.dp),
				verticalArrangement = Arrangement.Center,
			) {

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
				)

				RoundedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.height(60.dp)
						.border(
							2.dp,
							textFieldBorderColor,
							RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
						),
					placeholder = {
						Text(
							text = stringResource(id = R.string.address),
							color = secondaryTextColor,
						)
					},
					label = {
						Text(
							text = stringResource(id = R.string.address),
							color = secondaryTextColor,
						)
					},
					value = uiState.address,
					onValueChange = {
						viewModel.addressStream.onNext(it)
//						viewModel.updateUiState(
//							CreateListingPageUiState.Loaded(
//								address = it,
//								description = uiState.description,
//								price = uiState.price,
//								numBedrooms = uiState.numBedrooms,
//								startDate = uiState.startDate,
//								endDate = uiState.endDate,
//								housingType = uiState.housingType,
//							)
//						)
					}
				)

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
				)

				RoundedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.height(60.dp)
						.border(
							2.dp,
							textFieldBorderColor,
							RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
						),
					placeholder = {
						Text(
							text = stringResource(id = R.string.price),
							color = secondaryTextColor,
						)
					},
					label = {
						Text(
							text = stringResource(id = R.string.price),
							color = secondaryTextColor,
						)
					},
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					value = uiState.price,
					onValueChange = {
//						if (uiState.price == 0) "" else uiState.price.toString(),
						viewModel.priceStream.onNext("")
//						if (it.isEmpty()) {
//							viewModel.priceStream.onNext(0)
//						} else if (it.matches(numberPattern)) {
//							viewModel.priceStream.onNext(it.toInt())
//						} else {
//							viewModel.priceStream.onNext(0)
//						}
//						viewModel.updateUiState(
//							CreateListingPageUiState.Loaded(
//								address = uiState.address,
//								description = uiState.description,
//								price = it,
//								numBedrooms = uiState.numBedrooms,
//								startDate = uiState.startDate,
//								endDate = uiState.endDate,
//								housingType = uiState.housingType,
//							)
//						)
					}
				)

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
				)


				RoundedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.height(60.dp)
						.border(
							2.dp,
							textFieldBorderColor,
							RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
						),
					placeholder = {
						Text(
							text = stringResource(id = R.string.num_bedrooms),
							color = secondaryTextColor,
						)
					},
					label = {
						Text(
							text = stringResource(id = R.string.num_bedrooms),
							color = secondaryTextColor,
						)
					},
					value = uiState.numBedrooms,
					onValueChange = {
						viewModel.numBedroomsStream.onNext(it)
//						viewModel.updateUiState(
//							CreateListingPageUiState.Loaded(
//								address = uiState.address,
//								description = uiState.description,
//								price = uiState.price,
//								numBedrooms = it,
//								startDate = uiState.startDate,
//								endDate = uiState.endDate,
//								housingType = uiState.housingType,
//							)
//						)
					}
				)

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
				)

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					RoundedTextField(
						modifier = Modifier
							.fillMaxWidth(0.475f)
							.height(60.dp)
							.border(
								2.dp,
								textFieldBorderColor,
								RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
							),
						placeholder = {
							Text(
								text = "MM/DD/YYYY",
								color = secondaryTextColor,
							)
						},
						label = {
							Text(
								text = stringResource(id = R.string.start_date),
								color = secondaryTextColor,
							)
						},
						trailingIcon = {
						   IconButton(
							   onClick = {
								   coroutineScope.launch {
									   openBottomSheet = true
								   }
							   }
						   ) {
							   Icon(
								   painter = painterResource(
									   id = R.drawable.calendar_outline_gray_24
								   ),
								   contentDescription = stringResource(id = R.string.start_date),
							   )
						   }
						},
						value = startButtonText,
						readOnly = true,
						onValueChange = { }
					)

					RoundedTextField(
						modifier = Modifier
							.fillMaxWidth(0.475f / 0.525f)
							.height(60.dp)
							.border(
								2.dp,
								textFieldBorderColor,
								RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
							),
						placeholder = {
							Text(
								text = "MM/DD/YYYY",
								color = secondaryTextColor,
							)
						},
						label = {
							Text(
								text = stringResource(id = R.string.end_date),
								color = secondaryTextColor,
							)
						},
						trailingIcon = {
							IconButton(
								onClick = {
									coroutineScope.launch {
										openBottomSheet = true
									}
								}
							) {
								Icon(
									painter = painterResource(
										id = R.drawable.calendar_outline_gray_24
									),
									contentDescription = stringResource(id = R.string.end_date),
								)
							}
						},
						value = endButtonText,
						readOnly = true,
						onValueChange = { }
					)
				}

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
				)

				RoundedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.height(120.dp)
						.border(
							2.dp,
							textFieldBorderColor,
							RoundedCornerShape(dimensionResource(id = R.dimen.s))
						),
					placeholder = {
						Text(
							text = stringResource(id = R.string.description),
							color = secondaryTextColor,
						)
					},
					label = {
						Text(
							text = stringResource(id = R.string.description),
							color = secondaryTextColor,
						)
					},
					shape = RoundedCornerShape(dimensionResource(id = R.dimen.s)),
					singleLine = false,
					value = uiState.description,
					onValueChange = {
						viewModel.descriptionStream.onNext(it)
//						viewModel.updateUiState(
//							CreateListingPageUiState.Loaded(
//								address = uiState.address,
//								description = it,
//								price = uiState.price,
//								numBedrooms = uiState.numBedrooms,
//								startDate = uiState.startDate,
//								endDate = uiState.endDate,
//								housingType = uiState.housingType,
//							)
//						)
					}
				)

				if (openBottomSheet) {
					DatePickerBottomSheet(bottomSheetState, dateRangePickerState, onDismissRequest = { openBottomSheet = false },
						onClick = {
						coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
							if (!bottomSheetState.isVisible) {
								openBottomSheet = false
							}
							if (dateRangePickerState.selectedStartDateMillis != null) {
								startButtonText =
									displayDateFormatter.formatDate(dateRangePickerState.selectedStartDateMillis, locale = Locale.getDefault())!!
								val startDate = SimpleDateFormat("MM/dd/yyyy").parse(startButtonText)

								viewModel.startDateStream.onNext(if (startDate is Date) storeDateFormat.format(startDate) else uiState.startDate)
//								viewModel.updateUiState(
//									CreateListingPageUiState.Loaded(
//										address = uiState.address,
//										description = uiState.description,
//										price = uiState.price,
//										numBedrooms = uiState.numBedrooms,
//										startDate = if (startDate is Date) storeDateFormat.format(startDate) else uiState.startDate,
//										endDate = uiState.endDate,
//										housingType = uiState.housingType,
//									)
//								)
							} else {
								startButtonText = ""
								viewModel.startDateStream.onNext("")
//								viewModel.updateUiState(
//									CreateListingPageUiState.Loaded(
//										address = uiState.address,
//										description = uiState.description,
//										price = uiState.price,
//										numBedrooms = uiState.numBedrooms,
//										startDate = "",
//										endDate = uiState.endDate,
//										housingType = uiState.housingType,
//									)
//								)
							}
							if (dateRangePickerState.selectedEndDateMillis != null) {
								endButtonText =
									displayDateFormatter.formatDate(dateRangePickerState.selectedEndDateMillis, locale = Locale.getDefault())!!
								val endDate = SimpleDateFormat("MM/dd/yyyy").parse(endButtonText)
								viewModel.endDateStream.onNext(if (endDate is Date) storeDateFormat.format(endDate) else uiState.endDate)

//								viewModel.updateUiState(
//									CreateListingPageUiState.Loaded(
//										address = uiState.address,
//										description = uiState.description,
//										price = uiState.price,
//										numBedrooms = uiState.numBedrooms,
//										startDate = uiState.startDate,
//										endDate = if (endDate is Date) storeDateFormat.format(endDate) else uiState.endDate,
//										housingType = uiState.housingType,
//									)
//								)
							} else {
								endButtonText = ""
								viewModel.endDateStream.onNext("")
//								viewModel.updateUiState(
//									CreateListingPageUiState.Loaded(
//										address = uiState.address,
//										description = uiState.description,
//										price = uiState.price,
//										numBedrooms = uiState.numBedrooms,
//										startDate = uiState.startDate,
//										endDate = "",
//										housingType = uiState.housingType,
//									)
//								)
							}
						}
					})
				}

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
				)

				Text(
					text = stringResource(id = R.string.upload_images),
					style = MaterialTheme.typography.titleSmall
				)

				Spacer(
					modifier = Modifier.weight(weight = 1.0f)
				)

				UploadImages()

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
				)

				PrimaryButton(
					modifier = Modifier
						.fillMaxWidth()
						.height(60.dp)
						.padding(bottom = 10.dp),
					onClick = {
					  /* TODO */
						viewModel.createListingStream.onNext(uiState)
					},
				) {
					Text(
						text = stringResource(id = R.string.create_post),
						color = textOnSubletrPink,
					)
				}
			}
		}
	}
}

private const val ELEMENT_WIDTH = 0.9f

/*TODO: FIX STORE DATE FORMAT TIMEZONE?*/
val storeDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
val displayDateFormatter = DatePickerDefaults.dateFormatter(selectedDateSkeleton = "MM/dd/yyyy")

@Composable
fun UploadImages() {
	var imageUri by remember {
		mutableStateOf<Uri?>(null)
	}
	val context = LocalContext.current
	val bitmap =  remember {
		mutableStateOf<Bitmap?>(null)
	}

	val launcher = rememberLauncherForActivityResult(contract =
	ActivityResultContracts.GetContent()) { uri: Uri? ->
		imageUri = uri
	}
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Button(
			modifier = Modifier
				.height(150.dp)
				.width(150.dp)
				.border(
					2.dp,
					textFieldBorderColor,
					RoundedCornerShape(dimensionResource(id = R.dimen.s))
				),
			shape = RoundedCornerShape(dimensionResource(id = R.dimen.s)),
			onClick = {
				launcher.launch("image/*")
			},
			colors = ButtonDefaults.buttonColors(
				containerColor = textFieldBackgroundColor,
				contentColor = Color(0xFF808080)

			),
		) {
			Icon(
				painter = painterResource(
					id = R.drawable.add_photo_solid_gray_24
				),
				contentDescription = stringResource(id = R.string.back_arrow),
			)
		}

		Spacer(modifier = Modifier.width(12.dp))

		imageUri?.let {
			if (Build.VERSION.SDK_INT < 28) {
				bitmap.value = MediaStore.Images
					.Media.getBitmap(context.contentResolver,it)

			} else {
				val source = ImageDecoder
					.createSource(context.contentResolver,it)
				bitmap.value = ImageDecoder.decodeBitmap(source)
			}

			bitmap.value?.let {  btm ->
				Image(bitmap = btm.asImageBitmap(),
					contentDescription = null,
					modifier = Modifier
						.size(150.dp)
						.border(
							2.dp,
							textFieldBorderColor,
							RoundedCornerShape(dimensionResource(id = R.dimen.s))
						)
						.clip(RoundedCornerShape(dimensionResource(id = R.dimen.s))),
					contentScale = ContentScale.FillBounds)
			}
		}

	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerBottomSheet(
	bottomSheetState: SheetState,
	state: DateRangePickerState,
	onDismissRequest: () -> Unit,
	onClick: () -> Unit,
) {
	ModalBottomSheet(
		onDismissRequest = onDismissRequest,
		sheetState = bottomSheetState,
		content = {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(550.dp)
					.background(Color.White)
			) {
				LeaseDatePicker(state, onClick)
				Button(
					onClick = onClick,
					colors = ButtonDefaults.buttonColors(
						containerColor = subletrPink,
					),
					modifier = Modifier
						.align(Alignment.BottomCenter)
						.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
						.fillMaxWidth()
						.height(50.dp)
				) {
					Text("Done", color = textOnSubletrPink)
				}
			}
		},
		dragHandle = {}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaseDatePicker(state: DateRangePickerState, onClick: () -> Unit) {
	Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(start = 12.dp, end = 12.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			IconButton(onClick = onClick) {
				Icon(Icons.Filled.Close, contentDescription = "Localized description")
			}
		}

		DateRangePicker(state = state,
			modifier = Modifier.weight(1f),
			title = {
				Text(
					text = "Lease Start - End dates",
					modifier = Modifier
						.padding(start = 30.dp, end = 12.dp),
					color = secondaryTextColor,
				)
			},
			headline = {
				Row(
					modifier = Modifier
						.clearAndSetSemantics {
							liveRegion = LiveRegionMode.Polite
						}
						.padding(start = 30.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(4.dp),
				) {
					val dateFormatter = DatePickerDefaults.dateFormatter()
					Text(
						text =
							if (state.selectedStartDateMillis != null )
								dateFormatter.formatDate(state.selectedStartDateMillis, locale = Locale.getDefault())!!
							else "Start date",
						style = MaterialTheme.typography.displayLarge
					)
					Text(
						text = "-",
						style = MaterialTheme.typography.displayLarge
					)
					Text(
						text =
							if (state.selectedEndDateMillis != null )
								dateFormatter.formatDate(state.selectedEndDateMillis, locale = Locale.getDefault())!!
							else "End date",
						style = MaterialTheme.typography.displayLarge
					)
				}
			},
			colors = DatePickerDefaults.colors(
				containerColor = Color.White,
				dayInSelectionRangeContainerColor = textFieldBackgroundColor,
				selectedDayContainerColor = subletrPink,
			)
		)
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
				address = "",
				description = "",
				price = "",
				numBedrooms = "",
				startDate = "",
				endDate = "",
				housingType = HousingType.OTHER,
			)
		)
	}
}
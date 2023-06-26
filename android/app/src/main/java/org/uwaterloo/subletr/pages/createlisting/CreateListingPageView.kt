@file:Suppress("CyclomaticComplexMethod", "ForbiddenComment")

package org.uwaterloo.subletr.pages.createlisting

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
import org.uwaterloo.subletr.utils.toBase64String
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

	var addressStr by remember { mutableStateOf("") }

	val numberPattern = remember { Regex("^\\d+\$") }

	val bitmap =  remember { mutableStateOf<Bitmap?>(null) }

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
					.padding(
						start = dimensionResource(id = R.dimen.s),
						end = dimensionResource(id = R.dimen.s)
					),
				verticalArrangement = Arrangement.Center,
			) {

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
				)

				RoundedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.height(dimensionResource(id = R.dimen.xxl))
						.border(
							dimensionResource(id = R.dimen.xxxs),
							textFieldBorderColor,
							RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
						),
					placeholder = {
						Text(
							text = stringResource(id = R.string.address_format_label),
							color = secondaryTextColor,
						)
					},
					label = {
						Text(
							text = stringResource(id = R.string.address),
							color = secondaryTextColor,
						)
					},
					value = addressStr,
					onValueChange = {
						addressStr = it

						val splitAddress = it.split(",").toTypedArray()
						if (splitAddress.isNotEmpty()) {
							viewModel.addressLineStream.onNext(splitAddress[0])
						}
						if (splitAddress.size >= 2) {
							viewModel.addressCityStream.onNext(splitAddress[1])
						}
						if (splitAddress.size >= 3) {
							viewModel.addressPostalCodeStream.onNext(splitAddress[2])
						}
					}
				)

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
				)

				RoundedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.height(dimensionResource(id = R.dimen.xxl))
						.border(
							dimensionResource(id = R.dimen.xxxs),
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
					modifier = Modifier.weight(weight = 2.0f)
				)

				RoundedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.height(dimensionResource(id = R.dimen.xxl))
						.border(
							dimensionResource(id = R.dimen.xxxs),
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
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					value = if (uiState.numBedrooms == 0) "" else uiState.numBedrooms.toString(),
					onValueChange = {
						if (it.isEmpty()) {
							viewModel.numBedroomsStream.onNext(0)
						} else if (it.matches(numberPattern)) {
							viewModel.numBedroomsStream.onNext(it.toInt())
						}
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
							.height(dimensionResource(id = R.dimen.xxl))
							.border(
								dimensionResource(id = R.dimen.xxxs),
								textFieldBorderColor,
								RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
							),
						placeholder = {
							Text(
								text = stringResource(id = R.string.placeholder_date),
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
								   contentDescription = stringResource(id = R.string.open_date_picker),
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
							.height(dimensionResource(id = R.dimen.xxl))
							.border(
								dimensionResource(id = R.dimen.xxxs),
								textFieldBorderColor,
								RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
							),
						placeholder = {
							Text(
								text = stringResource(id = R.string.placeholder_date),
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
									contentDescription = stringResource(id = R.string.open_date_picker),
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
						.height(dimensionResource(id = R.dimen.xxxxxxl))
						.border(
							dimensionResource(id = R.dimen.xxxs),
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
					}
				)

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

				UploadImages(bitmap)

				Spacer(
					modifier = Modifier.weight(weight = 2.0f)
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
									viewModel.startDateStream.onNext(
										if (startDate is Date)
											storeDateFormat.format(startDate)
										else uiState.startDate)
								} else {
									startButtonText = ""
									viewModel.startDateStream.onNext("")
								}
								if (dateRangePickerState.selectedEndDateMillis != null) {
									endButtonText =
										displayDateFormatter.formatDate(dateRangePickerState.selectedEndDateMillis, locale = Locale.getDefault())!!
									val endDate = SimpleDateFormat("MM/dd/yyyy").parse(endButtonText)
									viewModel.endDateStream.onNext(
										if (endDate is Date)
											storeDateFormat.format(endDate)
										else uiState.endDate)
								} else {
									endButtonText = ""
									viewModel.endDateStream.onNext("")
								}
							}
						})
				}

				if (bitmap.value != null) {
					val base64ImageString = bitmap.value!!.toBase64String()
					val images = MutableList(1) { base64ImageString }
					viewModel.imagesByteStream.onNext(images)
				}

				PrimaryButton(
					modifier = Modifier
						.fillMaxWidth()
						.height(dimensionResource(id = R.dimen.xxl))
						.padding(bottom = dimensionResource(id = R.dimen.xs)),
					onClick = {
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

val storeDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

@OptIn(ExperimentalMaterial3Api::class)
val displayDateFormatter = DatePickerDefaults.dateFormatter(selectedDateSkeleton = "MM/dd/yyyy")

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
					.height(550.dp) // TODO: change this to not use explicit value
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
						.padding(
							start = dimensionResource(id = R.dimen.s),
							end = dimensionResource(id = R.dimen.s),
							bottom = dimensionResource(id = R.dimen.s)
						)
						.fillMaxWidth()
						.height(dimensionResource(id = R.dimen.xl))
				) {
					Text(stringResource(id = R.string.done), color = textOnSubletrPink)
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
				.padding(
					start = dimensionResource(id = R.dimen.xs),
					end = dimensionResource(id = R.dimen.xs)
				),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			IconButton(onClick = onClick) {
				Icon(Icons.Filled.Close, contentDescription = stringResource(id = R.string.close))
			}
		}

		DateRangePicker(state = state,
			modifier = Modifier.weight(1f),
			title = {
				Text(
					text = stringResource(id = R.string.lease_start_end_dates),
					modifier = Modifier
						.padding(start = dimensionResource(id = R.dimen.l), end = dimensionResource(id = R.dimen.xs)),
					color = secondaryTextColor,
				)
			},
			headline = {
				Row(
					modifier = Modifier
						.clearAndSetSemantics {
							liveRegion = LiveRegionMode.Polite
						}
						.padding(start = dimensionResource(id = R.dimen.l)),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxs)),
				) {
					val dateFormatter = DatePickerDefaults.dateFormatter()
					Text(
						text =
							if (state.selectedStartDateMillis != null )
								dateFormatter.formatDate(state.selectedStartDateMillis, locale = Locale.getDefault())!!
							else stringResource(id = R.string.start_date),
						style = MaterialTheme.typography.displayLarge
					)
					Text(
						text = stringResource(id = R.string.dash),
						style = MaterialTheme.typography.displayLarge
					)
					Text(
						text =
							if (state.selectedEndDateMillis != null )
								dateFormatter.formatDate(state.selectedEndDateMillis, locale = Locale.getDefault())!!
							else stringResource(id = R.string.end_date),
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

@Composable
fun UploadImages(bitmap: MutableState<Bitmap?>) {
	var imageUri by remember {
		mutableStateOf<Uri?>(null)
	}
	val context = LocalContext.current

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
				.size(dimensionResource(id = R.dimen.xxxxxxl))
				.border(
					dimensionResource(id = R.dimen.xxxs),
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
				contentDescription = stringResource(id = R.string.upload_images),
			)
		}

		Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.s)))

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
						.size(dimensionResource(id = R.dimen.xxxxxxl))
						.border(
							dimensionResource(id = R.dimen.xxxs),
							textFieldBorderColor,
							RoundedCornerShape(dimensionResource(id = R.dimen.s))
						)
						.clip(RoundedCornerShape(dimensionResource(id = R.dimen.s))),
					contentScale = ContentScale.FillBounds)
			}
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
				addressLine = "",
				addressCity = "",
				addressPostalCode = "",
				addressCountry = stringResource(id = R.string.canada),
				description = "",
				price = 0,
				numBedrooms = 0,
				startDate = "",
				endDate = "",
				housingType = HousingType.OTHER,
				images = ArrayList()
			)
		)
	}
}

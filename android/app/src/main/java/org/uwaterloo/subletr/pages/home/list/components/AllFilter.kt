package org.uwaterloo.subletr.pages.home.list.components

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.bottomsheet.DatePickerBottomSheet
import org.uwaterloo.subletr.components.button.DateInputButton
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.components.rating.StaticRatingsBar
import org.uwaterloo.subletr.components.switch.PrimarySwitch
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.pages.account.PageDivider
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.theme.filterBoldFont
import org.uwaterloo.subletr.theme.filterRegularFont
import org.uwaterloo.subletr.theme.subletrPalette
import org.uwaterloo.subletr.utils.displayDateFormatter
import org.uwaterloo.subletr.utils.parseUTCDateTimeToLocal
import org.uwaterloo.subletr.utils.storeDateFormatISO
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale

@SuppressWarnings("CyclomaticComplexMethod","LongMethod","ComplexCondition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AllFilter(
	currentFilterVals: HomePageUiState.FiltersModel,
	updateFilterVals: (HomePageUiState.FiltersModel) -> Unit,
	coroutineScope: CoroutineScope,
	closeAction: () -> Unit,
) {
	val listState: LazyListState = rememberLazyListState()
	var housingPref by remember {
		mutableStateOf(
			currentFilterVals.housingType
		)
	}
	var startDateButtonText by remember {
		mutableStateOf(
			currentFilterVals.dateRange.startingDate?.let {
				parseUTCDateTimeToLocal(it)
			} ?: ""
		)

	}
	var endDateButtonText by remember {
		mutableStateOf(
			currentFilterVals.dateRange.endingDate?.let {
				parseUTCDateTimeToLocal(it)
			} ?: ""
		)
	}
	var lowerPriceBoundText by remember {
		mutableStateOf(
			currentFilterVals.priceRange.lowerBound?.toString() ?: ""
		)

	}
	var upperPriceBoundText by remember {
		mutableStateOf(
			currentFilterVals.priceRange.upperBound?.toString() ?: ""
		)
	}
	var lowerLocBound by remember {
		mutableIntStateOf(
			currentFilterVals.locationRange.lowerBound ?: 0
		)
	}

	var upperLocBound by remember {
		mutableIntStateOf(
			currentFilterVals.locationRange.upperBound ?: MAX_LOCATION_RANGE
		)
	}
	var sliderPosition by remember { mutableStateOf(lowerLocBound.toFloat()..upperLocBound.toFloat()) }
	var lowerLocBoundText by remember {
		mutableStateOf(
			currentFilterVals.locationRange.lowerBound?.toString() ?: ""
		)
	}
	var upperLocBoundText by remember {
		mutableStateOf(
			currentFilterVals.locationRange.upperBound?.toString() ?: ""
		)
	}
	var isFavourite by remember {
		mutableStateOf(
			currentFilterVals.favourite
		)
	}
	var lowerLocTextFieldError by remember { mutableStateOf(false) }
	var upperLocTextFieldError by remember { mutableStateOf(false) }
	var lowerPriceTextFieldError by remember { mutableStateOf(false) }
	var upperPriceTextFieldError by remember { mutableStateOf(false) }
	val dateRangePickerState = rememberDateRangePickerState()
	var openDatePicker by rememberSaveable { mutableStateOf(false) }
	var bedroomForSublet by remember {
		mutableStateOf(
			currentFilterVals.roomRange.bedroomForSublet
		)
	}
	var bedroomInProperty by remember {
		mutableStateOf(
			currentFilterVals.roomRange.bedroomInProperty
		)
	}
	var bathroom by remember {
		mutableStateOf(
			currentFilterVals.roomRange.bathroom
		)
	}
	var ensuitebathroom by remember {
		mutableStateOf(
			currentFilterVals.roomRange.ensuiteBathroom
		)
	}
	var genderPref by remember {
		mutableStateOf(
			currentFilterVals.gender
		)
	}

	var ratingPref by remember {
		mutableIntStateOf(
			currentFilterVals.minRating
		)
	}

	val datePickerBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	BasicFilterLayout(
		modifier = Modifier.fillMaxHeight(1.0f),
		titleId = R.string.all_filters,
		contentModifier = Modifier.fillMaxHeight(1.0f),
		content = {
			LazyColumn(
				modifier = Modifier
					.fillMaxWidth(1.0f),
				userScrollEnabled = true,
				state = listState,
				verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s))
			) {
				item { FilterDefaultDivider(modifier = Modifier.shadow(dimensionResource(id = R.dimen.xxxs))) }
				item {
					Text(
						text = stringResource(id = R.string.dates),
						style = MaterialTheme.typography.displayMedium,
						color = MaterialTheme.subletrPalette.primaryTextColor,
					)
				}
				item {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
						verticalAlignment = Alignment.CenterVertically,
					) {
						DateInputButton(
							modifier = Modifier
								.fillMaxWidth()
								.weight(1f)
								.border(
									width = dimensionResource(id = R.dimen.xxxs),
									color = MaterialTheme.subletrPalette.textFieldBorderColor,
									shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl)),
								),
							labelStringId = R.string.start_date,
							value = startDateButtonText,
							onClick = {
								coroutineScope.launch {
									openDatePicker = true
								}
							},
						)
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
						DateInputButton(
							modifier = Modifier
								.fillMaxWidth()
								.weight(1f)
								.border(
									width = dimensionResource(id = R.dimen.xxxs),
									color = MaterialTheme.subletrPalette.textFieldBorderColor,
									shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl)),
								),
							labelStringId = R.string.end_date,
							value = endDateButtonText,
							onClick = {
								coroutineScope.launch {
									openDatePicker = true
								}
							},)
					}

					if (openDatePicker) {
						DatePickerBottomSheet(
							datePickerBottomSheetState, dateRangePickerState,
							onDismissRequest = { openDatePicker = false },
							onClick = {
								coroutineScope.launch { datePickerBottomSheetState.hide() }
									.invokeOnCompletion {
										if (!datePickerBottomSheetState.isVisible) {
											openDatePicker = false
										}
										startDateButtonText =
											displayDateFormatter.formatDate(
												dateRangePickerState.selectedStartDateMillis,
												locale = Locale.getDefault()
											) ?: ""

										endDateButtonText =
											displayDateFormatter.formatDate(
												dateRangePickerState.selectedEndDateMillis,
												locale = Locale.getDefault()
											) ?: ""

									}
							},
						)
					}
				}
				item { PageDivider() }
				item {
					Text(
						text = stringResource(id = R.string.price),
						style = MaterialTheme.typography.displayMedium,
						color = MaterialTheme.subletrPalette.primaryTextColor,
					)
				}
				item {
					Row(
						modifier = Modifier
							.fillMaxWidth(1.0f),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.SpaceBetween,
					) {

						TextFieldWithErrorIndication(
							value = lowerPriceBoundText,
							onValueChange = {
								lowerPriceBoundText = it
								lowerPriceTextFieldError =
									!verifyNewBoundVal(
										newVal = lowerPriceBoundText,
										upperBound = upperPriceBoundText
									)
							},
							isError = lowerPriceTextFieldError,
							placeholderString = stringResource(id = R.string.minimum),
						)
						Divider(
							modifier = Modifier.width(width = dimensionResource(id = R.dimen.m)),
							color = MaterialTheme.subletrPalette.secondaryTextColor,
							thickness = dimensionResource(id = R.dimen.xxxxs),
						)
						TextFieldWithErrorIndication(
							value = upperPriceBoundText,
							onValueChange = {
								upperPriceBoundText = it
								upperPriceTextFieldError =
									!verifyNewBoundVal(
										newVal = upperPriceBoundText,
										lowerBound = lowerPriceBoundText
									)
							},
							isError = upperPriceTextFieldError,
							placeholderString = stringResource(id = R.string.maximum),

							)
					}
				}
				item { PageDivider() }
				item {
					Text(
						text = stringResource(id = R.string.location),
						style = MaterialTheme.typography.displayMedium,
						color = MaterialTheme.subletrPalette.primaryTextColor,
					)
				}
				item {
					Column(
						modifier = Modifier
							.fillMaxWidth(1.0f)
							.wrapContentHeight(),
					) {
						RangeSlider(
							modifier = Modifier
								.fillMaxWidth(1.0f),
							value = sliderPosition,
							onValueChange = {
								sliderPosition = it
								lowerLocBoundText = it.start.toInt().toString()
								upperLocBoundText = it.endInclusive.toInt().toString()
							},
							valueRange = 0f..MAX_LOCATION_RANGE.toFloat(),
							colors = SliderDefaults.colors(
								thumbColor = MaterialTheme.subletrPalette.primaryBackgroundColor,
								activeTrackColor = MaterialTheme.subletrPalette.subletrPink,
								inactiveTrackColor = MaterialTheme.subletrPalette.darkerGrayButtonColor,
							),
							startThumb = {
								SliderDefaults.Thumb(
									modifier = Modifier
										.shadow(dimensionResource(id = R.dimen.xxs), CircleShape)
										.size(dimensionResource(id = R.dimen.m)),
									interactionSource = remember { MutableInteractionSource() },
									colors = SliderDefaults.colors(
										thumbColor = MaterialTheme.subletrPalette.primaryBackgroundColor,
									),
									enabled = true,
								)
							},
							endThumb = {
								SliderDefaults.Thumb(
									modifier = Modifier
										.shadow(dimensionResource(id = R.dimen.xxs), CircleShape)
										.size(dimensionResource(id = R.dimen.m)),
									interactionSource = remember { MutableInteractionSource() },
									colors = SliderDefaults.colors(
										thumbColor = MaterialTheme.subletrPalette.primaryBackgroundColor,
									),
									enabled = true,
								)
							},
						)
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
						Text(
							text = stringResource(id = R.string.Distance_instruction),
							style = filterRegularFont,
							color = MaterialTheme.subletrPalette.secondaryTextColor,
						)
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.s)))
						Row(
							modifier = Modifier.fillMaxWidth(1.0f),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.SpaceBetween,
						) {
							TextFieldWithErrorIndication(
								value = lowerLocBoundText,
								onValueChange = {
									lowerLocBoundText = it
									lowerLocTextFieldError =
										!verifyNewBoundVal(
											newVal = lowerLocBoundText,
											upperBound = upperLocBoundText,
										)
									val newNum = it.trim().toIntOrNull()
									if (newNum == null || lowerLocTextFieldError || newNum > MAX_LOCATION_RANGE) {
										sliderPosition = (0f..sliderPosition.endInclusive)
									} else {
										lowerLocTextFieldError = false
										sliderPosition =
											(newNum.toFloat()..sliderPosition.endInclusive)
									}
								},
								isError = lowerLocTextFieldError,
								placeholderString = stringResource(id = R.string.minimum),
								suffix = {
									Text(
										text = stringResource(id = R.string.km),
										style = filterBoldFont,
									)
								},

								)
							Divider(
								modifier = Modifier.width(width = dimensionResource(id = R.dimen.m)),
								color = MaterialTheme.subletrPalette.secondaryTextColor,
								thickness = dimensionResource(id = R.dimen.xxxxs),
							)
							TextFieldWithErrorIndication(
								value = upperLocBoundText,
								onValueChange = {
									upperLocBoundText = it
									upperLocTextFieldError =
										!verifyNewBoundVal(
											newVal = upperLocBoundText,
											lowerBound = lowerLocBoundText
										)
									val newNum = it.trim().toIntOrNull()
									if (newNum == null || upperLocTextFieldError || newNum > MAX_LOCATION_RANGE) {
										sliderPosition =
											(sliderPosition.start..MAX_LOCATION_RANGE.toFloat())
									} else {
										upperLocTextFieldError = false
										sliderPosition = (sliderPosition.start..newNum.toFloat())
									}
								},
								isError = upperLocTextFieldError,
								placeholderString = stringResource(id = R.string.maximum),
								suffix = {
									Text(
										text = stringResource(id = R.string.km),
										style = filterBoldFont,
									)
								},
							)
						}
					}
				}
				item { PageDivider() }
				item {
					Text(
						text = stringResource(id = R.string.rooms),
						style = MaterialTheme.typography.displayMedium,
						color = MaterialTheme.subletrPalette.primaryTextColor,
					)
				}
				item {
					Column(
						modifier = Modifier
							.fillMaxWidth(1.0f)
							.wrapContentHeight(),
						horizontalAlignment = Alignment.Start,
					) {
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
						Text(
							text = stringResource(id = R.string.bedrooms_for_sublet),
							style = filterRegularFont,
							color = MaterialTheme.subletrPalette.secondaryTextColor,
						)
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))

						Row(
							modifier = Modifier.fillMaxWidth(1.0f),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.SpaceBetween,
						) {

							DefaultFilterButton(
								isSelected = (bedroomForSublet == null),
								onClick = { bedroomForSublet = null },
								text = stringResource(
									id = R.string.any,
								),
							)
							for (i in 1..4) {
								DefaultFilterButton(
									isSelected = (bedroomForSublet == i),
									onClick = { bedroomForSublet = i },
									text = i.toString(),
								)
							}
							DefaultFilterButton(
								isSelected = (bedroomForSublet == 5),
								onClick = { bedroomForSublet = 5 },
								text = stringResource(
									id = R.string.five_plus,
								),
							)
						}
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))
						Text(
							text = stringResource(id = R.string.bedrooms_in_property),
							style = filterRegularFont,
							color = MaterialTheme.subletrPalette.secondaryTextColor,
						)
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
						Row(
							modifier = Modifier.fillMaxWidth(1.0f),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.SpaceBetween,
						) {
							DefaultFilterButton(
								isSelected = (bedroomInProperty == null),
								onClick = { bedroomInProperty = null },
								text = stringResource(
									id = R.string.any,
								),
							)
							for (i in 1..4) {
								DefaultFilterButton(
									isSelected = (bedroomInProperty == i),
									onClick = { bedroomInProperty = i },
									text = i.toString(),
								)
							}
							DefaultFilterButton(
								isSelected = (bedroomInProperty == 5),
								onClick = { bedroomInProperty = 5 },
								text = stringResource(
									id = R.string.five_plus,
								),
							)
						}
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))
						Text(
							text = stringResource(id = R.string.bathrooms),
							style = filterRegularFont,
							color = MaterialTheme.subletrPalette.secondaryTextColor,
						)
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
						Row(
							modifier = Modifier.fillMaxWidth(1.0f),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.SpaceBetween,
						) {

							DefaultFilterButton(
								isSelected = (bathroom == null),
								onClick = { bathroom = null },
								text = stringResource(
									id = R.string.any,
								),
							)
							for (i in 1..4) {
								DefaultFilterButton(
									isSelected = (bathroom == i),
									onClick = { bathroom = i },
									text = i.toString(),
								)
							}
							DefaultFilterButton(
								isSelected = (bathroom == 5),
								onClick = { bathroom = 5 },
								text = stringResource(
									id = R.string.five_plus,
								),
							)
						}
						Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.m)))
						Row(
							modifier = Modifier.fillMaxWidth(1.0f),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically,
						) {
							Text(
								text = stringResource(id = R.string.ensuite_bathroom),
								style = filterRegularFont,
								color = MaterialTheme.subletrPalette.secondaryTextColor,
							)
							PrimarySwitch(
								modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
								checked = ensuitebathroom,
								onCheckedChange = { ensuitebathroom = it },
								colors = SwitchDefaults.colors(
									checkedTrackColor = MaterialTheme.subletrPalette.subletrPink,
									checkedBorderColor = MaterialTheme.subletrPalette.subletrPink,
									uncheckedTrackColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
									uncheckedBorderColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
									checkedThumbColor = MaterialTheme.subletrPalette.textOnSubletrPink,
									uncheckedThumbColor = MaterialTheme.subletrPalette.textOnSubletrPink,
								)
							)
						}


					}
				}
				item { PageDivider() }
				item {
					Text(
						text = stringResource(id = R.string.property_type),
						style = MaterialTheme.typography.displayMedium,
						color = MaterialTheme.subletrPalette.primaryTextColor,
					)
				}
				item {
					LazyVerticalGrid(
						modifier = Modifier.height(144.dp),
						columns = GridCells.Fixed(3),
						horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
						content = {
							item {
								DefaultFilterButton(
									isSelected = (housingPref == HousingType.OTHER),
									onClick = { housingPref = HousingType.OTHER },
									text = stringResource(
										id = R.string.any,
									),
								)
							}
							HousingType.values().map {
								if (it != HousingType.OTHER) {
									item {
										DefaultFilterButton(
											isSelected = (housingPref == it),
											onClick = { housingPref = it },
											text = stringResource(
												id = it.stringId,
											),
										)
									}
								}
							}
						},
					)
				}
				item { PageDivider() }
				item {
					Text(
						text = stringResource(id = R.string.roommate_preference),
						style = MaterialTheme.typography.displayMedium,
						color = MaterialTheme.subletrPalette.primaryTextColor,
					)
				}
				item {
					Row(
						modifier = Modifier.fillMaxWidth(1.0f),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
					) {

						DefaultFilterButton(
							isSelected = (genderPref == Gender.OTHER),
							onClick = { genderPref = Gender.OTHER },
							text = stringResource(
								id = R.string.any,
							),
						)
						DefaultFilterButton(
							isSelected = (genderPref == Gender.FEMALE),
							onClick = { genderPref = Gender.FEMALE },
							text = stringResource(
								id = R.string.female,
							),
						)
						DefaultFilterButton(
							isSelected = (genderPref == Gender.MALE),
							onClick = { genderPref = Gender.MALE },
							text = stringResource(
								id = R.string.male,
							),
						)
					}
				}
				item { PageDivider() }
				item {
					Text(
						text = stringResource(id = R.string.favourite),
						style = MaterialTheme.typography.displayMedium,
						color = MaterialTheme.subletrPalette.primaryTextColor,
					)
				}
				item {
					Row(
						modifier = Modifier.fillMaxWidth(1.0f),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically,
					) {
						Text(
							text = stringResource(id = R.string.only_show_favourites),
							style = filterRegularFont,
							color = MaterialTheme.subletrPalette.secondaryTextColor,
						)
						PrimarySwitch(
							modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
							checked = isFavourite,
							onCheckedChange = { isFavourite = it },
							colors = SwitchDefaults.colors(
								checkedTrackColor = MaterialTheme.subletrPalette.subletrPink,
								checkedBorderColor = MaterialTheme.subletrPalette.subletrPink,
								uncheckedTrackColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
								uncheckedBorderColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
								checkedThumbColor = MaterialTheme.subletrPalette.textOnSubletrPink,
								uncheckedThumbColor = MaterialTheme.subletrPalette.textOnSubletrPink,
							)
						)
					}
				}
				item { PageDivider() }
				item {
					FlowRow(
						horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xs)),
						content = {
							DefaultFilterButton(
								isSelected = (ratingPref == 0),
								onClick = { ratingPref = 0 },
								text = stringResource(
									id = R.string.any,
								),
							)
							for (i in 5 downTo 1) {
								SecondaryButton(
									modifier = Modifier
										.defaultMinSize(
											minWidth = dimensionResource(id = R.dimen.xxxxs),
											minHeight = dimensionResource(id = R.dimen.xxxxs),
										),
									onClick = { ratingPref = i },
									contentPadding = PaddingValues(
										horizontal = dimensionResource(id = R.dimen.s),
										vertical = dimensionResource(id = R.dimen.xs),
									),
									colors = ButtonDefaults.buttonColors(
										containerColor =
										if (ratingPref == i) MaterialTheme.subletrPalette.subletrPink
										else MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
									)
								) {
									Row(
										modifier = Modifier.height(dimensionResource(id = R.dimen.m)),
										verticalAlignment = Alignment.CenterVertically,
										horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxs)),

										) {
										StaticRatingsBar(
											modifier = Modifier
												.height(dimensionResource(id = R.dimen.m))
												.padding(dimensionResource(id = R.dimen.xxs)),
											rating = i.toFloat(),
											ratingColor = if (ratingPref == i) MaterialTheme.subletrPalette.textOnSubletrPink
											else MaterialTheme.subletrPalette.primaryTextColor,
											onlyShowFilled = true,
										)
										if (i < 5) {
											Text(
												text = "& up",
												color = if (ratingPref == i) MaterialTheme.subletrPalette.textOnSubletrPink
												else MaterialTheme.subletrPalette.primaryTextColor,
											)

										}
									}
								}
							}
						},
					)
				}

				item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xxl))) }
			}


		},
		closeAction = closeAction,
		clearAction = {
			housingPref = HousingType.OTHER
			startDateButtonText = ""
			endDateButtonText = ""
			lowerPriceBoundText = ""
			upperPriceBoundText = ""
			lowerLocBound = 0
			upperLocBound = MAX_LOCATION_RANGE
			sliderPosition = (0f..MAX_LOCATION_RANGE.toFloat())
			lowerLocBoundText = ""
			upperLocBoundText = ""
			lowerLocTextFieldError = false
			upperLocTextFieldError = false
			lowerPriceTextFieldError = false
			upperPriceTextFieldError = false
			openDatePicker = false
			bedroomForSublet = null
			bedroomInProperty = null
			bathroom = null
			ensuitebathroom = false
			genderPref = Gender.OTHER
			ratingPref = 0


		},
		updateFilterAndClose = {
			if (!lowerLocTextFieldError && !upperLocTextFieldError && !lowerPriceTextFieldError && !upperPriceTextFieldError) {
				val newStartingDate = if (startDateButtonText.isNotEmpty())  SimpleDateFormat("MM/dd/yyyy").parse(startDateButtonText) else ""
				val newEndingDate = if (endDateButtonText.isNotEmpty()) SimpleDateFormat("MM/dd/yyyy").parse(endDateButtonText) else ""
				updateFilterVals(
					currentFilterVals.copy(
						priceRange = HomePageUiState.PriceRange(
							lowerPriceBoundText.toIntOrNull(),
							upperPriceBoundText.toIntOrNull()
						),
						roomRange = HomePageUiState.RoomRange(
							bedroomForSublet = bedroomForSublet,
							bedroomInProperty = bedroomInProperty,
							bathroom = bathroom,
							ensuiteBathroom = ensuitebathroom
						),
						gender = genderPref,
						housingType = housingPref,
						dateRange = HomePageUiState.DateRange(
							startingDate = if (newStartingDate is Date)
								newStartingDate.toInstant().atOffset(ZoneOffset.UTC).format(
									storeDateFormatISO
								) else null,
							endingDate = if (newEndingDate is Date)
								newEndingDate.toInstant().atOffset(ZoneOffset.UTC).format(
									storeDateFormatISO
								) else null,
						),
						favourite = isFavourite,
						locationRange = HomePageUiState.LocationRange(
							lowerLocBoundText.toIntOrNull(),
							upperLocBoundText.toIntOrNull(),
						),
						minRating = ratingPref,
					)
				)

			}
			closeAction()
		},
	)
}

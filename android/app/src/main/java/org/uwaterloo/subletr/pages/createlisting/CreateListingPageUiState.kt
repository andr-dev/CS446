package org.uwaterloo.subletr.pages.createlisting

import org.uwaterloo.subletr.enums.HousingType

sealed interface CreateListingPageUiState {
	object Loading : CreateListingPageUiState

	data class Loaded(
		val address: String,
		val description: String,
		val price: String,
		val numBedrooms: String,
		val startDate: String,
		val endDate: String,
		val housingType: HousingType,
	) : CreateListingPageUiState
}
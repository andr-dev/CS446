package org.uwaterloo.subletr.pages.createlisting

import org.uwaterloo.subletr.enums.HousingType

sealed interface CreateListingPageUiState {
	object Loading : CreateListingPageUiState

	data class Loaded(
		val addressLine: String,
		val addressCity: String,
		val addressPostalCode: String,
		val addressCountry: String,
		val description: String,
		val price: Int,
		val numBedrooms: Int,
		val startDate: String,
		val endDate: String,
		val housingType: HousingType,
		val images: MutableList<List<Int>>,
	) : CreateListingPageUiState
}

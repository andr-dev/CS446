package org.uwaterloo.subletr.pages.createlisting

import android.graphics.Bitmap
import com.google.android.libraries.places.api.model.AutocompletePrediction
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.enums.EnsuiteBathroomOption
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.enums.ListingForGenderOption

sealed interface CreateListingPageUiState {
	object Loading : CreateListingPageUiState

	data class AddressModel (
		val fullAddress: String,
		val addressLine: String,
		val addressCity: String,
		val addressPostalCode: String,
		val addressCountry: String
	)

	data class Loaded(
		val address: AddressModel,
		val addressAutocompleteOptions: ArrayList<AutocompletePrediction>,
		val isAutocompleting: Boolean,
		val description: String,
		val price: Int,
		val numBedrooms: Int,
		val totalNumBedrooms: Int,
		val numBathrooms: Int,
		val bathroomsEnsuite: EnsuiteBathroomOption,
		val totalNumBathrooms: Int,
		val gender: ListingForGenderOption,
		val startDate: String,
		val endDate: String,
		val startDateDisplayText: String,
		val endDateDisplayText: String,
		val housingType: HousingType,
		val imagesBitmap: MutableList<Bitmap?>,
		val images: List<String>,
	) : CreateListingPageUiState

	fun AddressModel.getInfoDisplay(): List<Pair<Int, String>> = listOf(
		Pair(R.string.address_line, addressLine),
		Pair(R.string.city, addressCity),
		Pair(R.string.postal_code, addressPostalCode),
		Pair(R.string.country, addressCountry),
	)
}

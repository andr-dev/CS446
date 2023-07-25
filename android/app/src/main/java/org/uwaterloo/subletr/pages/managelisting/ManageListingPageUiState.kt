package org.uwaterloo.subletr.pages.managelisting

import android.graphics.Bitmap
import com.google.android.libraries.places.api.model.AutocompletePrediction
import org.uwaterloo.subletr.api.models.UpdateListingRequest
import org.uwaterloo.subletr.enums.EnsuiteBathroomOption
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.enums.ListingForGenderOption

sealed interface ManageListingPageUiState {
	object Loading : ManageListingPageUiState

	data class AddressModel (
		val fullAddress: String,
		val addressLine: String,
		val addressCity: String,
		val addressPostalCode: String,
		val addressCountry: String
	)

	data class test(
		val address: String,
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
	)

	data class Loaded(
		val address: String,
		val images: List<Bitmap>,
		val isFetchingImages: Boolean,
		val editableFields: UpdateListingRequest,
		val startDateDisplay: String,
		val endDateDisplay: String,
	) : ManageListingPageUiState
}
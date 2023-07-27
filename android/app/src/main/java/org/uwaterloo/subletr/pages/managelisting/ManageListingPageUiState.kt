package org.uwaterloo.subletr.pages.managelisting

import android.graphics.Bitmap
import com.google.android.libraries.places.api.model.AutocompletePrediction
import org.uwaterloo.subletr.api.models.UpdateListingRequest
import org.uwaterloo.subletr.enums.EnsuiteBathroomOption
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.enums.ListingForGenderOption

sealed interface ManageListingPageUiState {
	object Loading : ManageListingPageUiState

	data class Loaded(
		val address: String,
		val images: List<Bitmap>,
		val isFetchingImages: Boolean,
		val editableFields: UpdateListingRequest,
		val startDateDisplay: String,
		val endDateDisplay: String,
		val attemptUpdate: Boolean,
	) : ManageListingPageUiState
}
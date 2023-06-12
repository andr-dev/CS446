package org.uwaterloo.subletr.pages.listingdetails

import android.graphics.Bitmap
import org.uwaterloo.subletr.api.models.ListingDetails

sealed interface ListingDetailsPageUiState {
	object Loading : ListingDetailsPageUiState

	data class Loaded(
		val listingDetails: ListingDetails,
		val favourited: Boolean,
		val images: List<Bitmap>,
	) : ListingDetailsPageUiState
}

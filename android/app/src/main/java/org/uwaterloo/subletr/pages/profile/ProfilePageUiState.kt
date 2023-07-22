package org.uwaterloo.subletr.pages.profile

import android.graphics.Bitmap
import org.uwaterloo.subletr.api.models.ListingDetails

sealed interface ProfilePageUiState {
	object Loading : ProfilePageUiState

	data class Loaded(
		val name: String,
		val listingId: Int?,
		val listingDetails: ListingDetails?,
		val listingImage: Bitmap?,
		val avatar: Bitmap?,
		val rating: Float,
		val verification: Boolean,
		val usersRating: Int,
	) : ProfilePageUiState
}
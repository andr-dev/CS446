package org.uwaterloo.subletr.pages.account

import android.graphics.Bitmap
import androidx.annotation.StringRes
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.enums.Gender

sealed interface AccountPageUiState {
	object Loading : AccountPageUiState

	data class Loaded(
		val lastName: String,
		val firstName: String,
		val gender: String,
		val settings: List<Setting>,
		val listingId: Int?,
		val listingDetails: ListingDetails?,
		val listingImage: Bitmap?,
	) : AccountPageUiState

	data class Setting(
		@StringRes
		val textStringId: Int,
		val toggleState: Boolean,
	)
}

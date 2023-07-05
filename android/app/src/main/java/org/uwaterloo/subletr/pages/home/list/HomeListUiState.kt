package org.uwaterloo.subletr.pages.home.list

import android.graphics.Bitmap
import androidx.annotation.StringRes
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.enums.RoomRange
import org.uwaterloo.subletr.pages.home.HomePageUiState
import java.util.Optional

sealed interface HomeListUiState: HomePageUiState {
	object Loading : HomeListUiState

	data class ListingItemsModel (
		val listings: List<ListingSummary>,
		val likedListings: Set<String>,
		val listingsImages: List<Bitmap?>,
	)

	data class LocationRange(
		var lowerBound: Int? = null,
		var upperBound: Int? = null,
	)

	data class PriceRange(
		var lowerBound: Int? = null,
		var upperBound: Int? = null,
	)

	data class Loaded(
		val locationRange: LocationRange,
		val priceRange: PriceRange,
		val roomRange: RoomRange,
		val listingItems: ListingItemsModel,
		@StringRes val infoTextStringId: Int?,
	) : HomeListUiState
}

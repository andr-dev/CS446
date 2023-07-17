package org.uwaterloo.subletr.pages.home

import android.graphics.Bitmap
import org.uwaterloo.subletr.api.models.ListingSummary

interface HomePageUiState {
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

	data class RoomRange(
		var bedroomForSublet: Int? = null,
		var bedroomInProperty: Int? = null,
		var bathroom: Int? = null,
		var ensuiteBathroom: Boolean = false,
	)
}

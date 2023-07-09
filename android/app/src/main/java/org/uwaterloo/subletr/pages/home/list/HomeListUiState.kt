package org.uwaterloo.subletr.pages.home.list

import android.graphics.Bitmap
import androidx.annotation.StringRes
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.pages.home.HomePageUiState
import java.util.Optional
import java.util.prefs.AbstractPreferences

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

	data class RoomRange(
		var bedroomForSublet: Int? = null,
		var bedroomInProperty: Int? = null,
		var bathroom: Int? = null,
		var ensuiteBathroom: Boolean = false,

	)
	data class Loaded(
		val locationRange: LocationRange,
		val priceRange: PriceRange,
		val roomRange: RoomRange,
		val listingItems: ListingItemsModel,
		val genderPreference: Gender,
		val houseTypePreference: HousingType,
		@StringRes val infoTextStringId: Int?,
	) : HomeListUiState
}

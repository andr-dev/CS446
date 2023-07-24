package org.uwaterloo.subletr.pages.home

import android.graphics.Bitmap
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType

interface HomePageUiState {
	data class ListingItemsModel (
		val listings: List<ListingSummary>,
		val likedListings: Set<String>,
		val listingsImages: List<Bitmap?>,
		val selectedListings: List<Boolean>,
		val timeToDestination: List<Float>,
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

	data class DateRange(
		var startingDate: String? = null,
		var endingDate: String? = null,
	)

	data class FiltersModel(
		val locationRange: LocationRange,
		val priceRange: PriceRange,
		val roomRange: RoomRange,
		val gender: Gender,
		val housingType: HousingType,
		val dateRange: DateRange,
		val favourite: Boolean,
		val timeToDestination: Float?,
		val addressSearch: String?,
		val minRating: Int,
	)

	enum class TransportationMethod {
		WALK,
		BIKE,
		BUS,
		CAR,
	}

	enum class HomePageViewType {
		LIST,
		MAP,
	}
}

fun HomePageUiState.FiltersModel.deepEquals(item: HomePageUiState.FiltersModel): Boolean {
	return this.locationRange == item.locationRange &&
			this.priceRange == item.priceRange &&
			this.roomRange == item.roomRange &&
			this.gender == item.gender &&
			this.housingType == item.housingType &&
			this.dateRange == item.dateRange &&
			this.favourite == item.favourite &&
			this.timeToDestination == item.timeToDestination &&
			this.addressSearch == item.addressSearch &&
			this.minRating == item.minRating
}

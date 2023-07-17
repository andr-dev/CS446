package org.uwaterloo.subletr.pages.home.map

import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.pages.home.list.HomeListUiState

interface HomeMapUiState: HomePageUiState {
	object Loading : HomeMapUiState

	enum class TransportationMethod {
		WALK,
		BIKE,
		BUS,
		ALL,
	}

	data class Loaded(
		val addressSearch: String,
		val timeToDestination: Float,
		val transportationMethod: TransportationMethod,
		val locationRange: HomePageUiState.LocationRange,
		val priceRange: HomePageUiState.PriceRange,
		val roomRange: HomePageUiState.RoomRange,
		val genderPreference: Gender,
		val houseTypePreference: HousingType,
		val listingItems: HomePageUiState.ListingItemsModel,
		val dateRange: HomeListUiState.DateRange,
	) : HomeMapUiState
}

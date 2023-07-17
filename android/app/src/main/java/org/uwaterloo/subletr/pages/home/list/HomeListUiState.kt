package org.uwaterloo.subletr.pages.home.list

import androidx.annotation.StringRes
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.pages.home.HomePageUiState

sealed interface HomeListUiState: HomePageUiState {
	object Loading : HomeListUiState

	data class DateRange(
		var startingDate: String? = null,
		var endingDate: String? = null,
	)

	data class Loaded(
		val locationRange: HomePageUiState.LocationRange,
		val priceRange: HomePageUiState.PriceRange,
		val roomRange: HomePageUiState.RoomRange,
		val genderPreference: Gender,
		val houseTypePreference: HousingType,
		val listingItems: HomePageUiState.ListingItemsModel,
		val filterFavourite: Boolean,
		val dateRange: DateRange,
		@StringRes val infoTextStringId: Int?,
	) : HomeListUiState

	object Failed : HomeListUiState
}

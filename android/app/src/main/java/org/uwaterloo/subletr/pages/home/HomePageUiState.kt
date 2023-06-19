package org.uwaterloo.subletr.pages.home

import androidx.annotation.StringRes
import org.uwaterloo.subletr.api.models.GetListingsResponse
import org.uwaterloo.subletr.enums.LocationRange
import org.uwaterloo.subletr.enums.PriceRange
import org.uwaterloo.subletr.enums.RoomRange

sealed interface HomePageUiState {
	object Loading : HomePageUiState

	data class Loaded(
		val locationRange: LocationRange,
		val priceRange: PriceRange,
		val roomRange: RoomRange,
		val listings: GetListingsResponse,
		@StringRes val infoTextStringId: Int?,
	) : HomePageUiState
}

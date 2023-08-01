package org.uwaterloo.subletr.pages.home.map

import org.uwaterloo.subletr.pages.home.HomePageUiState

interface HomeMapUiState: HomePageUiState {
	object Loading : HomeMapUiState

	data class Loaded(
		val addressSearch: String,
		val transportationMethod: HomePageUiState.TransportationMethod,
		val timeToDestination: Float,
		val filters: HomePageUiState.FiltersModel,
		val listingItems: List<HomePageUiState.ListingItem>,
		val userListingId: Int?,
	) : HomeMapUiState
}

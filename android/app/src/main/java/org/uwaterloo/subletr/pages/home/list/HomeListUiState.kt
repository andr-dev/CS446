package org.uwaterloo.subletr.pages.home.list

import org.uwaterloo.subletr.pages.home.HomePageUiState

sealed interface HomeListUiState: HomePageUiState {
	object Loading : HomeListUiState

	data class Loaded(
		val addressSearch: String,
		val filters: HomePageUiState.FiltersModel,
		val listingItems: List<HomePageUiState.ListingItem>,
		val userListingId: Int?,
	) : HomeListUiState

	object Failed : HomeListUiState
}

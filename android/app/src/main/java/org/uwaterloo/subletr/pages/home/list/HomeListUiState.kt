package org.uwaterloo.subletr.pages.home.list

import org.uwaterloo.subletr.pages.home.HomePageUiState

sealed interface HomeListUiState: HomePageUiState {
	object Loading : HomeListUiState

	data class Loaded(
		val filters: HomePageUiState.FiltersModel,
		val listingItems: HomePageUiState.ListingItemsModel,
	) : HomeListUiState

	object Failed : HomeListUiState
}

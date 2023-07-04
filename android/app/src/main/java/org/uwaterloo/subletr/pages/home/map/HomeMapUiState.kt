package org.uwaterloo.subletr.pages.home.map

import org.uwaterloo.subletr.pages.home.HomePageUiState

interface HomeMapUiState: HomePageUiState {
	object Loading : HomeMapUiState
}

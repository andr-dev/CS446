package org.uwaterloo.subletr.components.homepage

sealed interface HomePageUiState {
	object Loading : HomePageUiState

	data class Loaded(
		val titleBlackStringId: Int,
		val titlePinkStringId: Int,
		val emailStringId: Int,
		val passwordStringId: Int,
		val loginStringId: Int,
		val orStringId: Int,
		val createAccountStringId: Int,
	) : HomePageUiState
}

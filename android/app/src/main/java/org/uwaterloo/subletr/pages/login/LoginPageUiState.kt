package org.uwaterloo.subletr.pages.login

sealed interface LoginPageUiState {
	object Loading : LoginPageUiState

	data class Loaded(
		val titleBlackStringId: Int,
		val titlePinkStringId: Int,
		val emailStringId: Int,
		val passwordStringId: Int,
		val loginStringId: Int,
		val orStringId: Int,
		val createAccountStringId: Int,
	) : LoginPageUiState
}

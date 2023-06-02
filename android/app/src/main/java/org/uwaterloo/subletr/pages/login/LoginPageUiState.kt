package org.uwaterloo.subletr.pages.login

sealed interface LoginPageUiState {
	object Loading : LoginPageUiState

	data class Loaded(
		val email: String,
		val password: String,
	) : LoginPageUiState
}

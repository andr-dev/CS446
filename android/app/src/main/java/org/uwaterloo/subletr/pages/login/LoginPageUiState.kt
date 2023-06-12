package org.uwaterloo.subletr.pages.login

import androidx.annotation.StringRes

sealed interface LoginPageUiState {
	object Loading : LoginPageUiState

	data class Loaded(
		val email: String,
		val password: String,
		@StringRes val infoTextStringId: Int?,
	) : LoginPageUiState
}

package org.uwaterloo.subletr.pages.createaccount

import org.uwaterloo.subletr.enums.Gender

sealed interface CreateAccountPageUiState {
	object Loading : CreateAccountPageUiState

	data class Loaded(
		val firstName: String,
		val lastName: String,
		val email: String,
		val password: String,
		val confirmPassword: String,
		val gender: Gender,
	) : CreateAccountPageUiState
}

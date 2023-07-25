package org.uwaterloo.subletr.pages.createaccount

import androidx.annotation.StringRes
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
		@StringRes val firstNameInfoTextStringId: Int?,
		@StringRes val lastNameInfoTextStringId: Int?,
		@StringRes val emailInfoTextStringId: Int?,
		@StringRes val passwordInfoTextStringId: Int?,
		@StringRes val confirmPasswordInfoTextStringId: Int?,
		val accountCreationError: Boolean,
	) : CreateAccountPageUiState
}

package org.uwaterloo.subletr.pages.createaccount

sealed interface CreateAccountPageUiState {
	object NewAccount : CreateAccountPageUiState

	enum class Gender(val gender: String) {
		MALE("Male"),
		FEMALE("Female"),
		OTHER("Other")
	}

	data class NewAccountInfo(
		val firstName: String,
		val lastName: String,
		val email: String,
		val password: String,
		val confirmPassword: String,
		val gender: Gender
	) : CreateAccountPageUiState
}

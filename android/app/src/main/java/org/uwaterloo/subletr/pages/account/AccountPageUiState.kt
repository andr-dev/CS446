package org.uwaterloo.subletr.pages.account

import androidx.annotation.StringRes

sealed interface AccountPageUiState {
	object Loading : AccountPageUiState

	data class Loaded(
		val lastName: String,
		val firstName: String,
		val gender: Gender,
		val settings: List<Setting>,
	) : AccountPageUiState

	enum class Gender(val gender: String) {
		MALE("Male"),
		FEMALE("Female"),
		OTHER("Other"),
	}

	data class Setting(
		@StringRes
		val textStringId: Int,
		val toggleState: Boolean,
	)
}

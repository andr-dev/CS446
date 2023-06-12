package org.uwaterloo.subletr.pages.account

import androidx.annotation.StringRes
import org.uwaterloo.subletr.enums.Gender

sealed interface AccountPageUiState {
	object Loading : AccountPageUiState

	data class Loaded(
		val lastName: String,
		val firstName: String,
		val gender: Gender,
		val settings: List<Setting>,
	) : AccountPageUiState

	data class Setting(
		@StringRes
		val textStringId: Int,
		val toggleState: Boolean,
	)
}

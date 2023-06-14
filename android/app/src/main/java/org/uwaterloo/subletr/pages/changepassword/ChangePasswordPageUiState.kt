package org.uwaterloo.subletr.pages.changepassword

import androidx.annotation.StringRes

interface ChangePasswordPageUiState {
	object Loading : ChangePasswordPageUiState

	data class Loaded(
		val oldPassword: String,
		val newPassword: String,
		val confirmNewPassword: String,
		@StringRes val infoTextStringId: Int?,
	): ChangePasswordPageUiState
}

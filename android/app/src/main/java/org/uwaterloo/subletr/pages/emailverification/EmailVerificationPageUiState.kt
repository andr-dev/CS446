package org.uwaterloo.subletr.pages.emailverification

sealed interface EmailVerificationPageUiState {

	object Loading : EmailVerificationPageUiState

	data class Loaded(
		val verificationCode: List<String>,
	) : EmailVerificationPageUiState
}

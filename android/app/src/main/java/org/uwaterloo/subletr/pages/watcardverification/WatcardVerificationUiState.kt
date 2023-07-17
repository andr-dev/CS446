package org.uwaterloo.subletr.pages.watcardverification

import android.widget.ImageView

sealed interface WatcardVerificationUiState {
	object Loading : WatcardVerificationUiState

	data class Loaded(
		val watcard : ImageView?,
		var verified : Boolean,
	) : WatcardVerificationUiState
}
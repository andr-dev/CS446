package org.uwaterloo.subletr.pages.watcardverification

import android.media.Image
import android.widget.ImageView

sealed interface WatcardVerificationUiState {

	object Loading : WatcardVerificationUiState

	data class Loaded(
		val watcard : String?, // TODO: change this type
		var verified : Boolean,
	) : WatcardVerificationUiState
}
package org.uwaterloo.subletr.pages.watcardverification

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri


sealed interface WatcardVerificationUiState {

	object Loading : WatcardVerificationUiState

	data class Loaded(
		val watcard: Bitmap?,
		val verified: Boolean,
		val submitted: Boolean,
	) : WatcardVerificationUiState

}
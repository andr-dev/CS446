package org.uwaterloo.subletr.pages.individualchat

import android.graphics.Bitmap
import org.uwaterloo.subletr.models.ChatItemModel

sealed interface IndividualChatPageUiState {
	object Loading : IndividualChatPageUiState

	data class BasicInfo(
		val contactName: String,
		val address: String?,
		val avatar: Bitmap?,
	)

	data class Loaded(
		val basicInfo: BasicInfo,
		val chatItems: List<ChatItemModel>,
		val message: String,
	): IndividualChatPageUiState
}

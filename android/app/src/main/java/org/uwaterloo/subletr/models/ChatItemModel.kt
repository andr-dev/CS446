package org.uwaterloo.subletr.models

interface ChatItemModel {

	data class MyChatItem(
		val message: String,
	): ChatItemModel

	data class OtherChatItem(
		val message: String,
	): ChatItemModel
}

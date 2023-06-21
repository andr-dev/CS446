package org.uwaterloo.subletr.pages.chat

sealed interface ChatListingPageUiState {

	object Loading : ChatListingPageUiState

	data class Loaded(
		val users : MutableList<String>,
		val messageHistory : MutableMap<String, MutableList<String>>
	):ChatListingPageUiState
}
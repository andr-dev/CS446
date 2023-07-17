package org.uwaterloo.subletr.pages.chat

sealed interface ChatListingPageUiState {


	object Loading : ChatListingPageUiState

	data class Loaded(
		val contacts : List<Contact>
	) : ChatListingPageUiState
}

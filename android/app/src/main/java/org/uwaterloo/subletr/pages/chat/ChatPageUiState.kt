package org.uwaterloo.subletr.pages.chat

import org.uwaterloo.subletr.pages.account.AccountPageUiState

sealed interface ChatPageUiState {

	object Loading : ChatPageUiState

	data class Loaded(
		val users : MutableList<String>
	):ChatPageUiState
}
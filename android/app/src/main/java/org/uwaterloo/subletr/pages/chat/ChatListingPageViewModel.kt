package org.uwaterloo.subletr.pages.chat

import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject
import org.uwaterloo.subletr.infrastructure.SubletrViewModel

@HiltViewModel
class ChatListingPageViewModel @Inject constructor(
) : SubletrViewModel<ChatListingPageUiState>() {
	override val uiStateStream: BehaviorSubject<ChatListingPageUiState> = BehaviorSubject.createDefault(
		ChatListingPageUiState.Loaded(
			contacts = emptyList()
		)
	)
}

data class Contact(
	var name: String,
	var msg: List<String>,
	var unread : Boolean,
)

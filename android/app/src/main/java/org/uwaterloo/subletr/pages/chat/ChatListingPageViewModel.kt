package org.uwaterloo.subletr.pages.chat

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.apis.DefaultApi
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.pages.account.AccountPageUiState
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class ChatListingPageViewModel @Inject constructor(
	navigationService: INavigationService,
	defaultApi: DefaultApi,
) : ViewModel() {
	val uiStateStream: BehaviorSubject<ChatListingPageUiState> = BehaviorSubject.createDefault(
		ChatListingPageUiState.Loaded(
			contacts = emptyList()
		)
	)
}

class Contact(
	name: String = "Alex Lin",
	msg: List<String> = List(1){ "this is a test dialog" }
) {
	val name: String = name
	val msg: List<String> = msg
}
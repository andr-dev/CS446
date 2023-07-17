package org.uwaterloo.subletr.pages.individualchat

import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.models.ChatItemModel
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class IndividualChatPageViewModel @Inject constructor(
	private val navigationService: INavigationService,
) : SubletrViewModel<IndividualChatPageUiState>() {
	val navHostController get() = navigationService.navHostController

	private val basicInfoStream: BehaviorSubject<IndividualChatPageUiState.BasicInfo> =
		BehaviorSubject.createDefault(
			// TODO: Remove when adding API calls
			IndividualChatPageUiState.BasicInfo(
				contactName = "Abhed Shwarma",
				address = "123 University Ave.",
			)
		)
	private val chatItemsStream: BehaviorSubject<List<ChatItemModel>> = BehaviorSubject.createDefault(
		// TODO: Remove when adding API calls
		listOf(
			ChatItemModel.MyChatItem("my test message"),
			ChatItemModel.OtherChatItem("other test message")
		)
	)
	val messageStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")

	override val uiStateStream: Observable<IndividualChatPageUiState> = Observable.combineLatest(
		basicInfoStream,
		chatItemsStream,
		messageStream,
	) { basicInfo, chatItems, message ->
		IndividualChatPageUiState.Loaded(
			basicInfo = basicInfo,
			chatItems = chatItems,
			message = message,
		)
	}
}

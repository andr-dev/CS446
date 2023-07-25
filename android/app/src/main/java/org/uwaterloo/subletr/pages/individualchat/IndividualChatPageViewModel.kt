package org.uwaterloo.subletr.pages.individualchat

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.apis.UserApi
import org.uwaterloo.subletr.api.models.GetUserResponse
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.models.ChatItemModel
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE
import org.uwaterloo.subletr.utils.base64ToBitmap
import javax.inject.Inject

@HiltViewModel
class IndividualChatPageViewModel @Inject constructor(
	private val userApi: UserApi,
	private val listingsApi: ListingsApi,
	private val navigationService: INavigationService,
	savedStateHandle: SavedStateHandle,
) : SubletrViewModel<IndividualChatPageUiState>() {
	val navHostController get() = navigationService.navHostController
	private val userId: Int = checkNotNull(savedStateHandle["userId"])

	fun sendMessage(uiState: IndividualChatPageUiState.Loaded) {
		if (uiState.message.isNotBlank()) {
			sendMessageStream.onNext(
				MessageParams(
					newMessage = uiState.message,
					oldMessages = uiState.chatItems,
				)
			)
		}
	}

	private val userIdStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(userId)
	private val basicInfoStream =
		userIdStream.map {
			val responses = runBlocking {
				listOf(
					async {
						runCatching {
							userApi.userGet(userId = it)
						}
							.getOrDefault(
								GetUserResponse(
									firstName = "",
									lastName = "",
									email = "",
									gender = "",
									rating = 0.0f,
								)
							)
					},
					async {
						runCatching {
							userApi.userAvatarGet(userId = it)
						}
							.getOrNull()
					}
				).awaitAll()
			}

			val userResponse: GetUserResponse = responses.getOrElse(
				0
			) {
				GetUserResponse(
					firstName = "",
					lastName = "",
					email = "",
					gender = "",
					rating = 0.0f,
				)
			} as GetUserResponse

			val userAvatarResponse: String? = responses.getOrNull(1) as String?

			val getListingResponse = userResponse.listingId?.let { listingId ->
				runCatching {
					runBlocking {
						listingsApi.listingsDetails(
							listingId = listingId,
							longitude = UWATERLOO_LONGITUDE,
							latitude = UWATERLOO_LATITUDE,
						)
					}
				}
					.getOrNull()
			}

			BasicInfoPreprocessing(
				// TODO: Add localization
				contactName = "${userResponse.firstName} ${userResponse.lastName}",
				address = getListingResponse?.details?.address?.let {
					it.substring(
						startIndex = 0,
						endIndex = it.indexOf(",", it.indexOf(",") + 1),
					)
				},
				avatar = userAvatarResponse,
			)
		}
			.observeOn(Schedulers.computation())
			.map {
				IndividualChatPageUiState.BasicInfo(
					contactName = it.contactName,
					address = it.address,
					avatar = it.avatar?.base64ToBitmap(),
				)
			}
			.observeOn(Schedulers.io())
			.subscribeOn(Schedulers.io())
	private val chatItemsStream: BehaviorSubject<List<ChatItemModel>> = BehaviorSubject.createDefault(
		emptyList(),
	)
	val messageStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	private val sendMessageStream: PublishSubject<MessageParams> = PublishSubject.create()

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

	init {
		sendMessageStream.map {
			chatItemsStream.onNext(
				it.oldMessages + ChatItemModel.MyChatItem(it.newMessage)
			)
			messageStream.onNext("")
		}
			.subscribeOn(Schedulers.computation())
			.safeSubscribe()
	}

	data class BasicInfoPreprocessing(
		val contactName: String,
		val address: String?,
		val avatar: String?,
	)

	data class MessageParams(
		val newMessage: String,
		val oldMessages: List<ChatItemModel>,
	)
}

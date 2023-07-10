package org.uwaterloo.subletr.pages.account

import android.graphics.Bitmap
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.apis.UserApi
import org.uwaterloo.subletr.api.models.GetUserResponse
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class AccountPageViewModel @Inject constructor(
	private val userApi: UserApi,
	private val authenticationService: IAuthenticationService,
	val navigationService: INavigationService,
): SubletrViewModel<AccountPageUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController

	val firstNameStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val lastNameStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val genderStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val emailStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val settingsStream: BehaviorSubject<MutableList<AccountPageUiState.Setting>> = BehaviorSubject.createDefault(mutableListOf())

	val listingIdStream: BehaviorSubject<Int?> = BehaviorSubject?.createDefault(null)


	private val userDetailsStream: Observable<Result<GetUserResponse>> =
		runCatching {
			runBlocking {
				val userResponse = userApi.userGet()
				firstNameStream.onNext(userResponse.firstName)
				lastNameStream.onNext(userResponse.lastName)
				genderStream.onNext(userResponse.gender)
				emailStream.onNext(userResponse.email)
				listingIdStream.onNext(userResponse.listingId)

				userResponse
			}
		}.onFailure {
			navHostController.navigate(
				route = NavigationDestination.LOGIN.rootNavPath,
				navOptions = navOptions {
					popUpTo(navHostController.graph.id)
				},
			)
		}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	override val uiStateStream: BehaviorSubject<AccountPageUiState> = BehaviorSubject.createDefault(
		AccountPageUiState.Loaded(
			lastName = "",
			firstName = "",
			gender = Gender.OTHER,
			listingId = null,
			settings = listOf(
				AccountPageUiState.Setting(
					textStringId = R.string.setting_1,
					toggleState = false,
				),
				AccountPageUiState.Setting(
					textStringId = R.string.setting_1,
					toggleState = true,
				)
			),
		)
	)

	fun updateUiState(uiState: AccountPageUiState) {
		uiStateStream.onNext(uiState)
	}

	fun logout() {
		authenticationService.deleteAccessToken()
		navigationService
			navHostController
			.navigate(NavigationDestination.LOGIN.rootNavPath)
	}
}

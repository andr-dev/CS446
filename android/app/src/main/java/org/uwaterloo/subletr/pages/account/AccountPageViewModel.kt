package org.uwaterloo.subletr.pages.account

import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class AccountPageViewModel @Inject constructor(
	val authenticationService: IAuthenticationService,
	val navigationService: INavigationService,
): SubletrViewModel<AccountPageUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController

	override val uiStateStream: BehaviorSubject<AccountPageUiState> = BehaviorSubject.createDefault(
		AccountPageUiState.Loaded(
			lastName = "",
			firstName = "",
			gender = Gender.OTHER,
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

	fun checkAuth() {
		authenticationService.isAuthenticatedUser()
	}
}

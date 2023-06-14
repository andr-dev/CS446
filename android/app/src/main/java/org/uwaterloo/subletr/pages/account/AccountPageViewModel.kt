package org.uwaterloo.subletr.pages.account

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class AccountPageViewModel @Inject constructor(
	val authenticationService: IAuthenticationService,
	val navigationService: INavigationService,
): ViewModel() {
	val navHostController get() = navigationService.getNavHostController()

	val uiStateStream: BehaviorSubject<AccountPageUiState> = BehaviorSubject.createDefault(
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
			.getNavHostController()
			.navigate(NavigationDestination.LOGIN.rootNavPath)
	}
}

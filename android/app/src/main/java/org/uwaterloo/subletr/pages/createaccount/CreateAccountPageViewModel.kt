package org.uwaterloo.subletr.pages.createaccount

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject


@HiltViewModel
class CreateAccountPageViewModel @Inject constructor(
	navigationService: INavigationService,
) : ViewModel() {
	val navHostController = navigationService.getNavHostController()

	val uiStateStream: BehaviorSubject<CreateAccountPageUiState> = BehaviorSubject.createDefault(
		CreateAccountPageUiState.Loaded(
			firstName = "",
			lastName = "",
			email = "",
			password = "",
			confirmPassword = "",
			gender = Gender.OTHER,
		)
	)

	fun updateUiState(uiState: CreateAccountPageUiState) {
		uiStateStream.onNext(uiState)
	}
}

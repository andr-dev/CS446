package org.uwaterloo.subletr.pages.createaccount

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject


@HiltViewModel
class CreateAccountPageViewModel @Inject constructor() : ViewModel() {
	val uiStateStream: BehaviorSubject<CreateAccountPageUiState> = BehaviorSubject.createDefault(
		CreateAccountPageUiState.NewAccountInfo(
			firstName = "",
			lastName = "",
			email = "",
			password = "",
			confirmPassword = "",
			gender = CreateAccountPageUiState.Gender.OTHER,
		)
	)

	fun updateUiState(uiState: CreateAccountPageUiState) {
		uiStateStream.onNext(uiState)
	}
}

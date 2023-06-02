package org.uwaterloo.subletr.pages.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.R
import javax.inject.Inject

@HiltViewModel
class LoginPageViewModel @Inject constructor() : ViewModel() {
	val uiStateStream: BehaviorSubject<LoginPageUiState> = BehaviorSubject.createDefault(
		LoginPageUiState.Loaded(
			email = "",
			password = "",
		)
	)

	fun updateUiState(uiState: LoginPageUiState) {
		uiStateStream.onNext(uiState)
	}
}

package org.uwaterloo.subletr.pages.emailverification

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import okhttp3.internal.immutableListOf
import org.uwaterloo.subletr.R
import javax.inject.Inject

@HiltViewModel
class EmailVerificationPageViewModel @Inject constructor() : ViewModel() {
	val uiStateStream: BehaviorSubject<EmailVerificationPageUiState> = BehaviorSubject.createDefault(
		EmailVerificationPageUiState.Loaded(
			verificationCode = immutableListOf("","","","","")
		)
	)

	fun updateUiState(uiState: EmailVerificationPageUiState) {
		uiStateStream.onNext(uiState)
	}
}

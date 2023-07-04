package org.uwaterloo.subletr.pages.emailverification

import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import okhttp3.internal.immutableListOf
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class EmailVerificationPageViewModel @Inject constructor(
	private val navigationService: INavigationService
) : SubletrViewModel<EmailVerificationPageUiState>() {
	val navHostController get() = navigationService.getNavHostController()

	override val uiStateStream: BehaviorSubject<EmailVerificationPageUiState> =
		BehaviorSubject.createDefault(
			EmailVerificationPageUiState.Loaded(
				verificationCode = immutableListOf("", "", "", "", "")
			)
		)

	fun updateUiState(uiState: EmailVerificationPageUiState) {
		uiStateStream.onNext(uiState)
	}
}

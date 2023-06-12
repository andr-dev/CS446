package org.uwaterloo.subletr.pages.account

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.enums.Gender
import javax.inject.Inject

@HiltViewModel
class AccountPageViewModel @Inject constructor(): ViewModel() {
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
}

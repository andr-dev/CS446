package org.uwaterloo.subletr.components.homepage

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.R
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor() : ViewModel() {
	val uiStateStream: BehaviorSubject<HomePageUiState> = BehaviorSubject.createDefault(
		HomePageUiState.Loaded(
			titleBlackStringId = R.string.app_name_black_part,
			titlePinkStringId = R.string.app_name_pink_part,
			emailStringId = R.string.email,
			passwordStringId = R.string.password,
			loginStringId = R.string.log_in,
			orStringId = R.string.or_caps,
			createAccountStringId = R.string.create_account
		)
	)
}

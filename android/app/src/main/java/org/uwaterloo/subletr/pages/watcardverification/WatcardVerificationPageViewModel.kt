package org.uwaterloo.subletr.pages.watcardverification

import android.widget.ImageView
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.pages.home.list.HomeListChildViewModel
import org.uwaterloo.subletr.pages.home.map.HomeMapChildViewModel
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class WatcardVerificationPageViewModel @Inject constructor (
) : SubletrViewModel<WatcardVerificationUiState>() {
	override val uiStateStream: BehaviorSubject<WatcardVerificationUiState> = BehaviorSubject.createDefault(
		WatcardVerificationUiState.Loaded(
			watcard = null,
			verified = false
		)
	)
}


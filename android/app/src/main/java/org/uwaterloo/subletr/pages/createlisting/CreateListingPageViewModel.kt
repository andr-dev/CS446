package org.uwaterloo.subletr.pages.createlisting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.enums.HousingType
import javax.inject.Inject

@HiltViewModel
class CreateListingPageViewModel @Inject constructor() : ViewModel() {
	val uiStateStream: BehaviorSubject<CreateListingPageUiState> = BehaviorSubject.createDefault(
		CreateListingPageUiState.Loaded(
			address = "",
			description = "",
			price = "",
			numBedrooms = "",
			startDate = "",
			endDate = "",
			housingType = HousingType.OTHER,
		)
	)

	fun updateUiState(uiState: CreateListingPageUiState) {
		uiStateStream.onNext(uiState)
	}

}
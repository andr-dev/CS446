package org.uwaterloo.subletr.pages.createlisting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import org.uwaterloo.subletr.api.apis.DefaultApi
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class CreateListingPageViewModel @Inject constructor(
	private val api: DefaultApi,
	val navigationService: INavigationService,
) : ViewModel() {
	private val disposables: MutableList<Disposable> = mutableListOf()

	val navHostController get() = navigationService.getNavHostController()

	val addressStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val descriptionStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val priceStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val numBedroomsStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val startDateStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val endDateStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val housingTypeStream: BehaviorSubject<HousingType> = BehaviorSubject.createDefault(HousingType.OTHER)


	val uiStateStream: Observable<CreateListingPageUiState> = Observable.combineLatest(
		addressStream,
		descriptionStream,
		priceStream,
		numBedroomsStream,
		startDateStream,
		endDateStream,
		housingTypeStream,
	) {
		address, description, price, numBedrooms, startDate, endDate, housingType ->
		CreateListingPageUiState.Loaded(
			address = "",
			description = "",
			price = "",
			numBedrooms = "",
			startDate = "",
			endDate = "",
			housingType = HousingType.OTHER,
		)
	}

	val createListingStream: PublishSubject<CreateListingPageUiState.Loaded> = PublishSubject.create()

	init {


	}
	override fun onCleared() {
		super.onCleared()
		disposables.forEach {
			it.dispose()
		}
	}

//	fun updateUiState(uiState: CreateListingPageUiState) {
//		uiStateStream.onNext(uiState)
//	}

}
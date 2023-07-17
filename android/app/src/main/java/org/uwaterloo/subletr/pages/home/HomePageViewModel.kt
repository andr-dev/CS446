package org.uwaterloo.subletr.pages.home

import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.pages.home.list.HomeListChildViewModel
import org.uwaterloo.subletr.pages.home.list.HomeListUiState
import org.uwaterloo.subletr.pages.home.map.HomeMapChildViewModel
import org.uwaterloo.subletr.pages.home.map.HomeMapUiState
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
	private val navigationService: INavigationService,
	val homeListChildViewModel: HomeListChildViewModel,
	val homeMapChildViewModel: HomeMapChildViewModel,
) : SubletrViewModel<HomePageUiState>(
	homeListChildViewModel,
	homeMapChildViewModel,
) {
	val navHostController: NavHostController get() = navigationService.navHostController

	private val internalMergedUiStateStream = Observable.merge(
		homeListChildViewModel.uiStateStream,
		homeMapChildViewModel.uiStateStream
	)
		.map {
			internalPublishUiStateStream.onNext(it)
		}
		.subscribeOn(Schedulers.io())

	private val internalPublishUiStateStream: BehaviorSubject<HomePageUiState> =
		BehaviorSubject.createDefault(HomeListUiState.Loading)

	override val uiStateStream: Observable<HomePageUiState> = internalPublishUiStateStream

	fun switchToMapView(uiState: HomePageUiState) {
		if (uiState is HomeListUiState.Loading) {
			internalPublishUiStateStream.onNext(
				HomeMapUiState.Loading
			)
		}
		else if (uiState is HomeListUiState.Loaded) {
			internalPublishUiStateStream.onNext(
				HomeMapUiState.Loaded(
					addressSearch = "",
					timeToDestination = 0.0f,
					transportationMethod = HomeMapUiState.TransportationMethod.ALL,
					locationRange = uiState.locationRange,
					priceRange = uiState.priceRange,
					roomRange = uiState.roomRange,
					genderPreference = uiState.genderPreference,
					houseTypePreference = uiState.houseTypePreference,
					listingItems = uiState.listingItems,
					dateRange = uiState.dateRange,
				)
			)
		}
	}

	fun switchToListView(uiState: HomePageUiState) {
		if (uiState is HomeMapUiState.Loading) {
			internalPublishUiStateStream.onNext(
				HomeListUiState.Loading
			)
		}
		else if (uiState is HomeMapUiState.Loaded) {
			internalPublishUiStateStream.onNext(
				HomeListUiState.Loaded(
					locationRange = uiState.locationRange,
					priceRange = uiState.priceRange,
					roomRange = uiState.roomRange,
					genderPreference = uiState.genderPreference,
					houseTypePreference = uiState.houseTypePreference,
					infoTextStringId = null,
					listingItems = uiState.listingItems,
					filterFavourite = false,
					dateRange = uiState.dateRange,
				)
			)
		}
	}

	init {
		internalMergedUiStateStream.safeSubscribe()
	}
}

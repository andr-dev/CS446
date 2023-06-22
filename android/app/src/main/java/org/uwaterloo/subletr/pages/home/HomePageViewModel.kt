package org.uwaterloo.subletr.pages.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.DefaultApi
import org.uwaterloo.subletr.api.models.GetListingsResponse
import org.uwaterloo.subletr.enums.LocationRange
import org.uwaterloo.subletr.enums.PriceRange
import org.uwaterloo.subletr.enums.RoomRange
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.INavigationService
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class HomePageViewModel @Inject constructor(
	private val api: DefaultApi,
	private val navigationService: INavigationService,
) : ViewModel() {

	data class FilterVals(
		var locationRange: LocationRange,
		var priceRange: PriceRange,
		var roomRange: RoomRange,

		)

	private val disposables: MutableList<Disposable> = mutableListOf()

	val navHostController get() = navigationService.getNavHostController()

	val locationRangeFilterStream: BehaviorSubject<LocationRange> =
		BehaviorSubject.createDefault(LocationRange.NOFILTER)
	val priceRangeFilterStream: BehaviorSubject<PriceRange> = BehaviorSubject.createDefault(
		PriceRange.NOFILTER
	)
	val roomRangeFilterStream: BehaviorSubject<RoomRange> =
		BehaviorSubject.createDefault(RoomRange.NOFILTER)
	private val listingsStream: BehaviorSubject<GetListingsResponse> = BehaviorSubject.createDefault(
		GetListingsResponse(
			listOf(), setOf()
		)
	)
	private val infoTextStringIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())

	private val filterStream: Observable<FilterVals> = Observable.combineLatest(
		locationRangeFilterStream,
		priceRangeFilterStream,
		roomRangeFilterStream,
	) { locationRange, priceRange, roomRange ->
		FilterVals(
			locationRange = locationRange,
			priceRange = priceRange,
			roomRange = roomRange
		)
	}
	val uiStateStream: Observable<HomePageUiState> = Observable.combineLatest(
		locationRangeFilterStream,
		priceRangeFilterStream,
		roomRangeFilterStream,
		listingsStream,
		infoTextStringIdStream
	) { locationRange, priceRange, roomRange, listings, infoTextStringId ->
		HomePageUiState.Loaded(
			locationRange = locationRange,
			priceRange = priceRange,
			roomRange = roomRange,
			listings = listings,
			infoTextStringId = infoTextStringId.getOrNull()
		)
	}

	init {
		disposables.add(
			filterStream.map {
				runBlocking {
					api.listingsList(null, null, null, null)
				}
			}
				.map {
					listingsStream.onNext(it)
				}
				.doOnError {
					navHostController.navigate(NavigationDestination.LOGIN.rootNavPath)
				}
				.subscribeOn(Schedulers.io())
				.onErrorResumeWith(Observable.never())
				.subscribe()
		)
	}

	override fun onCleared() {
		super.onCleared()
		disposables.forEach {
			it.dispose()
		}
	}
}

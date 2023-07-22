package org.uwaterloo.subletr.pages.home.map

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.coroutineScope
import org.uwaterloo.subletr.infrastructure.SubletrChildViewModel
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.pages.home.HomePageViewModel
import org.uwaterloo.subletr.services.ILocationService
import org.uwaterloo.subletr.services.INavigationService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeMapChildViewModel @Inject constructor(
	private val locationService: ILocationService,
	private val navigationService: INavigationService,
): SubletrChildViewModel<HomeMapUiState>() {
	val navHostController get() = navigationService.navHostController

	suspend fun setLocationToCurrentLocation(uiState: HomeMapUiState.Loaded) {
		coroutineScope {
			val currentLocation: Location? = locationService.getLocation()
			if (currentLocation != null) {
				getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(
							timeToDestination = uiState.timeToDestination,
							addressSearch = CURRENT_LOCATION_STRING_VAL,
						),
						transportationMethod = uiState.transportationMethod,
						homePageView = HomePageUiState.HomePageViewType.MAP,
						computedLatLng = LatLng(
							currentLocation.latitude,
							currentLocation.longitude,
						),
					)
				)
			}
		}
	}

	val getListingParamsStream: PublishSubject<HomePageViewModel.GetListingParams> =
		PublishSubject.create()

	override val uiStateStream: PublishSubject<HomeMapUiState> = PublishSubject.create()

	init {
		uiStateStream
			.distinctUntilChanged { first, second ->
				if (first is HomeMapUiState.Loaded && second is HomeMapUiState.Loaded) {
					first.timeToDestination == second.timeToDestination &&
						first.addressSearch == second.addressSearch
				}
				else true
			}
			.skip(1)
			.debounce(1, TimeUnit.SECONDS)
			.map {
				if (it is HomeMapUiState.Loaded) {
					getListingParamsStream.onNext(
						HomePageViewModel.GetListingParams(
							filters = it.filters.copy(
								timeToDestination = it.timeToDestination,
								addressSearch = it.addressSearch.ifEmpty { null },
							),
							transportationMethod = it.transportationMethod,
							homePageView = HomePageUiState.HomePageViewType.MAP,
						)
					)
				}
			}
			.subscribeOn(Schedulers.computation())
			.safeSubscribe()
	}

	companion object {
		const val CURRENT_LOCATION_STRING_VAL = "CURRENT_LOCATION_STRING_CONSTANT"
	}
}

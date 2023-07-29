package org.uwaterloo.subletr.pages.home.map

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.coroutineScope
import org.uwaterloo.subletr.infrastructure.SubletrChildViewModel
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.pages.home.HomePageViewModel
import org.uwaterloo.subletr.pages.home.HomePageViewModel.Companion.CURRENT_LOCATION_STRING_VAL
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
				val latLng = LatLng(
					currentLocation.latitude,
					currentLocation.longitude,
				)
				getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(
							timeToDestination = uiState.timeToDestination,
							addressSearch = CURRENT_LOCATION_STRING_VAL,
							computedLatLng = latLng,
						),
						transportationMethod = uiState.transportationMethod,
						homePageView = HomePageUiState.HomePageViewType.MAP,
					)
				)
			}
		}
	}

	fun updateAllFilters(uiState: HomeMapUiState.Loaded, newFilters: HomePageUiState.FiltersModel) {
		getListingParamsStream.onNext(
			HomePageViewModel.GetListingParams(
				filters = newFilters,
				transportationMethod = uiState.transportationMethod,
				homePageView = HomePageUiState.HomePageViewType.MAP,
			)
		)
	}

	val navigateToChatStream: PublishSubject<Int> = PublishSubject.create()

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
			.map { homeMapUiState: HomeMapUiState ->
				if (homeMapUiState is HomeMapUiState.Loaded) {
					getListingParamsStream.onNext(
						HomePageViewModel.GetListingParams(
							filters = homeMapUiState.filters.copy(
								timeToDestination = homeMapUiState.timeToDestination,
								addressSearch = homeMapUiState.addressSearch.ifEmpty { null },
								locationRange = if (homeMapUiState.timeToDestination < 180) {
									HomePageUiState.LocationRange(
										lowerBound = null,
										upperBound = null,
									)
								} else homeMapUiState.filters.locationRange,
							),
							transportationMethod = homeMapUiState.transportationMethod,
							homePageView = HomePageUiState.HomePageViewType.MAP,
						)
					)
				}
			}
			.subscribeOn(Schedulers.computation())
			.safeSubscribe()
	}
}

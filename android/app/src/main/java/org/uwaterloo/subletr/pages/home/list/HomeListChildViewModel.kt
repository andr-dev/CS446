package org.uwaterloo.subletr.pages.home.list

import android.location.Location
import androidx.navigation.NavHostController
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

class HomeListChildViewModel @Inject constructor(
	private val locationService: ILocationService,
	private val navigationService: INavigationService,
) : SubletrChildViewModel<HomeListUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController

	suspend fun setLocationToCurrentLocation(uiState: HomeListUiState.Loaded) {
		coroutineScope {
			val currentLocation: Location? = locationService.getLocation()
			if (currentLocation != null) {
				getListingParamsStream.onNext(
					HomePageViewModel.GetListingParams(
						filters = uiState.filters.copy(
							addressSearch = HomePageViewModel.CURRENT_LOCATION_STRING_VAL,
							computedLatLng = LatLng(
								currentLocation.latitude,
								currentLocation.longitude,
							),
						),
						transportationMethod = HomePageUiState.TransportationMethod.WALK,
						homePageView = HomePageUiState.HomePageViewType.LIST,
					)
				)
			}
		}
	}

	val navigateToChatStream: PublishSubject<Int> = PublishSubject.create()

	val getListingParamsStream: PublishSubject<HomePageViewModel.GetListingParams> =
		PublishSubject.create()

	override val uiStateStream: PublishSubject<HomeListUiState> = PublishSubject.create()

	init {
		uiStateStream
			.distinctUntilChanged { first, second ->
				if (first is HomeListUiState.Loaded && second is HomeListUiState.Loaded) {
					first.addressSearch == second.addressSearch
				}
				else true
			}
			.skip(1)
			.debounce(1, TimeUnit.SECONDS)
			.map {
				if (it is HomeListUiState.Loaded) {
					getListingParamsStream.onNext(
						HomePageViewModel.GetListingParams(
							filters = it.filters.copy(
								addressSearch = it.addressSearch.ifEmpty { null },
							),
							transportationMethod = HomePageUiState.TransportationMethod.WALK,
							homePageView = HomePageUiState.HomePageViewType.LIST,
						)
					)
				}
			}
			.subscribeOn(Schedulers.computation())
			.safeSubscribe()
	}
}

package org.uwaterloo.subletr.pages.home.map

import android.location.Location
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.coroutineScope
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.infrastructure.SubletrChildViewModel
import org.uwaterloo.subletr.pages.home.HomePageUiState
import org.uwaterloo.subletr.pages.home.list.HomeListUiState
import org.uwaterloo.subletr.services.ILocationService
import java.time.OffsetDateTime
import javax.inject.Inject

class HomeMapChildViewModel @Inject constructor(
	private val locationService: ILocationService,
): SubletrChildViewModel<HomeMapUiState>() {
	suspend fun getLocation(): Location? {
		return coroutineScope {
			return@coroutineScope locationService.getLocation()
		}
	}

	val addressSearchStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val timeToDestinationStream: BehaviorSubject<Float> = BehaviorSubject.createDefault(0f)
	val transportationMethodStream: BehaviorSubject<HomeMapUiState.TransportationMethod> =
		BehaviorSubject.createDefault(HomeMapUiState.TransportationMethod.ALL)

	override val uiStateStream: Observable<HomeMapUiState> = Observable.combineLatest(
		addressSearchStream,
		timeToDestinationStream,
		transportationMethodStream,
	) { addressSearch, timeToDestination, transportationMethod ->
		HomeMapUiState.Loaded(
			addressSearch = addressSearch,
			timeToDestination = timeToDestination,
			transportationMethod = transportationMethod,
			locationRange = HomePageUiState.LocationRange(null, null),
			priceRange = HomePageUiState.PriceRange(null, null),
			roomRange = HomePageUiState.RoomRange(null, null, null, false),
			dateRange = HomeListUiState.DateRange(null, null),
			genderPreference = Gender.FEMALE,
			houseTypePreference = HousingType.HOUSE,
			listingItems = HomePageUiState.ListingItemsModel(
				listings = listOf(
					ListingSummary(
						listingId = 1,
						address = "123 University Ave.",
						distanceMeters = 25f,
						price = 100,
						roomsAvailable = 2,
						roomsTotal = 2,
						bathroomsAvailable = 2,
						bathroomsEnsuite = 0,
						bathroomsTotal = 2,
						leaseStart = OffsetDateTime.now(),
						leaseEnd = OffsetDateTime.now(),
						imgIds = emptyList(),
						residenceType = ResidenceType.apartment,
					)
				),
				likedListings = emptySet(),
				listingsImages = emptyList(),
			),
		)
	}
}

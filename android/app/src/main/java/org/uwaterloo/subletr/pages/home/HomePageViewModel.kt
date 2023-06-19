package org.uwaterloo.subletr.pages.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.apis.DefaultApi
import org.uwaterloo.subletr.api.models.GetListingsResponse
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.api.models.UserLoginRequest
import org.uwaterloo.subletr.enums.LocationRange
import org.uwaterloo.subletr.enums.PriceRange
import org.uwaterloo.subletr.enums.RoomRange
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.login.LoginPageUiState
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.INavigationService
import java.time.OffsetDateTime
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

val fakeSummary = ListingSummary(
	listingId = 1234234,
	address = "10 University Ave.",
	leaseEnd = OffsetDateTime.now(),
	leaseStart = OffsetDateTime.now(),
	price = 5000,
	rooms = 4,
	residenceType = ResidenceType.house
)

@HiltViewModel
class HomePageViewModel @Inject constructor(
	private val api: DefaultApi,
	private val navigationService: INavigationService,
	private val authenticationService: IAuthenticationService,
) : ViewModel() {
	private val disposables: MutableList<Disposable> = mutableListOf()

	val navHostController get() = navigationService.getNavHostController()

	val locationRangeFilterStream: BehaviorSubject<LocationRange> =
		BehaviorSubject.createDefault(LocationRange.NOFILTER)
	val priceRangeFilterStream: BehaviorSubject<PriceRange> = BehaviorSubject.createDefault(
		PriceRange.NOFILTER
	)
	val roomRangeFilterStream: BehaviorSubject<RoomRange> =
		BehaviorSubject.createDefault(RoomRange.NOFILTER)
	val listingsStream: BehaviorSubject<GetListingsResponse> = BehaviorSubject.createDefault(
		GetListingsResponse(
			listOf<ListingSummary>(
				fakeSummary,
				fakeSummary,
				fakeSummary,
				fakeSummary,

				), setOf()
		)
	)
	private val infoTextStringIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())

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

	val getListingStream: PublishSubject<LoginPageUiState.Loaded> = PublishSubject.create()

	init {
		disposables.add(
			getListingStream.map {
				runBlocking {
					api.listingsListWithHttpInfo(null, null, null, null)
				}
			}
				.map {
//					authenticationService.setAccessToken(it.token)
				}
				.doOnError {
					infoTextStringIdStream.onNext(
						Optional.of(R.string.invalid_login_credentials_try_again)
					)
					authenticationService.deleteAccessToken()
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

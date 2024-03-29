package org.uwaterloo.subletr.pages.home

import android.graphics.Bitmap
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.GeocodeApi
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.apis.UserApi
import org.uwaterloo.subletr.api.models.GetListingsResponse
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.enums.getGenderFilterString
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.pages.home.list.HomeListChildViewModel
import org.uwaterloo.subletr.pages.home.list.HomeListUiState
import org.uwaterloo.subletr.pages.home.map.HomeMapChildViewModel
import org.uwaterloo.subletr.pages.home.map.HomeMapUiState
import org.uwaterloo.subletr.pages.home.map.MAX_DISTANCE_IN_MINUTES
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE
import org.uwaterloo.subletr.utils.base64ToBitmap
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class HomePageViewModel @Inject constructor(
	private val navigationService: INavigationService,
	val homeListChildViewModel: HomeListChildViewModel,
	val homeMapChildViewModel: HomeMapChildViewModel,
	private val listingsApi: ListingsApi,
	private val geocodeApi: GeocodeApi,
	private val userApi: UserApi,
) : SubletrViewModel<HomePageUiState>(
	homeListChildViewModel,
	homeMapChildViewModel,
) {
	val navHostController: NavHostController get() = navigationService.navHostController

	val updateListingIdStream: BehaviorSubject<Unit> = BehaviorSubject.createDefault(Unit)

	val userListingIdStream: Observable<Optional<Int>> = updateListingIdStream.map {
		val userGetResponse = runCatching {
			runBlocking {
				userApi.userGet()
			}
		}
			.getOrNull()
		if (userGetResponse?.listingId != null) {
			Optional.of(userGetResponse.listingId)
		} else {
			Optional.empty()
		}
	}

	private val navigateToChatStream = Observable.merge(
		homeListChildViewModel.navigateToChatStream,
		homeMapChildViewModel.navigateToChatStream,
	)
		.map { listingId ->
			runCatching {
				runBlocking {
					listingsApi.listingsDetails(
						listingId = listingId,
						UWATERLOO_LONGITUDE,
						UWATERLOO_LATITUDE,
					)
				}
			}
				.onSuccess {
					navHostController.navigate(
						route = "${NavigationDestination.INDIVIDUAL_CHAT_PAGE.rootNavPath}/${it.details.ownerUserId}"
					)
				}
		}
		.subscribeOn(Schedulers.io())

	private val totalNumberOfPagesStream: BehaviorSubject<Int> =
		BehaviorSubject.createDefault(1)

	private val getListingParamsStream: Observable<GetListingParams> = BehaviorSubject.createDefault(
		GetListingParams(
			filters = HomePageUiState.FiltersModel(
				locationRange = HomePageUiState.LocationRange(),
				priceRange = HomePageUiState.PriceRange(),
				roomRange = HomePageUiState.RoomRange(),
				gender = Gender.OTHER,
				housingType = HousingType.OTHER,
				dateRange = HomePageUiState.DateRange(),
				favourite = false,
				timeToDestination = null,
				addressSearch = null,
				minRating = 0,
				showVerifiedOnly = false,
				computedLatLng = null,
			),
			transportationMethod = HomePageUiState.TransportationMethod.WALK,
			homePageView = HomePageUiState.HomePageViewType.LIST,
		)
	)
		.distinctUntilChanged()

	private val listingsStream: Observable<ListingParamsAndResponse> = Observable.merge(
		getListingParamsStream,
		homeListChildViewModel.getListingParamsStream,
		homeMapChildViewModel.getListingParamsStream,
	)
		.distinctUntilChanged { first, second ->
			first.listingPagingParams.pageNumber == second.listingPagingParams.pageNumber &&
				first.filters.deepEquals(second.filters) &&
				first.transportationMethod == second.transportationMethod
		}
		/*
		 * Enforce removing all previous listings when fetching first page
		 */
		.map {
			if (it.listingPagingParams.pageNumber == 0) {
				it.copy(
					listingPagingParams = it.listingPagingParams.copy(
						previousListingItemsModel = emptyList()
					)
				)
			}
			else {
				it
			}
		}
		/*
		 * Retrieve LatLng from geocodeApi if addressSearch is defined
		 */
		.map { getListingParams ->
			if (
				!getListingParams.filters.addressSearch.isNullOrEmpty()
				&& getListingParams.filters.addressSearch != CURRENT_LOCATION_STRING_VAL
			) {
				val splitAddress = getListingParams.filters.addressSearch.split(",").toTypedArray()
				runCatching {
					runBlocking {
						geocodeApi.forwardGeocode(
							addressLine = if (splitAddress.isNotEmpty()) splitAddress[0] else "",
							addressCity = if (splitAddress.size >= 2) splitAddress[1] else "",
							addressPostalcode = if (splitAddress.size >= 3) splitAddress[2] else "",
							addressCountry = if (splitAddress.size >= 4) splitAddress[3] else "Canada",
						)
					}
				}
					.getOrNull()
					?.let {
						return@map getListingParams.copy(
							filters = getListingParams.filters.copy(
								computedLatLng = LatLng(
									it.latitude.toDouble(),
									it.longitude.toDouble(),
								)
							)
						)
					}
				return@map getListingParams
			}
			else {
				return@map getListingParams
			}
		}
		/*
		 * Actually make Api call
		 */
		.map { getListingParams: GetListingParams ->
			ListingParamsAndResultResponse(
				listingParams = getListingParams,
				listingsResponse = runCatching {
					runBlocking {
						listingsApi.listingsList(
							longitude =
							if (getListingParams.filters.computedLatLng != null)
								getListingParams.filters.computedLatLng.longitude.toFloat()
							else UWATERLOO_LONGITUDE,
							latitude = if (getListingParams.filters.computedLatLng != null)
								getListingParams.filters.computedLatLng.latitude.toFloat()
							else UWATERLOO_LATITUDE,
							pageNumber = getListingParams.listingPagingParams.pageNumber,
							pageSize = LISTING_PAGE_SIZE,
							distanceMetersMin = getListingParams.filters.locationRange.lowerBound?.toFloat(),
							distanceMetersMax = getListingParams.filters.locationRange.upperBound?.toFloat(),
							priceMin = getListingParams.filters.priceRange.lowerBound,
							priceMax = getListingParams.filters.priceRange.upperBound,
							roomsAvailableMin = getListingParams.filters.roomRange.bedroomForSublet,
							roomsAvailableMax = setMaxRoom(getListingParams.filters.roomRange.bedroomForSublet),
							roomsTotalMin = getListingParams.filters.roomRange.bedroomInProperty,
							roomsTotalMax = setMaxRoom(getListingParams.filters.roomRange.bedroomInProperty),
							bathroomsAvailableMin = null,
							bathroomsAvailableMax = null,
							bathroomsTotalMin = getListingParams.filters.roomRange.bathroom,
							bathroomsTotalMax = setMaxRoom(getListingParams.filters.roomRange.bathroom),
							bathroomsEnsuiteMin = if (getListingParams.filters.roomRange.ensuiteBathroom) 1 else null,
							bathroomsEnsuiteMax = null,
							gender = getGenderFilterString(getListingParams.filters.gender),
							leaseStart = getListingParams.filters.dateRange.startingDate,
							leaseEnd =getListingParams.filters.dateRange.endingDate,
						)
					}
				}.onSuccess {
					totalNumberOfPagesStream.onNext(it.pages)
				}.onFailure {
					uiStateStream.onNext(HomeListUiState.Failed)
				}
			)
		}
		.filter {
			it.listingsResponse.isSuccess
		}
		.map {
			ListingParamsAndResponse(
				listingParams = it.listingParams,
				listingsResponse = it.listingsResponse
					.getOrDefault(
						GetListingsResponse(
							listings = emptyList(),
							pages = 0,
							liked = emptySet(),
						)
					),
			)
		}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	private val imagesStream: Observable<List<Pair<Int, Bitmap?>>> = listingsStream
		.map {
			runBlocking {
				it.listingsResponse.listings
					.map { l: ListingSummary ->
						async {
							Pair(
								l.listingId,
								runCatching {
									listingsApi.listingsImagesGet(l.imgIds.first())
								}.getOrNull()
							)
						}
					}.awaitAll()
			}
		}
		.subscribeOn(Schedulers.io())
		.observeOn(Schedulers.computation())
		.map { base64ImageList: List<Pair<Int, String?>> ->
			base64ImageList.map { (listingId: Int, base64Image: String?) ->
				Pair(
					listingId,
					base64Image?.base64ToBitmap()
				)
			}
		}
		.observeOn(Schedulers.io())
		.onErrorResumeWith(Observable.never())

	private val listingItemsStream: Observable<Unit> =
		Observable.combineLatest(
			listingsStream,
			imagesStream,
			userListingIdStream,
		) { listingParamsAndResponse, images, userListingId ->
			val getTimeToDestination: (ListingSummary) -> Float = {
				when (listingParamsAndResponse.listingParams.transportationMethod) {
					HomePageUiState.TransportationMethod.WALK -> {
						it.distanceMeters / WALKING_METRES_PER_MINUTE
					}
					HomePageUiState.TransportationMethod.BIKE -> {
						it.distanceMeters / CYCLING_METRES_PER_MINUTE
					}
					HomePageUiState.TransportationMethod.BUS -> {
						it.distanceMeters / BUS_METRES_PER_MINUTE
					}
					HomePageUiState.TransportationMethod.CAR -> {
						it.distanceMeters / DRIVING_METRES_PER_MINUTE
					}
				}
			}

			val newListingItems = (
				listingParamsAndResponse
					.listingParams
					.listingPagingParams
					.previousListingItemsModel +
					listingParamsAndResponse.listingsResponse.listings.map {
						HomePageUiState.ListingItem(
							summary = it,
							timeToDestination = getTimeToDestination(it),
						)
					}
				).map {
					images.forEach { (listingId: Int, imageBitmap: Bitmap?) ->
						if (it.summary.listingId == listingId) {
							return@map it.copy(
								image = imageBitmap,
							)
						}
					}
					it
				}
				.map {
					if (
						listingParamsAndResponse.listingsResponse.liked.contains(
							element = it.summary.listingId.toString(),
						)
					) {
						it.copy(liked = true)
					}
					else {
						it
					}
				}

			when (listingParamsAndResponse.listingParams.homePageView) {
				HomePageUiState.HomePageViewType.LIST -> {
					uiStateStream.onNext(
						HomeListUiState.Loaded(
							addressSearch = listingParamsAndResponse.listingParams.filters.addressSearch ?: "",
							filters = listingParamsAndResponse.listingParams.filters,
							listingItems = newListingItems,
							userListingId = userListingId.getOrNull(),
						)
					)
				}
				HomePageUiState.HomePageViewType.MAP -> {
					val filteredListingItems = newListingItems.filter {
						if ((listingParamsAndResponse
								.listingParams
								.filters
								.timeToDestination ?: 200.0f) < 180) {
							it.timeToDestination <
								(listingParamsAndResponse
									.listingParams
									.filters
									.timeToDestination ?: 210.0f)
						}
						else {
							true
						}
					}

					uiStateStream.onNext(
						HomeMapUiState.Loaded(
							filters = listingParamsAndResponse.listingParams.filters,
							listingItems = filteredListingItems,
							transportationMethod = listingParamsAndResponse.listingParams.transportationMethod,
							addressSearch = listingParamsAndResponse.listingParams.filters.addressSearch ?: "",
							timeToDestination = listingParamsAndResponse.listingParams.filters.timeToDestination
								?: MAX_DISTANCE_IN_MINUTES,
							userListingId = userListingId.getOrNull(),
						)
					)
				}
			}
		}
			.subscribeOn(Schedulers.computation())

	override val uiStateStream: BehaviorSubject<HomePageUiState> =
		BehaviorSubject.createDefault(HomeListUiState.Loading)

	fun switchToMapView(uiState: HomePageUiState) {
		if (uiState is HomeListUiState.Loading) {
			uiStateStream.onNext(
				HomeMapUiState.Loading
			)
		}
		else if (uiState is HomeListUiState.Loaded) {
			uiStateStream.onNext(
				HomeMapUiState.Loaded(
					addressSearch = uiState.addressSearch,
					transportationMethod = HomePageUiState.TransportationMethod.WALK,
					filters = uiState.filters,
					listingItems = uiState.listingItems,
					timeToDestination = uiState.filters.timeToDestination
						?: MAX_DISTANCE_IN_MINUTES,
					userListingId = uiState.userListingId
				)
			)
		}
	}

	fun switchToListView(uiState: HomePageUiState) {
		if (uiState is HomeMapUiState.Loading) {
			uiStateStream.onNext(
				HomeListUiState.Loading
			)
		}
		else if (uiState is HomeMapUiState.Loaded) {
			uiStateStream.onNext(
				HomeListUiState.Loaded(
					addressSearch = uiState.addressSearch,
					filters = uiState.filters,
					listingItems = uiState.listingItems,
					userListingId = uiState.userListingId,
				)
			)
		}
	}


	data class GetListingParams(
		val filters: HomePageUiState.FiltersModel,
		val homePageView: HomePageUiState.HomePageViewType,
		val transportationMethod: HomePageUiState.TransportationMethod,
		val listingPagingParams: ListingPagingParams = ListingPagingParams(
			previousListingItemsModel = emptyList(),
			pageNumber = 0,
		),
	)

	data class ListingPagingParams(
		val previousListingItemsModel: List<HomePageUiState.ListingItem>,
		val pageNumber: Int,
	)

	data class ListingParamsAndResultResponse(
		val listingParams: GetListingParams,
		val listingsResponse: Result<GetListingsResponse>,
	)

	data class ListingParamsAndResponse(
		val listingParams: GetListingParams,
		val listingsResponse: GetListingsResponse,
	)

	companion object {
		const val LISTING_PAGE_SIZE = 5
		const val WALKING_METRES_PER_MINUTE = 75
		const val CYCLING_METRES_PER_MINUTE = 250
		const val BUS_METRES_PER_MINUTE = 350
		const val DRIVING_METRES_PER_MINUTE = 600
		const val CURRENT_LOCATION_STRING_VAL = "CURRENT_LOCATION_STRING_VAL"
	}

	init {
		listingItemsStream.safeSubscribe()

		Observable.merge(
			homeListChildViewModel.uiStateStream,
			homeMapChildViewModel.uiStateStream,
		)
			.map {
				uiStateStream.onNext(it)
			}
			.subscribeOn(Schedulers.io())
			.safeSubscribe()

		navigateToChatStream.safeSubscribe()
	}

	private fun setMaxRoom(roomNum: Int?): Int? {
		return if (roomNum == 5) {
			null
		} else roomNum
	}
}

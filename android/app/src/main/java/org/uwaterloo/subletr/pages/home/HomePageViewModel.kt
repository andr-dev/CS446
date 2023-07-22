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
import org.uwaterloo.subletr.api.models.GetListingsResponse
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.pages.home.list.HomeListChildViewModel
import org.uwaterloo.subletr.pages.home.list.HomeListUiState
import org.uwaterloo.subletr.pages.home.map.HomeMapChildViewModel
import org.uwaterloo.subletr.pages.home.map.HomeMapChildViewModel.Companion.CURRENT_LOCATION_STRING_VAL
import org.uwaterloo.subletr.pages.home.map.HomeMapUiState
import org.uwaterloo.subletr.pages.home.map.MAX_DISTANCE_IN_MINUTES
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE
import org.uwaterloo.subletr.utils.base64ToBitmap
import java.lang.Integer.max
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
	private val navigationService: INavigationService,
	val homeListChildViewModel: HomeListChildViewModel,
	val homeMapChildViewModel: HomeMapChildViewModel,
	private val listingsApi: ListingsApi,
	private val geocodeApi: GeocodeApi,
) : SubletrViewModel<HomePageUiState>(
	homeListChildViewModel,
	homeMapChildViewModel,
) {
	val navHostController: NavHostController get() = navigationService.navHostController

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
						previousListingItemsModel = HomePageUiState.ListingItemsModel(
							listings = emptyList(),
							likedListings = emptySet(),
							listingsImages = emptyList(),
							selectedListings = emptyList(),
						)
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
							computedLatLng = LatLng(
								it.latitude.toDouble(),
								it.longitude.toDouble(),
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
							if (getListingParams.computedLatLng != null)
								getListingParams.computedLatLng.longitude.toFloat()
							else UWATERLOO_LONGITUDE,
							latitude = if (getListingParams.computedLatLng != null)
								getListingParams.computedLatLng.latitude.toFloat()
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
							gender = null,
							leaseStart = null,
							leaseEnd = null,
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

	private val imagesStream: Observable<List<Bitmap?>> = listingsStream
		.map {
			runBlocking {
				it.listingsResponse.listings
					.map { l: ListingSummary ->
						async {
							runCatching {
								listingsApi.listingsImagesGet(l.imgIds.first())
							}.getOrNull()
						}
					}.awaitAll()
			}
		}
		.subscribeOn(Schedulers.io())
		.observeOn(Schedulers.computation())
		.map { base64ImageList: List<String?> ->
			base64ImageList.map { it?.base64ToBitmap() }
		}
		.observeOn(Schedulers.io())
		.onErrorResumeWith(Observable.never())

	private val listingItemsStream: Observable<Unit> =
		Observable.combineLatest(
			listingsStream,
			imagesStream,
		) { listingParamsAndResponse, images ->
			when (listingParamsAndResponse.listingParams.homePageView) {
				HomePageUiState.HomePageViewType.LIST -> {
					uiStateStream.onNext(
						HomeListUiState.Loaded(
							filters = listingParamsAndResponse.listingParams.filters,
							listingItems = HomePageUiState.ListingItemsModel(
								listings = listingParamsAndResponse.listingParams.listingPagingParams.previousListingItemsModel.listings +
									listingParamsAndResponse.listingsResponse.listings,
								likedListings = listingParamsAndResponse
									.listingParams
									.listingPagingParams
									.previousListingItemsModel
									.likedListings +
									listingParamsAndResponse.listingsResponse.liked,
								listingsImages = listingParamsAndResponse
									.listingParams.listingPagingParams.previousListingItemsModel.listingsImages +
										images,
								selectedListings = emptyList(),
							),
						)
					)
				}
				HomePageUiState.HomePageViewType.MAP -> {
					val newListings = listingParamsAndResponse.listingParams.listingPagingParams.previousListingItemsModel.listings +
						listingParamsAndResponse.listingsResponse.listings
					val newLikedListings = listingParamsAndResponse
						.listingParams
						.listingPagingParams
						.previousListingItemsModel
						.likedListings +
						listingParamsAndResponse.listingsResponse.liked
					val newListingImages = listingParamsAndResponse
						.listingParams
						.listingPagingParams
						.previousListingItemsModel
						.listingsImages +
						images
					val newSelectedListings = listingParamsAndResponse
						.listingParams
						.listingPagingParams
						.previousListingItemsModel
						.selectedListings +
						List(
							size = max(
								newListings.size -
									listingParamsAndResponse
										.listingParams
										.listingPagingParams
										.previousListingItemsModel
										.selectedListings
										.size,
								0)
						) { false }

					uiStateStream.onNext(
						HomeMapUiState.Loaded(
							filters = listingParamsAndResponse.listingParams.filters,
							listingItems = HomePageUiState.ListingItemsModel(
								listings = newListings,
								likedListings = newLikedListings,
								listingsImages = newListingImages,
								selectedListings = newSelectedListings,
							),
							transportationMethod = listingParamsAndResponse.listingParams.transportationMethod,
							addressSearch = listingParamsAndResponse.listingParams.filters.addressSearch ?: "",
							timeToDestination = listingParamsAndResponse.listingParams.filters.timeToDestination
								?: MAX_DISTANCE_IN_MINUTES
						)
					)
				}
			}
		}
			.subscribeOn(Schedulers.io())

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
					addressSearch = "",
					transportationMethod = HomePageUiState.TransportationMethod.WALK,
					filters = uiState.filters,
					listingItems = uiState.listingItems,
					timeToDestination = uiState.filters.timeToDestination
						?: MAX_DISTANCE_IN_MINUTES
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
					filters = uiState.filters,
					listingItems = uiState.listingItems,
				)
			)
		}
	}


	data class GetListingParams(
		val filters: HomePageUiState.FiltersModel,
		val homePageView: HomePageUiState.HomePageViewType,
		val transportationMethod: HomePageUiState.TransportationMethod,
		val listingPagingParams: ListingPagingParams = ListingPagingParams(
			previousListingItemsModel = HomePageUiState.ListingItemsModel(
				listings = listOf(),
				likedListings = setOf(),
				listingsImages = listOf(),
				selectedListings = emptyList(),
			),
			pageNumber = 0,
		),
		val computedLatLng: LatLng? = null,
	)

	data class ListingPagingParams(
		val previousListingItemsModel: HomePageUiState.ListingItemsModel,
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
	}

	private fun setMaxRoom(roomNum: Int?): Int? {
		return if (roomNum == 5) {
			null
		} else roomNum
	}
}

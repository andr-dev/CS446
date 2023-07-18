package org.uwaterloo.subletr.pages.home.list

import android.graphics.Bitmap
import androidx.navigation.NavHostController
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.models.GetListingsResponse
import org.uwaterloo.subletr.api.models.ListingSummary
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.infrastructure.SubletrChildViewModel
import org.uwaterloo.subletr.services.ILocationService
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE
import org.uwaterloo.subletr.utils.base64ToBitmap
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

class HomeListChildViewModel @Inject constructor(
	private val listingsApi: ListingsApi,
	private val navigationService: INavigationService,
	private val locationService: ILocationService,
) : SubletrChildViewModel<HomeListUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController

	val locationRangeFilterStream: BehaviorSubject<HomeListUiState.LocationRange> =
		BehaviorSubject.createDefault(HomeListUiState.LocationRange())
	val priceRangeFilterStream: BehaviorSubject<HomeListUiState.PriceRange> =
		BehaviorSubject.createDefault(HomeListUiState.PriceRange())
	val roomRangeFilterStream: BehaviorSubject<HomeListUiState.RoomRange> =
		BehaviorSubject.createDefault(HomeListUiState.RoomRange())
	val genderFilterStream: BehaviorSubject<Gender> =
		BehaviorSubject.createDefault(Gender.OTHER)
	val houseTypeFilterStream: BehaviorSubject<HousingType> =
		BehaviorSubject.createDefault(HousingType.OTHER)
	val dateFilterStream: BehaviorSubject<HomeListUiState.DateRange> =
		BehaviorSubject.createDefault(HomeListUiState.DateRange())
	val favouriteFilterStream: BehaviorSubject<Boolean> =
		BehaviorSubject.createDefault(false)

	val listingPagingParamsStream: BehaviorSubject<ListingPagingParams> =
		BehaviorSubject.createDefault(
			ListingPagingParams(
				previousListingItemsModel = HomeListUiState.ListingItemsModel(
					listings = mutableListOf(),
					likedListings = mutableSetOf(),
					listingsImages = mutableListOf(),
				),
				pageNumber = 0,
			)
		)

	private val infoTextStringIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())

	private val totalNumberOfPagesStream: BehaviorSubject<Int> =
		BehaviorSubject.createDefault(1)

	private val listingsStream: Observable<ListingParamsAndResponse> = Observable.combineLatest(
		locationRangeFilterStream.distinctUntilChanged(),
		priceRangeFilterStream.distinctUntilChanged(),
		roomRangeFilterStream.distinctUntilChanged(),
		genderFilterStream.distinctUntilChanged(),
		houseTypeFilterStream.distinctUntilChanged(),
		dateFilterStream.distinctUntilChanged(),
		favouriteFilterStream.distinctUntilChanged(),
		listingPagingParamsStream
			.distinctUntilChanged { t1, t2 ->
				t1.pageNumber == t2.pageNumber
			}
			.withLatestFrom(totalNumberOfPagesStream, ::Pair)
			.filter {
				0 <= it.first.pageNumber && it.first.pageNumber < it.second
			}
			.map {
				it.first
			},
		::GetListingParams
	)
		.map { getListingParams: GetListingParams ->
			ListingParamsAndResultResponse(
				listingParams = getListingParams,
				listingsResponse = runCatching {

					runBlocking {
						// TODO: Change to use filter values + latitude + longitude
						listingsApi.listingsList(
							longitude = UWATERLOO_LONGITUDE,
							latitude = UWATERLOO_LATITUDE,
							pageNumber = getListingParams.listingPagingParams.pageNumber,
							pageSize = LISTING_PAGE_SIZE,
							distanceMetersMin = getListingParams.locationRange.lowerBound?.toFloat(),
							distanceMetersMax = getListingParams.locationRange.upperBound?.toFloat(),
							priceMin = getListingParams.priceRange.lowerBound,
							priceMax = getListingParams.priceRange.upperBound,
							roomsAvailableMin = getListingParams.roomRange.bedroomForSublet,
							roomsAvailableMax = setMaxRoom(getListingParams.roomRange.bedroomForSublet),
							roomsTotalMin = getListingParams.roomRange.bedroomInProperty,
							roomsTotalMax = setMaxRoom(getListingParams.roomRange.bedroomInProperty),
							bathroomsAvailableMin = null,
							bathroomsAvailableMax = null,
							bathroomsTotalMin = getListingParams.roomRange.bathroom,
							bathroomsTotalMax = setMaxRoom(getListingParams.roomRange.bathroom),
							bathroomsEnsuiteMin = if (getListingParams.roomRange.ensuiteBathroom) 1 else null,
							bathroomsEnsuiteMax = null,
							gender = "Female",
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

	private val listingItemsStream: Observable<HomeListUiState.ListingItemsModel> =
		Observable.combineLatest(
			listingsStream,
			imagesStream
		) { listingParamsAndResponse, images ->
			HomeListUiState.ListingItemsModel(
				listings = listingParamsAndResponse.listingParams.listingPagingParams.previousListingItemsModel.listings +
					listingParamsAndResponse.listingsResponse.listings,
				likedListings = listingParamsAndResponse.listingParams.listingPagingParams.previousListingItemsModel.likedListings +
					listingParamsAndResponse.listingsResponse.liked,
				listingsImages =
				listingParamsAndResponse.listingParams.listingPagingParams.previousListingItemsModel.listingsImages +
					images,
			)
		}

	// To ensure that view does not force an api call on every subscription
	private val internalUiStateStream: Observable<Unit> = Observable.combineLatest(
		locationRangeFilterStream,
		priceRangeFilterStream,
		roomRangeFilterStream,
		listingItemsStream,
		genderFilterStream,
		houseTypeFilterStream,
		dateFilterStream,
		favouriteFilterStream,
		infoTextStringIdStream,
		) {
			locationRange: HomeListUiState.LocationRange,
			priceRange: HomeListUiState.PriceRange,
			roomRange: HomeListUiState.RoomRange,
			listings: HomeListUiState.ListingItemsModel,
			genderPreference: Gender,
			houseTypePreference: HousingType,
			dateRange: HomeListUiState.DateRange,
			filterFavourite: Boolean,
			infoTextStringId: Optional<Int>,
		->
		uiStateStream.onNext(
			HomeListUiState.Loaded(
				locationRange = locationRange,
				priceRange = priceRange,
				roomRange = roomRange,
				listingItems = listings,
				genderPreference = genderPreference,
				houseTypePreference = houseTypePreference,
				dateRange = dateRange,
				filterFavourite = filterFavourite,
				infoTextStringId = infoTextStringId.getOrNull(),
			)
		)
	}

	override val uiStateStream: BehaviorSubject<HomeListUiState> =
		BehaviorSubject.createDefault(HomeListUiState.Loading)

	init {
		internalUiStateStream.safeSubscribe()
	}

	data class GetListingParams(
		val locationRange: HomeListUiState.LocationRange,
		val priceRange: HomeListUiState.PriceRange,
		val roomRange: HomeListUiState.RoomRange,
		val gender: Gender,
		val housingType: HousingType,
		val dateRange: HomeListUiState.DateRange,
		val filterFavourite: Boolean,
		val listingPagingParams: ListingPagingParams,
		)

	data class ListingPagingParams(
		val previousListingItemsModel: HomeListUiState.ListingItemsModel,
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

	private fun setMaxRoom(roomNum: Int?): Int? {
		return if (roomNum == 5) {
			null
		} else roomNum

	}
}

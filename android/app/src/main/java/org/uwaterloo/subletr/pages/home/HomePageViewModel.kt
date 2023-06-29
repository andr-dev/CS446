package org.uwaterloo.subletr.pages.home

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.navigation.navOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.models.GetListingsResponse
import org.uwaterloo.subletr.enums.LocationRange
import org.uwaterloo.subletr.enums.PriceRange
import org.uwaterloo.subletr.enums.RoomRange
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.base64ToBitmap
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class HomePageViewModel @Inject constructor(
	private val listingsApi: ListingsApi,
	private val navigationService: INavigationService,
) : ViewModel() {
	data class GetListingParams(
		val locationRange: LocationRange,
		val priceRange: PriceRange,
		val roomRange: RoomRange,
		val listingPagingParams: ListingPagingParams,
	)

	data class ListingPagingParams(
		val previousListingItemsModel: HomePageUiState.ListingItemsModel,
		val pageNumber: Int,
	)

	private val disposables: MutableList<Disposable> = mutableListOf()

	val navHostController get() = navigationService.getNavHostController()

	val locationRangeFilterStream: BehaviorSubject<LocationRange> =
		BehaviorSubject.createDefault(LocationRange.NOFILTER)
	val priceRangeFilterStream: BehaviorSubject<PriceRange> =
		BehaviorSubject.createDefault(PriceRange.NOFILTER)
	val roomRangeFilterStream: BehaviorSubject<RoomRange> =
		BehaviorSubject.createDefault(RoomRange.NOFILTER)

	val listingPagingParamsStream: BehaviorSubject<ListingPagingParams> =
		BehaviorSubject.createDefault(
			ListingPagingParams(
				previousListingItemsModel = HomePageUiState.ListingItemsModel(
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

	data class ListingParamsAndResponse(
		val listingParams: GetListingParams,
		val listingsResponse: GetListingsResponse,
	)

	private val listingsStream: Observable<ListingParamsAndResponse> = Observable.combineLatest(
		locationRangeFilterStream,
		priceRangeFilterStream,
		roomRangeFilterStream,
		listingPagingParamsStream
			.distinctUntilChanged { t1, t2 -> t1.pageNumber == t2.pageNumber }
			.withLatestFrom(totalNumberOfPagesStream, ::Pair)
			.filter {
				0 <= it.first.pageNumber && it.first.pageNumber < it.second
			}
			.map {
				it.first
			},
		::GetListingParams
	)
		.map { getListingParams ->
			ListingParamsAndResponse(
				listingParams = getListingParams,
				listingsResponse = runCatching {
					runBlocking {
						// TODO: Change to use filter values
						listingsApi.listingsList(
							priceMin = null,
							priceMax = null,
							roomsMin = null,
							roomsMax = null,
							pageNumber = getListingParams.listingPagingParams.pageNumber,
							pageSize = LISTING_PAGE_SIZE,
						)
					}
				}.onSuccess {
					totalNumberOfPagesStream.onNext(it.pages)
				}.onFailure {
					navHostController.navigate(
						route = NavigationDestination.LOGIN.rootNavPath,
						navOptions = navOptions {
							popUpTo(navHostController.graph.id)
						},
					)
				}
					.getOrDefault(
						GetListingsResponse(
							listings = emptyList(),
							liked = emptySet(),
							pages = 0,
						)
					)
		)
	}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	private val imagesStream: Observable<List<Bitmap?>> = listingsStream
		.map {
			runBlocking {
				it.listingsResponse.listings
					.map { l ->
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
		.map { base64ImageList ->
			base64ImageList.map { it?.base64ToBitmap() }
		}
		.observeOn(Schedulers.io())
		.onErrorResumeWith(Observable.never())

	private val listingItemsStream: Observable<HomePageUiState.ListingItemsModel> = Observable.combineLatest(
		listingsStream,
		imagesStream,
		::Pair
	).map {
		HomePageUiState.ListingItemsModel(
			listings = it.first.listingParams.listingPagingParams.previousListingItemsModel.listings +
				it.first.listingsResponse.listings,
			likedListings = it.first.listingParams.listingPagingParams.previousListingItemsModel.likedListings +
				it.first.listingsResponse.liked,
			listingsImages = it.first.listingParams.listingPagingParams.previousListingItemsModel.listingsImages +
				it.second,
		)
	}

	val uiStateStream: Observable<HomePageUiState> = Observable.combineLatest(
		locationRangeFilterStream,
		priceRangeFilterStream,
		roomRangeFilterStream,
		listingItemsStream,
		infoTextStringIdStream,
	) { locationRange, priceRange, roomRange, listings, infoTextStringId ->
		return@combineLatest HomePageUiState.Loaded(
			locationRange = locationRange,
			priceRange = priceRange,
			roomRange = roomRange,
			listingItems = listings,
			infoTextStringId = infoTextStringId.getOrNull()
		)
	}

	override fun onCleared() {
		super.onCleared()
		disposables.forEach {
			it.dispose()
		}
	}

	companion object {
		const val LISTING_PAGE_SIZE = 5
	}
}

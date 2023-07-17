package org.uwaterloo.subletr.pages.listingdetails

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.models.FavouriteListingRequest
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE
import org.uwaterloo.subletr.utils.base64ToBitmap
import javax.inject.Inject

@HiltViewModel
class ListingDetailsPageViewModel @Inject constructor(
	private val listingsApi: ListingsApi,
	savedStateHandle: SavedStateHandle,
	private val navigationService: INavigationService,
) : SubletrViewModel<ListingDetailsPageUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController
	private val listingId: Int = checkNotNull(savedStateHandle["listingId"])

	private val listingIdStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(listingId)
	private val imageIdsStream: BehaviorSubject<List<String>> =
		BehaviorSubject.createDefault(emptyList())
	private val favouritedStream: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
	private val toggleFavouriteStream: PublishSubject<Boolean> = PublishSubject.create()
	fun toggleFavourite(isFavourite: Boolean) {
		toggleFavouriteStream.onNext(isFavourite)
	}

	private val isFetchingImagesStream: BehaviorSubject<Boolean> =
		BehaviorSubject.createDefault(false)
	private val listingDetailsStream: Observable<Result<ListingDetails>> = listingIdStream.map {
		runCatching {
			runBlocking {
				val listing =
					listingsApi.listingsDetails(
						listingId = it,
						longitude = UWATERLOO_LONGITUDE,
						latitude = UWATERLOO_LATITUDE,
					)
				favouritedStream.onNext(listing.favourited)
				imageIdsStream.onNext(listing.details.imgIds)
				listing.details
			}
		}.onFailure {
			navHostController.navigate(
				route = NavigationDestination.LOGIN.rootNavPath,
				navOptions = navOptions {
					popUpTo(navHostController.graph.id)
				},
			)
		}
	}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	private val imagesStream: Observable<List<Bitmap>> = imageIdsStream.map {
		isFetchingImagesStream.onNext(true)
		runBlocking {
			it.map { id ->
				async {
					runCatching {
						listingsApi.listingsImagesGet(id)
					}.getOrNull()
				}
			}.awaitAll()
				.filterNotNull()
		}
	}
		.subscribeOn(Schedulers.io())
		.observeOn(Schedulers.computation())
		.map { base64ImageList ->
			base64ImageList.map { it.base64ToBitmap() }
		}
		.doOnNext {
			isFetchingImagesStream.onNext(false)
		}
		.observeOn(Schedulers.io())
		.onErrorResumeWith(Observable.never())

	override val uiStateStream: Observable<ListingDetailsPageUiState> = Observable.combineLatest(
		listingDetailsStream,
		favouritedStream,
		imagesStream,
		isFetchingImagesStream,
	) { listingDetails, favourited, images, isFetchingImages ->
		listingDetails.getOrNull()?.let {
			return@combineLatest ListingDetailsPageUiState.Loaded(
				it,
				favourited,
				images,
				isFetchingImages,
			)
		}
		return@combineLatest ListingDetailsPageUiState.Loading
	}

	init {
		toggleFavouriteStream.map {
			val favouriteListingRequest = FavouriteListingRequest(listingId)
			runCatching {
				runBlocking {
					if (it) {
						listingsApi.listingsFavourite(favouriteListingRequest)
					} else {
						listingsApi.listingsUnfavourite(favouriteListingRequest)
					}
					it
				}
			}.onSuccess {
				favouritedStream.onNext(it)
			}.onFailure {
				Log.d("API ERROR", "Favourite toggle failed")
			}
		}
			.subscribeOn(Schedulers.io())
			.onErrorResumeWith(Observable.never())
			.safeSubscribe()
	}
}

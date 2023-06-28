package org.uwaterloo.subletr.pages.listingdetails

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.base64ToBitmap
import javax.inject.Inject

@HiltViewModel
class ListingDetailsPageViewModel @Inject constructor(
	private val listingsApi: ListingsApi,
	savedStateHandle: SavedStateHandle,
	val navigationService: INavigationService
) : ViewModel() {

	private val listingIdStream: BehaviorSubject<Int> =
		BehaviorSubject.createDefault(checkNotNull(savedStateHandle["listingId"]))
	private val imageIdsStream: BehaviorSubject<List<String>> = BehaviorSubject.createDefault(emptyList())
	val favouritedStream: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
	private val listingDetailsStream: Observable<Result<ListingDetails>> = listingIdStream.map {
		runCatching {
			runBlocking {
				val listing = listingsApi.listingsDetails(it)
				favouritedStream.onNext(listing.favourited)
				imageIdsStream.onNext(listing.details.imgIds)
				listing.details
			}
		}
	}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	private val imagesStream: Observable<List<Bitmap>> = imageIdsStream.map {
		runCatching {
			runBlocking {
				it.map { id ->
					async {
						listingsApi.listingsImagesGet(id)
					}
				}.awaitAll()
			}
		}
	}
		.filter { it.isSuccess }
		.map {
			it.getOrDefault(emptyList())
		}
		.subscribeOn(Schedulers.io())
		.observeOn(Schedulers.computation())
		.map { base64ImageList ->
			base64ImageList.map { it.base64ToBitmap() }
		}
		.observeOn(Schedulers.io())
		.onErrorResumeWith(Observable.never())

	val uiStateStream: Observable<ListingDetailsPageUiState> = Observable.combineLatest(
		listingDetailsStream,
		favouritedStream,
		imagesStream,
	) {
		listingDetails, favourited, images ->
			listingDetails.getOrNull()?.let {
				return@combineLatest ListingDetailsPageUiState.Loaded(
					it,
					favourited,
					images,
				)
			}
		return@combineLatest ListingDetailsPageUiState.Loading
	}
}

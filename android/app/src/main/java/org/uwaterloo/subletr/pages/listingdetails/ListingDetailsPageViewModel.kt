package org.uwaterloo.subletr.pages.listingdetails

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.DefaultApi
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.base64ToBitmap
import javax.inject.Inject

@HiltViewModel
class ListingDetailsPageViewModel @Inject constructor(
	private val defaultApi: DefaultApi,
	savedStateHandle: SavedStateHandle,
	val navigationService: INavigationService
) : ViewModel() {

	private val listingIdStream: BehaviorSubject<Int> =
		BehaviorSubject.createDefault(checkNotNull(savedStateHandle["listingId"]))
	private val imageIdsStream: BehaviorSubject<List<String>> = BehaviorSubject.createDefault(emptyList())
	val favouritedStream: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
	private val listingDetailsStream: Observable<ListingDetails> = listingIdStream.map {
		runBlocking {
			val listing = defaultApi.listingsDetails(it)
			favouritedStream.onNext(listing.favourited)
			imageIdsStream.onNext(listing.details.imgIds)
			listing.details
		}
	}
		.subscribeOn(Schedulers.io())
	private val imagesStream: Observable<List<Bitmap>> = imageIdsStream.map {
		runBlocking {
			it.map { id ->
				val encodedImage = defaultApi.listingsImagesGet(id)
				encodedImage.base64ToBitmap()
			}
		}
	}
		.subscribeOn(Schedulers.computation())

	val uiStateStream: Observable<ListingDetailsPageUiState> = Observable.combineLatest(
		listingDetailsStream,
		favouritedStream,
		imagesStream,
	) {
		listingDetails, favourited, images ->
			ListingDetailsPageUiState.Loaded(
				listingDetails = listingDetails,
				favourited = favourited,
				images = images,
			)
	}
}

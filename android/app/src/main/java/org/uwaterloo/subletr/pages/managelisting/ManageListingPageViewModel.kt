package org.uwaterloo.subletr.pages.managelisting

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.material3.SnackbarHostState
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
import org.uwaterloo.subletr.api.apis.UserApi
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.api.models.UpdateListingRequest
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.services.ISnackbarService
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE
import org.uwaterloo.subletr.utils.base64ToBitmap
import java.time.format.DateTimeFormatter
import java.util.Optional
import javax.inject.Inject

@HiltViewModel
class ManageListingPageViewModel @Inject constructor(
	private val listingsApi: ListingsApi,
	val navigationService: INavigationService,
	savedStateHandle: SavedStateHandle,
	val snackbarService: ISnackbarService,
) : SubletrViewModel<ManageListingPageUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController
	val snackBarHostState: SnackbarHostState get() = snackbarService.snackbarHostState

	private val listingId: Int = checkNotNull(savedStateHandle["listingId"])
	private val listingIdStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(listingId)
	private val addressStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	private val imageIdsStream: BehaviorSubject<List<String>> =
		BehaviorSubject.createDefault(emptyList())
	val startDateDisplayTextStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val endDateDisplayTextStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")

	val editableFieldsStream: BehaviorSubject<UpdateListingRequest> = BehaviorSubject.createDefault(
		UpdateListingRequest(
			price = 0,
			roomsAvailable = 0,
			roomsTotal = 0,
			bathroomsAvailable = 0,
			bathroomsEnsuite = 0,
			bathroomsTotal = 0,
			leaseStart = "",
			leaseEnd = "",
			description = "",
			residenceType = ResidenceType.other,
			gender = "",
		)
	)

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
				addressStream.onNext(listing.details.address)
				imageIdsStream.onNext(listing.details.imgIds)
				startDateDisplayTextStream.onNext(
					DateTimeFormatter.ofPattern("MM/dd/yyyy").format(listing.details.leaseStart)
				)
				endDateDisplayTextStream.onNext(
					DateTimeFormatter.ofPattern("MM/dd/yyyy").format(listing.details.leaseEnd)
				)
				editableFieldsStream.onNext(
					UpdateListingRequest(
						price = listing.details.price,
						roomsAvailable = listing.details.roomsAvailable,
						roomsTotal = listing.details.roomsTotal,
						bathroomsAvailable = listing.details.bathroomsAvailable,
						bathroomsEnsuite = listing.details.bathroomsEnsuite,
						bathroomsTotal = listing.details.bathroomsTotal,
						leaseStart = listing.details.leaseStart.toString(),
						leaseEnd = listing.details.leaseEnd.toString(),
						description = listing.details.description,
						residenceType = listing.details.residenceType,
						gender = listing.details.gender,
					)
				)
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

	override val uiStateStream: Observable<ManageListingPageUiState> = Observable.combineLatest(
		addressStream,
		imagesStream,
		isFetchingImagesStream,
		editableFieldsStream,
		startDateDisplayTextStream,
		endDateDisplayTextStream,
	) {
			address, images, isFetchingImages, editableFields, startDateDisplay, endDateDisplay ->
		ManageListingPageUiState.Loaded(
			address = address,
			images = images,
			isFetchingImages = isFetchingImages,
			editableFields = editableFields,
			startDateDisplay = startDateDisplay,
			endDateDisplay = endDateDisplay,
		)
	}

	private val listingUpdateStream: PublishSubject<UpdateListingRequest> = PublishSubject.create()
	// TODO v
	private val listingDeleteStream: PublishSubject<UpdateListingRequest> = PublishSubject.create()

	init {
		listingUpdateStream.map {
			runCatching {
				runBlocking {
					listingsApi.listingsUpdate(it)
				}
			}
				.onFailure {
					// TODO ERROR CHECKING / FAILURE MESSAGE
					Log.d("API ERROR", "Listing update failed")
				}
		}
			.subscribeOn(Schedulers.io())
			.onErrorResumeWith(Observable.never())
			.safeSubscribe()

		listingDetailsStream.safeSubscribe()
	}

	fun updateListing(request: UpdateListingRequest) {
		listingUpdateStream.onNext(request)
	}

	fun deleteListing() {

	}
}
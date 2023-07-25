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
import org.uwaterloo.subletr.api.apis.UserApi
import org.uwaterloo.subletr.api.models.FavouriteListingRequest
import org.uwaterloo.subletr.api.models.GetUserResponse
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE
import org.uwaterloo.subletr.utils.base64ToBitmap
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class ListingDetailsPageViewModel @Inject constructor(
	private val listingsApi: ListingsApi,
	private val userApi: UserApi,
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

	val ownerIdStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
	private val ownerNameStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	private val ownerRatingStream: BehaviorSubject<Float> = BehaviorSubject.createDefault(0.0f)

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
				ownerIdStream.onNext(listing.details.ownerUserId)
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

	private val ownerDetailsStream: Observable<Result<GetUserResponse>> = ownerIdStream.map {
		runCatching {
			runBlocking {
				userApi.userGet(it)
			}
		}
			.onSuccess { userResponse ->
				ownerNameStream.onNext("${userResponse.firstName} ${userResponse.lastName}")
				ownerRatingStream.onNext(userResponse.rating)
			}
			.onFailure {
				Log.d("API ERROR", "Failed to get user details")
			}
	}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	private val ownerAvatarStream: Observable<Optional<String>> = ownerIdStream.map {
		var avatar = Optional.empty<String>()
		runCatching {
			runBlocking {
				userApi.userAvatarGet(it)
			}
		}
			.onSuccess { img ->
				avatar = Optional.of(img)
			}
			.onFailure {
				avatar = Optional.empty<String>()
			}
		avatar
	}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	private val ownerAvatarBitmapStream: Observable<Optional<Bitmap>> = ownerAvatarStream
		.observeOn(Schedulers.computation())
		.map {
			val nullableVersion = it.getOrNull()
			if (nullableVersion != null) {
				Optional.of(nullableVersion.base64ToBitmap())
			}
			else {
				Optional.empty<Bitmap>()
			}
		}
		.observeOn(Schedulers.io())

	private val ownerVerificationStream: Observable<Result<Boolean>> = ownerIdStream.map {
		runCatching {
			runBlocking {
				userApi.userIsVerified(it)
			}
		}
	}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	override val uiStateStream: Observable<ListingDetailsPageUiState> = Observable.combineLatest(
		listingDetailsStream,
		favouritedStream,
		imagesStream,
		isFetchingImagesStream,
		ownerNameStream,
		ownerRatingStream,
		ownerAvatarBitmapStream,
		ownerVerificationStream,
	) { listingDetails, favourited, images, isFetchingImages, ownerName, ownerRating, ownerAvatar, ownerVerification  ->
		listingDetails.getOrNull()?.let {
			return@combineLatest ListingDetailsPageUiState.Loaded(
				it,
				favourited,
				images,
				isFetchingImages,
				ownerDetails = ListingDetailsPageUiState.OwnerDetails(
					name = ownerName,
					rating = ownerRating,
					verified = ownerVerification.getOrDefault(false),
					avatar = ownerAvatar.getOrNull(),
				),
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

		ownerDetailsStream.safeSubscribe()
	}

	fun goToProfile() {
		ownerIdStream.value?.let {
			navHostController.navigate(
				route = "${NavigationDestination.PROFILE.rootNavPath}/${it}"
			)
		}
	}
}

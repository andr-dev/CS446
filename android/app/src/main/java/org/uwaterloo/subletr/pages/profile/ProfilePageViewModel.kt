package org.uwaterloo.subletr.pages.profile

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
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.apis.UserApi
import org.uwaterloo.subletr.api.models.GetUserResponse
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.api.models.RateUserRequest
import org.uwaterloo.subletr.api.models.UserAvatarUpdateRequest
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE
import org.uwaterloo.subletr.utils.base64ToBitmap
import org.uwaterloo.subletr.utils.toBase64String
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class ProfilePageViewModel @Inject constructor(
	private val userApi: UserApi,
	private val listingsApi: ListingsApi,
	savedStateHandle: SavedStateHandle,
	private val authenticationService: IAuthenticationService,
	val navigationService: INavigationService,
): SubletrViewModel<ProfilePageUiState>() {

	val navHostController: NavHostController get() = navigationService.navHostController

	private val userId: Int = checkNotNull(savedStateHandle["userId"])
	private val userIdStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(userId)
	private val nameStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val listingIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())
	private val listingDetailsStream: BehaviorSubject<Optional<ListingDetails>> =
		BehaviorSubject.createDefault(Optional.empty())
	private val listingImageStream: BehaviorSubject<Optional<String>> =
		BehaviorSubject.createDefault(Optional.empty())
	val ratingStream: BehaviorSubject<Float> = BehaviorSubject.createDefault(0.0f)
	val usersRatingStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)

	private val userDetailsStream: Observable<Result<GetUserResponse>> = userIdStream.map {
		runBlocking {
			runCatching {
				userApi.userGet(it)
			}.onSuccess { userResponse ->
				nameStream.onNext("${userResponse.firstName} ${userResponse.lastName}")
				ratingStream.onNext(userResponse.rating)
				if (userResponse.listingId != null) {
					listingIdStream.onNext(Optional.of(userResponse.listingId))
					runCatching {
						listingsApi.listingsDetails(
							listingId = userResponse.listingId,
							longitude = UWATERLOO_LONGITUDE,
							latitude = UWATERLOO_LATITUDE,
						)
					}
						.onSuccess { listing ->
							listingDetailsStream.onNext(Optional.of(listing.details))
							if (listing.details.imgIds.isNotEmpty()) {
								runCatching {
									listingsApi.listingsImagesGet(
										listing.details.imgIds.first(),
									)
								}
									.onSuccess { imageResponse ->
										listingImageStream.onNext(Optional.of(imageResponse))
									}
									.onFailure {
										Log.d("API ERROR", "Failed to get user's listing's image")
									}
							}
						}
						.onFailure {
							Log.d("API ERROR", "Failed to get user's listing details")
						}
				}
			}
		}
			.onFailure {
				Log.d("API ERROR", "Failed to get user details")
				navigationService.navHostController.popBackStack()
			}
	}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	private val listingImageBitmapStream: Observable<Optional<Bitmap>> = listingImageStream
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

	private val avatarStream: Observable<Optional<String>> = userIdStream.map {
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

	private val avatarBitmapStream: Observable<Optional<Bitmap>> = avatarStream
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

	private val verificationStream: Observable<Result<Boolean>> = userIdStream.map {
		runCatching {
			runBlocking {
				userApi.userIsVerified(it)
			}
		}
	}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	override val uiStateStream: Observable<ProfilePageUiState> = Observable.combineLatest(
		nameStream,
		listingIdStream,
		listingDetailsStream,
		listingImageBitmapStream,
		avatarBitmapStream,
		ratingStream,
		verificationStream,
		usersRatingStream,
	) {
			name, listingId, listingDetails, listingImage, avatar, rating, verification, usersRating ->
		ProfilePageUiState.Loaded(
			name = name,
			listingId = listingId.getOrNull(),
			listingDetails = listingDetails.getOrNull(),
			listingImage = listingImage.getOrNull(),
			avatar = avatar.getOrNull(),
			rating = rating,
			verification = verification.getOrDefault(false),
			usersRating = usersRating,
		)
	}

	val ratingUpdateStream: PublishSubject<Int> = PublishSubject.create()

	init {
		ratingUpdateStream.map {
			runBlocking {
				runCatching {
					userApi.userRate(
						RateUserRequest(
							userId = userId,
							rating = it,
						)
					)
				}
					.onSuccess {
						runCatching {
							userApi.userGetRating(userId)
						}
							.onSuccess { ratingResponse ->
								ratingStream.onNext(ratingResponse)
							}
							.onFailure {
								Log.d("API ERROR", "Failed to retrieve user rating")
							}
					}
					.onFailure {
						Log.d("API ERROR", "Failed to submit rating")
					}
			}
		}
		.subscribeOn(Schedulers.io())
		.onErrorResumeWith(Observable.never())
		.safeSubscribe()

		userDetailsStream.safeSubscribe()
	}

	fun allowUserToReview(): Boolean {
		val user = authenticationService.isAuthenticatedUser()
		if (user != null && user.userId == userId) {
			return false
		}
		return true
	}
}
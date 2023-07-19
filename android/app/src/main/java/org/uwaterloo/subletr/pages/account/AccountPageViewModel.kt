package org.uwaterloo.subletr.pages.account

import android.graphics.Bitmap
import android.util.Log
import androidx.navigation.NavHostController
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
import org.uwaterloo.subletr.api.models.UpdateUserRequest
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE
import org.uwaterloo.subletr.utils.base64ToBitmap
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class AccountPageViewModel @Inject constructor(
	private val userApi: UserApi,
	private val listingsApi: ListingsApi,
	private val authenticationService: IAuthenticationService,
	val navigationService: INavigationService,
): SubletrViewModel<AccountPageUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController

	val firstNameStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val lastNameStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val genderStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val settingsStream: BehaviorSubject<MutableList<AccountPageUiState.Setting>> =
		BehaviorSubject.createDefault(mutableListOf())
	val listingIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())
	private val listingDetailsStream: BehaviorSubject<Optional<ListingDetails>> =
		BehaviorSubject.createDefault(Optional.empty())
	private val imageStream: BehaviorSubject<Optional<String>> =
		BehaviorSubject.createDefault(Optional.empty())

	private val userDetailsStream: Observable<Result<GetUserResponse>> =
		BehaviorSubject.createDefault(Optional.empty<String>()).map {
			runBlocking {
				runCatching {
					userApi.userGet()
				}.onSuccess { userResponse ->
					firstNameStream.onNext(userResponse.firstName)
					lastNameStream.onNext(userResponse.lastName)
					genderStream.onNext(userResponse.gender)

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
									val imageResponse = listingsApi.listingsImagesGet(
										listing.details.imgIds.first(),
									)

									imageStream.onNext(Optional.of(imageResponse))
								}
							}
					}
				}
			}
				.onFailure {
					Log.d("API ERROR", "Failed to get user details")
//					navHostController.navigate(
//						route = NavigationDestination.LOGIN.rootNavPath,
//						navOptions = navOptions {
//							popUpTo(navHostController.graph.id)
//						},
//					)
				}
		}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	private val imageBitmapStream: Observable<Optional<Bitmap>> = imageStream.map {
		val nullableVersion = it.getOrNull()
		if (nullableVersion != null) {
			Optional.of(nullableVersion.base64ToBitmap())
		}
		else {
			Optional.empty<Bitmap>()
		}
	}
		.observeOn(Schedulers.computation())


	override val uiStateStream: Observable<AccountPageUiState> = Observable.combineLatest(
		lastNameStream,
		firstNameStream,
		genderStream,
		settingsStream,
		listingIdStream,
		listingDetailsStream,
		imageBitmapStream
	) {
			lastName, firstName, gender, settings, listingId, listingDetails, imageBitmap ->
		AccountPageUiState.Loaded(
			lastName = lastName,
			firstName = firstName,
			gender = gender,
			settings = settings,
			listingId = listingId.getOrNull(),
			listingDetails = listingDetails.getOrNull(),
			listingImage = imageBitmap.getOrNull(),
		)
	}

	val userUpdateStream: PublishSubject<AccountPageUiState.Loaded> = PublishSubject.create()

	init {
		userUpdateStream.map {
			Log.d("IN API CALL", "IN API CALL")
			runCatching {
				runBlocking {
					userApi.userUpdate(
						UpdateUserRequest(
							lastName = it.lastName,
							firstName = it.firstName,
							gender = it.gender,
						)
					)
				}
			}
				.onSuccess {
					navHostController.popBackStack()
				}
				.onFailure {
					Log.d("API ERROR", "User update failed")
				}
		}
			.subscribeOn(Schedulers.io())
			.onErrorResumeWith(Observable.never())
			.safeSubscribe()

		userDetailsStream.safeSubscribe()
	}

	fun logout() {
		authenticationService.deleteAccessToken()
		navigationService
			navHostController
			.navigate(NavigationDestination.LOGIN.rootNavPath)
	}
}

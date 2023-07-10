package org.uwaterloo.subletr.pages.account

import android.graphics.Bitmap
import android.util.Log
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
import kotlin.jvm.optionals.getOrDefault
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
	private val listingImageIdStream: BehaviorSubject<Optional<String>> =
		BehaviorSubject.createDefault(Optional.empty())

	private val userDetailsStream: Observable<Result<GetUserResponse>> =
		BehaviorSubject.createDefault(Optional.empty<String>()).map {
		runCatching {
			runBlocking {
				val userResponse = userApi.userGet()
				firstNameStream.onNext(userResponse.firstName)
				lastNameStream.onNext(userResponse.lastName)
				genderStream.onNext(userResponse.gender)
				if (userResponse.listingId != null) {
					listingIdStream.onNext(Optional.of(userResponse.listingId))

					val listing =
						listingsApi.listingsDetails(
							listingId = userResponse.listingId,
							longitude = UWATERLOO_LONGITUDE,
							latitude = UWATERLOO_LATITUDE,
						)
					listingDetailsStream.onNext(Optional.of(listing.details))
					if (listing.details.imgIds.isNotEmpty()) {
						listingImageIdStream.onNext(Optional.of(listing.details.imgIds.first()))
					}
				}

				userResponse
			}
		}.onFailure {
			Log.d("API ERROR", "Failed to get user details")
//			navHostController.navigate(
//				route = NavigationDestination.LOGIN.rootNavPath,
//				navOptions = navOptions {
//					popUpTo(navHostController.graph.id)
//				},
//			)
		}
	}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

//	private val avatarStream: Observable<Result<String>> =
//		BehaviorSubject.createDefault("").map {
//			runCatching {
//				runBlocking {
//					authenticationService.a
//					userApi.userAvatarGet()
//				}
//			}.onFailure {
//				Log.d("API ERROR", "Failed to get user avatar")
//			}
//		}
//			.onErrorResumeWith(Observable.never())
//			.subscribeOn(Schedulers.io())


	override val uiStateStream: Observable<AccountPageUiState> = Observable.combineLatest(
		userDetailsStream,
		lastNameStream,
		firstNameStream,
		genderStream,
		settingsStream,
		listingIdStream,
		listingDetailsStream,
	) {
			userDetails, lastName, firstName, gender, settings, listingId, listingDetails ->
		AccountPageUiState.Loaded(
			lastName = lastName,
			firstName = firstName,
			gender = gender,
			settings = settings,
			listingId = listingId.getOrNull(),
			listingDetails = listingDetails.getOrNull(),
			listingImage = null,
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
	}

	fun logout() {
		authenticationService.deleteAccessToken()
		navigationService
			navHostController
			.navigate(NavigationDestination.LOGIN.rootNavPath)
	}
}

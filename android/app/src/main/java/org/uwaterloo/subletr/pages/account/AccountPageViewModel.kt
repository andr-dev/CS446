package org.uwaterloo.subletr.pages.account

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.apis.UserApi
import org.uwaterloo.subletr.api.models.GetUserResponse
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.api.models.UpdateUserRequest
import org.uwaterloo.subletr.api.models.UserAvatarUpdateRequest
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.services.ISettingsService
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE
import org.uwaterloo.subletr.utils.base64ToBitmap
import org.uwaterloo.subletr.utils.toBase64String
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class AccountPageViewModel @Inject constructor(
	private val userApi: UserApi,
	private val listingsApi: ListingsApi,
	private val authenticationService: IAuthenticationService,
	val navigationService: INavigationService,
	private val settingsService: ISettingsService,
	): SubletrViewModel<AccountPageUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController

	val personalInformationStream: BehaviorSubject<AccountPageUiState.PersonalInformation> =
		BehaviorSubject.createDefault(AccountPageUiState.PersonalInformation(
			lastName = "",
			firstName = "",
			gender = "",
		))

	val settingsStream: BehaviorSubject<AccountPageUiState.Settings> =
		BehaviorSubject.createDefault(
			AccountPageUiState.Settings(
				useDeviceTheme = settingsService.getDefaultDisplayTheme(),
				useDarkMode = settingsService.getDisplayTheme(),
				allowChatNotifications = settingsService.getChatNotifications(),
			)
		)

	val listingIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())
	private val listingDetailsStream: BehaviorSubject<Optional<ListingDetails>> =
		BehaviorSubject.createDefault(Optional.empty())
	private val listingImageStream: BehaviorSubject<Optional<String>> =
		BehaviorSubject.createDefault(Optional.empty())
	val newAvatarBitmapStream: BehaviorSubject<Optional<Bitmap>> =
		BehaviorSubject.createDefault(Optional.empty())

	private val userDetailsStream: Observable<Result<GetUserResponse>> =
		BehaviorSubject.createDefault(Optional.empty<String>()).map {
			runBlocking {
				runCatching {
					userApi.userGet()
				}.onSuccess { userResponse ->
					personalInformationStream.onNext(
						AccountPageUiState.PersonalInformation(
							userResponse.lastName,
							userResponse.firstName,
							userResponse.gender,
						)
					)
//					firstNameStream.onNext(userResponse.firstName)
//					lastNameStream.onNext(userResponse.lastName)
//					genderStream.onNext(userResponse.gender)

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

	private val listingImageBitmapStream: Observable<Optional<Bitmap>> = listingImageStream.map {
		val nullableVersion = it.getOrNull()
		if (nullableVersion != null) {
			Optional.of(nullableVersion.base64ToBitmap())
		}
		else {
			Optional.empty<Bitmap>()
		}
	}
		.observeOn(Schedulers.computation())

	private val avatarStream: Observable<Optional<String>> =
		BehaviorSubject.createDefault(Optional.empty<String>()).map {
			val user = authenticationService.isAuthenticatedUser()
			if (user != null) {
				var avatar = Optional.empty<String>()
				runCatching {
					runBlocking {
						userApi.userAvatarGet(user.userId)
					}
				}.onSuccess { img ->
					avatar = Optional.of(img)
				}.onFailure {
					avatar = Optional.empty<String>()
				}
				avatar
			} else {
				Optional.empty<String>()
			}
		}
			.onErrorResumeWith(Observable.never())
			.subscribeOn(Schedulers.io())

	private val avatarBitmapStream: Observable<Optional<Bitmap>> = avatarStream.map {
		val nullableVersion = it.getOrNull()
		if (nullableVersion != null) {
			Optional.of(nullableVersion.base64ToBitmap())
		}
		else {
			Optional.empty<Bitmap>()
		}
	}
		.observeOn(Schedulers.computation())

	private val newAvatarStringStream: Observable<Optional<String>> = newAvatarBitmapStream
		.observeOn(Schedulers.computation())
		.map {
			val nullableVersion = it.getOrNull()
			if (nullableVersion != null) {
				Optional.of(nullableVersion.toBase64String())
			}
			else {
				Optional.empty<String>()
			}
		}
		.observeOn(Schedulers.io())
		.onErrorResumeWith(Observable.never())

	override val uiStateStream: Observable<AccountPageUiState> = Observable.combineLatest(
		personalInformationStream,
		settingsStream,
		listingIdStream,
		listingDetailsStream,
		listingImageBitmapStream,
		avatarBitmapStream,
		newAvatarBitmapStream,
		newAvatarStringStream,
	) {
			personalInformation, settings, listingId, listingDetails, listingImageBitmap, avatarBitmap, newAvatarBitmap, newAvatarString ->
		AccountPageUiState.Loaded(
			personalInformation = personalInformation,
			settings = settings,
			listingId = listingId.getOrNull(),
			listingDetails = listingDetails.getOrNull(),
			listingImage = listingImageBitmap.getOrNull(),
			avatarBitmap = avatarBitmap.getOrNull(),
			newAvatarBitmap = newAvatarBitmap.getOrNull(),
			newAvatarString = newAvatarString.getOrNull(),
		)
	}

	val userUpdateStream: PublishSubject<AccountPageUiState.Loaded> = PublishSubject.create()

	init {
		userUpdateStream.map {
			runCatching {
				runBlocking {
					if (it.newAvatarString != null) {
						userApi.userAvatarUpdate(UserAvatarUpdateRequest(it.newAvatarString))
					} else {
						userApi.userUpdate(
							UpdateUserRequest(
								lastName = it.personalInformation.lastName,
								firstName = it.personalInformation.firstName,
								gender = it.personalInformation.gender,
							)
						)
					}
				}
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

	fun updateAvatar(context: Context, imageUri: Uri) {
		if (Build.VERSION.SDK_INT < 28) {

			newAvatarBitmapStream.onNext(
				Optional.of(MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri))
			)
		} else {
			val source = ImageDecoder
				.createSource(context.contentResolver, imageUri)
			newAvatarBitmapStream.onNext(Optional.of(ImageDecoder.decodeBitmap(source)))
		}
	}

	fun setDefaultDisplayTheme(useDeviceTheme: Boolean) {
		settingsService.setDefaultDisplayTheme(useDeviceTheme)
	}

	fun setDisplayMode(useDarkMode: Boolean) {
		settingsService.setDisplayTheme(useDarkMode)
	}

	fun setChatNotifications(allowChatNotifications: Boolean) {
		settingsService.setChatNotifications(allowChatNotifications)
	}

	fun logout() {
		authenticationService.deleteAccessToken()
		navigationService
			navHostController
			.navigate(NavigationDestination.LOGIN.rootNavPath)
	}
}

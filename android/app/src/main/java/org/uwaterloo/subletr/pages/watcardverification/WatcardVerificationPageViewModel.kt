package org.uwaterloo.subletr.pages.watcardverification

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
import org.uwaterloo.subletr.api.apis.UserApi
import org.uwaterloo.subletr.api.models.UserVerifyUploadRequest
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.toBase64String
import java.util.Optional
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class WatcardVerificationPageViewModel @Inject constructor (
	private val userApi: UserApi,
	private val navigationService: INavigationService,
) : SubletrViewModel<WatcardVerificationUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController

	private val watcardBitmapStream: BehaviorSubject<Optional<Bitmap>> = BehaviorSubject.createDefault(Optional.empty())

	private val watcardUploadStream: PublishSubject<Bitmap> = PublishSubject.create()

	private val verifyStringStream = BehaviorSubject.createDefault(Optional.empty<String>())

	val submittedStream: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

	private val verifiedStream: Observable<Boolean> = Observable.interval(10, TimeUnit.SECONDS, Schedulers.io())
		.map {
			runCatching {
				runBlocking {
					userApi.userIsVerified()
				}
			}
				.getOrDefault(false)
		}


	override val uiStateStream: Observable<WatcardVerificationUiState> = Observable.combineLatest(
		verifiedStream,
		submittedStream,
		watcardBitmapStream,
	){ verifiedStream, submittedStream, watcardBitmapStream ->
			WatcardVerificationUiState.Loaded(
				watcard = watcardBitmapStream.getOrNull(),
				submitted = submittedStream,
				verified = verifiedStream,
			)
		}

	init {
		watcardUploadStream
			.observeOn(Schedulers.computation())
			.map {
				it.toBase64String()
			}
			.observeOn(Schedulers.io())
			.map {
				runCatching {
					runBlocking {
						userApi.userVerifyUpload(UserVerifyUploadRequest(it))
					}
				}
					.onFailure {
						Log.d("API ERROR", "Watcard upload failed")
					}
			}
			.subscribeOn(Schedulers.io())
			.onErrorResumeWith(Observable.never())
			.safeSubscribe()
	}
	fun updateWatcardBitmap(context: Context, imageUri: Uri) {
		if (Build.VERSION.SDK_INT < 28) {
			watcardBitmapStream.onNext(
				Optional.of(MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri))

			)
		} else {
			val source = ImageDecoder
				.createSource(context.contentResolver, imageUri)
			watcardBitmapStream.onNext(
				Optional.of(ImageDecoder.decodeBitmap(source))
			)
		}
	}

	fun uploadWatcard(bitmap: Bitmap) {
		watcardUploadStream.onNext(bitmap)
	}

}


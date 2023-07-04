package org.uwaterloo.subletr.pages.createlisting

import android.graphics.Bitmap
import android.util.Log
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.models.CreateListingRequest
import org.uwaterloo.subletr.api.models.ListingsImagesCreateRequest
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.toBase64String
import javax.inject.Inject

@HiltViewModel
class CreateListingPageViewModel @Inject constructor(
	private val listingsApi: ListingsApi,
	val navigationService: INavigationService,
) : SubletrViewModel<CreateListingPageUiState>() {
	val navHostController: NavHostController get() = navigationService.getNavHostController()

	val fullAddressStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")

	private val addressStream: Observable<CreateListingPageUiState.AddressModel> = fullAddressStream
		.observeOn(Schedulers.computation())
		.map {
			val splitAddress = it.split(",").toTypedArray()

			CreateListingPageUiState.AddressModel(
				fullAddress = it,
				addressLine = if (splitAddress.isNotEmpty()) splitAddress[0] else "",
				addressCity = if (splitAddress.size >= 2) splitAddress[1] else "",
				addressPostalCode = if (splitAddress.size >= 3) splitAddress[2] else "",
				addressCountry = if (splitAddress.size >= 4) splitAddress[3] else "Canada",
			)
		}
		.observeOn(Schedulers.io())
		.onErrorResumeWith(Observable.never())

	val descriptionStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val priceStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
	val numBedroomsStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
	val startDateStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val endDateStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")

	val imagesBitmapStream: BehaviorSubject<MutableList<Bitmap?>> = BehaviorSubject.createDefault(mutableListOf())

	private val imagesStream: Observable<List<String>> = imagesBitmapStream
		.observeOn(Schedulers.computation())
		.map {
			it.filterNotNull().map { btm ->
				btm.toBase64String()
			}
		}
		.observeOn(Schedulers.io())
		.onErrorResumeWith(Observable.never())


	override val uiStateStream: Observable<CreateListingPageUiState> = Observable.combineLatest(
		addressStream,
		descriptionStream,
		priceStream,
		numBedroomsStream,
		startDateStream,
		endDateStream,
		imagesBitmapStream,
		imagesStream,
	) {
			address, description, price, numBedrooms, startDate, endDate, imagesBitmap, images ->
		CreateListingPageUiState.Loaded(
			address = address,
			description = description,
			price = price,
			numBedrooms = numBedrooms,
			startDate = startDate,
			endDate = endDate,
			housingType = HousingType.OTHER,
			imagesBitmap = imagesBitmap,
			images = images,
		)
	}

	val createListingStream: PublishSubject<CreateListingPageUiState.Loaded> = PublishSubject.create()

	init {
		createListingStream.map {
			runCatching {
				runBlocking {
					val imgIds = it.images.map { img ->
						async {
							listingsApi.listingsImagesCreate(
								ListingsImagesCreateRequest(
									image = img,
								)
							).imageId
						}
					}.awaitAll()

					listingsApi.listingsCreate(
						CreateListingRequest(
							addressLine = it.address.addressLine,
							addressCity = it.address.addressCity,
							addressPostalcode = it.address.addressPostalCode,
							addressCountry = it.address.addressCountry,
							price = it.price,
							rooms = it.numBedrooms,
							leaseStart = it.startDate,
							leaseEnd = it.endDate,
							description = it.description,
							residenceType = ResidenceType.house,
							imgIds = imgIds,
						)
					)
				}
			}
				.onSuccess {
					navHostController.popBackStack()
				}
				.onFailure {
					Log.d("API ERROR", "Create listing failed")
				}
		}
			.subscribeOn(Schedulers.io())
			.onErrorResumeWith(Observable.never())
			.safeSubscribe()
	}
}

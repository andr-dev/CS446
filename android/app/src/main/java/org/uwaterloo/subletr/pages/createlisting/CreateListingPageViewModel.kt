package org.uwaterloo.subletr.pages.createlisting

import android.util.Log
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.ListingsApi
import org.uwaterloo.subletr.api.models.CreateListingRequest
import org.uwaterloo.subletr.api.models.ListingsImagesCreateRequest
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class CreateListingPageViewModel @Inject constructor(
	private val listingsApi: ListingsApi,
	val navigationService: INavigationService,
) : SubletrViewModel<CreateListingPageUiState>() {
	val navHostController: NavHostController get() = navigationService.getNavHostController()

	val addressLineStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val addressCityStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val addressPostalCodeStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")

	val descriptionStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val priceStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
	val numBedroomsStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
	val startDateStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val endDateStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")

	val imagesByteStream: BehaviorSubject<MutableList<String>> = BehaviorSubject.createDefault(ArrayList())

	override val uiStateStream: Observable<CreateListingPageUiState> = Observable.combineLatest(
		addressLineStream,
		addressCityStream,
		addressPostalCodeStream,
		descriptionStream,
		priceStream,
		numBedroomsStream,
		startDateStream,
		endDateStream,
		imagesByteStream
	) {
			addressLine, addressCity, addressPostalCode, description, price, numBedrooms, startDate, endDate, images ->
		CreateListingPageUiState.Loaded(
			addressLine = addressLine,
			addressCity = addressCity,
			addressPostalCode = addressPostalCode,
			addressCountry = "Canada",
			description = description,
			price = price,
			numBedrooms = numBedrooms,
			startDate = startDate,
			endDate = endDate,
			housingType = HousingType.OTHER,
			images = images,
		)
	}

	val createListingStream: PublishSubject<CreateListingPageUiState.Loaded> = PublishSubject.create()

	init {
		createListingStream.map {
			runCatching {
				runBlocking {
					val imgId = listingsApi.listingsImagesCreate(
						ListingsImagesCreateRequest(
							image = it.images[0]
						)
					).imageId
					listingsApi.listingsCreate(
						CreateListingRequest(
							addressLine = it.addressLine,
							addressCity = it.addressCity,
							addressPostalcode = it.addressPostalCode,
							addressCountry = it.addressCountry,
							price = it.price,
							rooms = it.numBedrooms,
							leaseStart = it.startDate,
							leaseEnd = it.endDate,
							description = it.description,
							residenceType = ResidenceType.house,
							imgIds = listOf(imgId)
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

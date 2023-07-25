package org.uwaterloo.subletr.pages.createlisting

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
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
import org.uwaterloo.subletr.enums.EnsuiteBathroomOption
import org.uwaterloo.subletr.enums.HousingType
import org.uwaterloo.subletr.enums.ListingForGenderOption
import org.uwaterloo.subletr.enums.getKey
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.CANADA
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE_DOUBLE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE_DOUBLE
import org.uwaterloo.subletr.utils.toBase64String
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CreateListingPageViewModel @Inject constructor(
	private val listingsApi: ListingsApi,
	val navigationService: INavigationService,
	@ApplicationContext applicationContext: Context,
) : SubletrViewModel<CreateListingPageUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController
	private val autocompleteSessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
	private val bounds: RectangularBounds = RectangularBounds.newInstance(
		LatLng(UWATERLOO_LATITUDE_DOUBLE, UWATERLOO_LONGITUDE_DOUBLE),
		LatLng(UWATERLOO_LATITUDE_DOUBLE, UWATERLOO_LONGITUDE_DOUBLE)
	)
	private val placesClient: PlacesClient = Places.createClient(applicationContext)

	val fullAddressStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	private val addressAutocompleteStream: Observable<ArrayList<AutocompletePrediction>> = fullAddressStream
		.debounce(1, TimeUnit.SECONDS, Schedulers.computation())
		.flatMap {
			Observable.create { emitter: ObservableEmitter<ArrayList<AutocompletePrediction>> ->
			val request = FindAutocompletePredictionsRequest.builder()
				.setLocationBias(bounds)
				.setOrigin(LatLng(UWATERLOO_LATITUDE_DOUBLE, UWATERLOO_LONGITUDE_DOUBLE))
				.setCountries(CANADA)
				.setTypesFilter(listOf(PlaceTypes.ADDRESS))
				.setSessionToken(autocompleteSessionToken)
				.setQuery(it)
				.build()
			placesClient.findAutocompletePredictions(request)
				.addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
					val predictions = ArrayList<AutocompletePrediction>()
					for (prediction in response.autocompletePredictions) {
						predictions.add(prediction)
					}
					emitter.onNext(predictions)
				}.addOnFailureListener { exception: Exception? ->
					emitter.onError(exception ?: RuntimeException())
				}
			}
		}.subscribeOn(Schedulers.io())

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
	val totalNumBedroomsStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
	val numBathroomsStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
	val bathroomsEnsuiteStream: BehaviorSubject<EnsuiteBathroomOption> =
		BehaviorSubject.createDefault(EnsuiteBathroomOption.NO)
	val totalNumBathroomsStream: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
	val genderStream: BehaviorSubject<ListingForGenderOption> = BehaviorSubject.createDefault(ListingForGenderOption.ANY)
	val housingTypeStream: BehaviorSubject<HousingType> = BehaviorSubject.createDefault(HousingType.OTHER)
	val startDateStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val startDateDisplayTextStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val endDateStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val endDateDisplayTextStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")

	val imagesBitmapStream: BehaviorSubject<MutableList<Bitmap?>> =
		BehaviorSubject.createDefault(mutableListOf())

	private val imagesStream: Observable<List<String>> = imagesBitmapStream
		.observeOn(Schedulers.computation())
		.map {
			it.filterNotNull().map { btm ->
				btm.toBase64String()
			}
		}
		.observeOn(Schedulers.io())
		.onErrorResumeWith(Observable.never())

	private val observables: List<Observable<*>> = listOf(
		addressStream,
		addressAutocompleteStream,
		descriptionStream,
		priceStream,
		numBedroomsStream,
		totalNumBedroomsStream,
		numBathroomsStream,
		bathroomsEnsuiteStream,
		totalNumBathroomsStream,
		genderStream,
		housingTypeStream,
		startDateStream,
		startDateDisplayTextStream,
		endDateStream,
		endDateDisplayTextStream,
		imagesBitmapStream,
		imagesStream,
	)

	override val uiStateStream: Observable<CreateListingPageUiState> = Observable.combineLatest(
		observables
	) {
		observing ->
		@Suppress("UNCHECKED_CAST")
		CreateListingPageUiState.Loaded(
			address = observing[0] as CreateListingPageUiState.AddressModel,
			addressAutocompleteOptions = observing[1] as ArrayList<AutocompletePrediction>,
			description = observing[2] as String,
			price = observing[3] as Int,
			numBedrooms = observing[4] as Int,
			totalNumBedrooms = observing[5] as Int,
			numBathrooms = observing[6] as Int,
			bathroomsEnsuite = observing[7] as EnsuiteBathroomOption,
			totalNumBathrooms = observing[8] as Int,
			gender = observing[9] as ListingForGenderOption,
			housingType = observing[10] as HousingType,
			startDate = observing[11] as String,
			startDateDisplayText = observing[12] as String,
			endDate = observing[13] as String,
			endDateDisplayText = observing[14] as String,
			imagesBitmap = observing[15] as MutableList<Bitmap?>,
			images = observing[16] as List<String>,
		)
	}

	val createListingStream: PublishSubject<CreateListingPageUiState.Loaded> =
		PublishSubject.create()

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
							leaseStart = it.startDate,
							leaseEnd = it.endDate,
							description = it.description,
							residenceType = when (it.housingType) {
								HousingType.HOUSE -> ResidenceType.house
								HousingType.APARTMENT -> ResidenceType.apartment
								else -> ResidenceType.other
							},
							imgIds = imgIds,
							bathroomsAvailable = it.numBathrooms,
							bathroomsEnsuite = if (it.bathroomsEnsuite == EnsuiteBathroomOption.YES) 1 else 0,
							bathroomsTotal = it.totalNumBathrooms,
							roomsAvailable = it.numBedrooms,
							roomsTotal = it.totalNumBedrooms,
							gender = it.gender.getKey(),
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

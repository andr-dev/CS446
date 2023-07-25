package org.uwaterloo.subletr.services

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.uwaterloo.subletr.utils.CANADA
import org.uwaterloo.subletr.utils.UWATERLOO_LATITUDE_DOUBLE
import org.uwaterloo.subletr.utils.UWATERLOO_LONGITUDE_DOUBLE
import java.util.concurrent.TimeUnit

class AddressAutocompleteService(
	private val placesClient: PlacesClient,
): IAddressAutocompleteService {
	override fun createAddressAutocompleteStream(
		fullAddressStream: BehaviorSubject<String>,
	): Observable<ArrayList<AutocompletePrediction>> {
		return fullAddressStream
			.debounce(1, TimeUnit.SECONDS, Schedulers.computation())
			.flatMap { address ->
				Observable.create<ArrayList<AutocompletePrediction>> { emitter ->
					val request = FindAutocompletePredictionsRequest.builder()
						.setLocationBias(bounds)
						.setOrigin(LatLng(UWATERLOO_LATITUDE_DOUBLE, UWATERLOO_LONGITUDE_DOUBLE))
						.setCountries(CANADA)
						.setTypesFilter(listOf(PlaceTypes.ADDRESS))
						.setSessionToken(autocompleteSessionToken)
						.setQuery(address)
						.build()
					placesClient.findAutocompletePredictions(request)
						.addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
							val predictions = ArrayList<AutocompletePrediction>()
							for (prediction in response.autocompletePredictions) {
								predictions.add(prediction)
							}
							emitter.onNext(predictions)
						}
						.addOnFailureListener { exception: Exception? ->
							emitter.onError(exception ?: RuntimeException())
						}
				}
			}
			.subscribeOn(Schedulers.io())
	}

	companion object {
		val autocompleteSessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
		val bounds: RectangularBounds = RectangularBounds.newInstance(
			LatLng(UWATERLOO_LATITUDE_DOUBLE, UWATERLOO_LONGITUDE_DOUBLE),
			LatLng(UWATERLOO_LATITUDE_DOUBLE, UWATERLOO_LONGITUDE_DOUBLE)
		)
	}
}

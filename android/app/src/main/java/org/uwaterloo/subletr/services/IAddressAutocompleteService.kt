package org.uwaterloo.subletr.services

import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.PlacesClient
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface IAddressAutocompleteService {
	fun createAddressAutocompleteStream(
		fullAddressStream: BehaviorSubject<String>,
	): Observable<ArrayList<AutocompletePrediction>>
}

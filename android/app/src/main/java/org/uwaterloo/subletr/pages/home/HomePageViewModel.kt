package org.uwaterloo.subletr.pages.home

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.api.apis.DefaultApi
import org.uwaterloo.subletr.api.models.GetListingsResponse
import org.uwaterloo.subletr.enums.LocationRange
import org.uwaterloo.subletr.enums.PriceRange
import org.uwaterloo.subletr.enums.RoomRange
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.utils.base64ToBitmap
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class HomePageViewModel @Inject constructor(
	private val api: DefaultApi,
	private val navigationService: INavigationService,
) : ViewModel() {

	data class FilterVals(
		var locationRange: LocationRange,
		var priceRange: PriceRange,
		var roomRange: RoomRange,
	)

	private val disposables: MutableList<Disposable> = mutableListOf()

	val navHostController get() = navigationService.getNavHostController()

	val locationRangeFilterStream: BehaviorSubject<LocationRange> =
		BehaviorSubject.createDefault(LocationRange.NOFILTER)
	val priceRangeFilterStream: BehaviorSubject<PriceRange> =
		BehaviorSubject.createDefault(PriceRange.NOFILTER)
	val roomRangeFilterStream: BehaviorSubject<RoomRange> =
		BehaviorSubject.createDefault(RoomRange.NOFILTER)

	private val infoTextStringIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())

	private val filterStream: Observable<FilterVals> = Observable.combineLatest(
		locationRangeFilterStream,
		priceRangeFilterStream,
		roomRangeFilterStream,
	) { locationRange, priceRange, roomRange ->
		FilterVals(
			locationRange = locationRange,
			priceRange = priceRange,
			roomRange = roomRange,
		)
	}

	private val listingsStream: Observable<Result<GetListingsResponse>> = filterStream.map {
		runCatching {
			runBlocking {
				// TODO: Change to use filter values
				api.listingsList(
					priceMin = null,
					priceMax = null,
					roomsMin = null,
					roomsMax = null,
				)
			}
		}
	}
		.doOnError {
			navHostController.navigate(NavigationDestination.LOGIN.rootNavPath)
		}
		.onErrorResumeWith(Observable.never())
		.subscribeOn(Schedulers.io())

	private val imagesStream: Observable<List<Bitmap>> = listingsStream
		.map {
			it.getOrDefault(
				GetListingsResponse(
					listings = emptyList(),
					liked = emptySet(),
				)
			)
		}
		.map {
			runCatching {
				runBlocking {
					it.listings
						.map { l ->
							async {
								api.listingsImagesGet(l.imgIds.first())
							}
						}.awaitAll()
				}
			}
		}
		.map {
			it.getOrDefault(emptyList())
		}
		.subscribeOn(Schedulers.io())
		.observeOn(Schedulers.computation())
		.map { base64ImageList ->
			base64ImageList.map { it.base64ToBitmap() }
		}
		.observeOn(Schedulers.io())
		.onErrorResumeWith(Observable.never())

	val uiStateStream: Observable<HomePageUiState> = Observable.combineLatest(
		locationRangeFilterStream,
		priceRangeFilterStream,
		roomRangeFilterStream,
		listingsStream,
		imagesStream,
		infoTextStringIdStream,
	) { locationRange, priceRange, roomRange, listings, listingsImages, infoTextStringId ->
		listings.onSuccess {
			return@combineLatest HomePageUiState.Loaded(
				locationRange = locationRange,
				priceRange = priceRange,
				roomRange = roomRange,
				listings = it,
				listingsImages = listingsImages,
				infoTextStringId = infoTextStringId.getOrNull()
			)
		}

		return@combineLatest HomePageUiState.Loading
	}

	override fun onCleared() {
		super.onCleared()
		disposables.forEach {
			it.dispose()
		}
	}
}

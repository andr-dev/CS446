package org.uwaterloo.subletr.pages.listingdetails

import android.graphics.Bitmap
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.api.models.ResidenceType
import org.uwaterloo.subletr.utils.localize

sealed interface ListingDetailsPageUiState {
	object Loading : ListingDetailsPageUiState

	data class Loaded(
		val listingDetails: ListingDetails,
		val favourited: Boolean,
		val images: List<Bitmap>,
		val isFetchingImages: Boolean,
	) : ListingDetailsPageUiState

	data class ListOfInfo(
		val price: Int,
		val roomsAvailable: Int,
		val roomsTotal: Int,
		val bathroomsAvailable: Int,
		val bathroomsEnsuite: Int,
		val bathroomsTotal: Int,
		val leaseStart: java.time.OffsetDateTime,
		val leaseEnd: java.time.OffsetDateTime,
		val residenceType: ResidenceType,
		val gender: String,
	)

	fun ListingDetails.toListOfInfo() = ListOfInfo(
		price = price,
		roomsAvailable = roomsAvailable,
		roomsTotal = roomsTotal,
		bathroomsAvailable = bathroomsAvailable,
		bathroomsEnsuite = bathroomsEnsuite,
		bathroomsTotal = bathroomsTotal,
		leaseStart = leaseStart,
		leaseEnd = leaseEnd,
		residenceType = residenceType,
		gender = gender,
	)

	fun ListOfInfo.getInfoDisplay(): List<Pair<Int, Any>> = listOf(
		Pair(R.string.gender, gender),
		Pair(R.string.price, price),
		Pair(R.string.start_date, leaseStart.localize()),
		Pair(R.string.end_date, leaseEnd.localize()),
		Pair(R.string.rooms, roomsAvailable),
		Pair(R.string.total_rooms_in_house, roomsTotal),
		Pair(R.string.bathrooms, bathroomsAvailable),
		Pair(R.string.ensuite_bathroom, if (bathroomsEnsuite > 0) R.string.yes else R.string.no),
		Pair(R.string.total_bathrooms_in_house, bathroomsTotal),
	)
}

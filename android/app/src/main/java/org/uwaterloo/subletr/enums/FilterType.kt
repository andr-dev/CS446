package org.uwaterloo.subletr.enums

import androidx.annotation.StringRes
import org.uwaterloo.subletr.R

enum class FilterType (@StringRes val stringId: Int) {
	LOCATION(R.string.location),
	PRICE(R.string.price),
	ROOMS(R.string.rooms),
	PROPERTY_TYPE(R.string.property_type),
	ROOMMATE(R.string.roommate),
	DATES(R.string.dates),
	FAVOURITE(R.string.favourite),
	RATING(R.string.rating),
	VERIFIEDPOST(R.string.verified_posts),
	ALL(R.string.all_filters),
}

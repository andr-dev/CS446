package org.uwaterloo.subletr.enums

import androidx.annotation.StringRes
import org.uwaterloo.subletr.R

enum class LocationRange(@StringRes val stringId: Int) {
	LESSTHAN1KM(R.string.less_than_1km),
	LESSTHAN3KM(R.string.less_than_3km),
	LESSTHAN5KM(R.string.less_than_5km),
	MORETHAN5KM(R.string.greater_than_5km),
	NOFILTER(R.string.clear);

	companion object {
		fun fromInt(value: Int) = LocationRange.values().first { it.stringId == value }
	}
}

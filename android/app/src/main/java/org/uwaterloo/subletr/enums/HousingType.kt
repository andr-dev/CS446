package org.uwaterloo.subletr.enums

import androidx.annotation.StringRes
import org.uwaterloo.subletr.R

enum class HousingType(@StringRes val stringId: Int) {
	SUITE(R.string.suite),
	HOUSE(R.string.house),
	STUDIO(R.string.studio),
	BASEMENT(R.string.basement),
	DORM(R.string.dorm),
	APARTMENT(R.string.apartment),
	OTHER(R.string.other)
}

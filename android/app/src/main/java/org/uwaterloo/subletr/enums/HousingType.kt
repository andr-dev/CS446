package org.uwaterloo.subletr.enums

import androidx.annotation.StringRes
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.models.ResidenceType

enum class HousingType(@StringRes val stringId: Int) {
	SUITE(R.string.suite),
	HOUSE(R.string.house),
	STUDIO(R.string.studio),
	BASEMENT(R.string.basement),
	DORM(R.string.dorm),
	APARTMENT(R.string.apartment),
	OTHER(R.string.other)
}

fun HousingType.toResidenceType(): ResidenceType {
	return when (this) {
		HousingType.HOUSE -> ResidenceType.house
		HousingType.APARTMENT -> ResidenceType.apartment
		else -> ResidenceType.other
	}
}

fun ResidenceType.toHousingType(): HousingType {
	return when (this) {
		ResidenceType.house -> HousingType.HOUSE
		ResidenceType.apartment -> HousingType.APARTMENT
		else -> HousingType.OTHER
	}
}
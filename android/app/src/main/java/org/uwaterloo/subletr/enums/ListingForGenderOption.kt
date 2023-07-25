package org.uwaterloo.subletr.enums

import androidx.annotation.StringRes
import org.uwaterloo.subletr.R

enum class ListingForGenderOption(@StringRes val stringId: Int) {
	MALE(R.string.male),
	FEMALE(R.string.female),
	ANY(R.string.any);
}

fun ListingForGenderOption.getKey(): String {
	return when (this) {
		ListingForGenderOption.MALE -> "Male"
		ListingForGenderOption.FEMALE -> "Female"
		ListingForGenderOption.ANY -> "Any"
	}
}

fun String.getGender(): ListingForGenderOption {
	return when (this) {
		"Male" -> ListingForGenderOption.MALE
		"Female" -> ListingForGenderOption.FEMALE
		else -> ListingForGenderOption.ANY
	}
}

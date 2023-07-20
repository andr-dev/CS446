package org.uwaterloo.subletr.enums

import androidx.annotation.StringRes
import org.uwaterloo.subletr.R

enum class ListingForGenderOption(@StringRes val stringId: Int) {
	MALE(R.string.male),
	FEMALE(R.string.female),
	ANY(R.string.any);
}

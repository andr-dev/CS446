package org.uwaterloo.subletr.enums

import androidx.annotation.StringRes
import org.uwaterloo.subletr.R

enum class Gender(@StringRes val stringId: Int) {
	MALE(R.string.male),
	FEMALE(R.string.female),
	UNKNOWN(R.string.prefer_not_to_say),
	OTHER(R.string.other);

}

fun Gender.getKey(): String {
	return when (this) {
		Gender.MALE -> "Male"
		Gender.FEMALE -> "Female"
		Gender.UNKNOWN -> "Prefer not to say"
		Gender.OTHER -> "Other"
	}
}

fun getGenderFilterString(value:Gender): String? {
	return when(value){
		Gender.MALE -> "Male"
		Gender.FEMALE -> "Female"
		Gender.UNKNOWN -> null
		Gender.OTHER -> null
	}
}

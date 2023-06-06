package org.uwaterloo.subletr.enums

import androidx.annotation.StringRes
import org.uwaterloo.subletr.R

enum class Gender {
	MALE,
	FEMALE,
	OTHER;

	@StringRes
	fun getStringRes(): Int {
		return when (this) {
			MALE -> R.string.male
			FEMALE -> R.string.female
			OTHER -> R.string.other
		}
	}
}

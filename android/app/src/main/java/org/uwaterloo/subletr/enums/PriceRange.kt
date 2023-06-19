package org.uwaterloo.subletr.enums

import androidx.annotation.StringRes
import org.uwaterloo.subletr.R

enum class PriceRange(@StringRes val stringId: Int) {
	LESSTHAN500(R.string.less_than_five_hundred),
	FIVEHUNDREDTOONEK(R.string.five_hundred_to_one_k),
	ONEKTOTWOK(R.string.one_k_to_two_k),
	GREATERTHAN2K(R.string.greater_than_two_k),
	NOFILTER(R.string.clear);

	companion object {
		fun fromInt(value: Int) = PriceRange.values().first { it.stringId == value }
	}
}

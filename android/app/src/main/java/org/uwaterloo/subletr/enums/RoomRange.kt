package org.uwaterloo.subletr.enums

import androidx.annotation.StringRes
import org.uwaterloo.subletr.R

enum class RoomRange(@StringRes val stringId: Int) {
	ONE(R.string.one),
	TWOTOTHREE(R.string.two_to_three),
	FOURTOFIVE(R.string.four_to_five),
	GREATERTHANFIVE(R.string.greater_than_five),
	NOFILTER(R.string.clear);

	companion object {
		fun fromInt(value: Int) = RoomRange.values().first { it.stringId == value }
	}

}
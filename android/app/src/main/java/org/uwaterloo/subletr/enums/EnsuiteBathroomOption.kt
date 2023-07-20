package org.uwaterloo.subletr.enums

import androidx.annotation.StringRes
import org.uwaterloo.subletr.R

enum class EnsuiteBathroomOption(@StringRes val stringId: Int) {
	YES(R.string.yes),
	NO(R.string.no),
}

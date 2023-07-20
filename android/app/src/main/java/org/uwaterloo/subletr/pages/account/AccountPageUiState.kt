package org.uwaterloo.subletr.pages.account

import android.graphics.Bitmap
import androidx.annotation.StringRes
import org.uwaterloo.subletr.api.models.ListingDetails
import org.uwaterloo.subletr.enums.Gender

sealed interface AccountPageUiState {
	object Loading : AccountPageUiState

	data class Loaded(
		val personalInformation: PersonalInformation,
		val settings: Settings,
		val listingId: Int?,
		val listingDetails: ListingDetails?,
		val listingImage: Bitmap?,
		val avatarBitmap: Bitmap?,
	) : AccountPageUiState

	data class Settings(
		val useDeviceTheme: Boolean,
		val useDarkMode: Boolean,
		val allowChatNotifications: Boolean,
	)

	data class PersonalInformation(
		val lastName: String,
		val firstName: String,
		val gender: String,
	)
}

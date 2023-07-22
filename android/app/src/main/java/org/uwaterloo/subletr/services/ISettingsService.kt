package org.uwaterloo.subletr.services

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope

interface ISettingsService {
	val useDeviceTheme: MutableState<Boolean>
	val useDarkTheme: MutableState<Boolean>
	val allowChatNotifications: MutableState<Boolean>
	fun subscribe(coroutineScope: CoroutineScope)
}

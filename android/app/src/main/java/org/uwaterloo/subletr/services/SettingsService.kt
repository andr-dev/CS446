package org.uwaterloo.subletr.services

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class SettingsService(
	private val ioService: IIoService,
	val context: Context,
): ISettingsService {
	override val useDeviceTheme: MutableState<Boolean> =
		if (settingExists(DEFAULT_DISPLAY_SETTINGS_PATH))
			mutableStateOf(
				value = ioService.readStringFromInternalFile(DEFAULT_DISPLAY_SETTINGS_PATH).toBoolean(),
			)
		else mutableStateOf(value = true)
	override val useDarkTheme: MutableState<Boolean> =
		if (settingExists(DISPLAY_THEME_SETTINGS_PATH))
			mutableStateOf(
				value = ioService.readStringFromInternalFile(DISPLAY_THEME_SETTINGS_PATH).toBoolean(),
			)
		else mutableStateOf(value = false)
	override val allowChatNotifications: MutableState<Boolean> =
		if (settingExists(CHAT_NOTIFICATIONS_SETTINGS_PATH))
			mutableStateOf(
				value = ioService.readStringFromInternalFile(CHAT_NOTIFICATIONS_SETTINGS_PATH).toBoolean()
			)
		else mutableStateOf(value = true)
	override fun subscribe(coroutineScope: CoroutineScope) {
		snapshotFlow { useDeviceTheme.value }
			.onEach {
				if (it) {
					useDarkTheme.value = (
						context.getResources().getConfiguration().uiMode and Configuration.UI_MODE_NIGHT_MASK
						) == Configuration.UI_MODE_NIGHT_YES
				}

				ioService.writeStringToInternalFile(
					fileName = DEFAULT_DISPLAY_SETTINGS_PATH,
					input = it.toString(),
				)
			}
			.launchIn(coroutineScope)

		snapshotFlow { useDarkTheme.value }
			.onEach {
				ioService.writeStringToInternalFile(
					fileName = DISPLAY_THEME_SETTINGS_PATH,
					input = it.toString(),
				)
			}
			.launchIn(coroutineScope)

		snapshotFlow { useDarkTheme.value }
			.onEach {
				ioService.writeStringToInternalFile(
					fileName = CHAT_NOTIFICATIONS_SETTINGS_PATH,
					input = it.toString(),
				)
			}
			.launchIn(coroutineScope)
	}

	private fun settingExists(filePath: String): Boolean {
		return ioService.internalFileExists(filePath)
	}

	companion object {
		private const val DEFAULT_DISPLAY_SETTINGS_PATH = "settings.defaultdisplaytheme.subletr"
		private const val DISPLAY_THEME_SETTINGS_PATH = "settings.displaytheme.subletr"
		private const val CHAT_NOTIFICATIONS_SETTINGS_PATH = "settings.chatnotifications.subletr"
	}
}

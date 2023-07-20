package org.uwaterloo.subletr.services


class SettingsService(
	private val ioService: IIoService,
): ISettingsService {

	override fun settingExists(filePath: String): Boolean {
		return ioService.internalFileExists(filePath)
	}

	override fun setDefaultDisplayTheme(useOSSetting: Boolean) {
		ioService.writeStringToInternalFile(
			fileName = DEFAULT_DISPLAY_SETTINGS_PATH,
			input = useOSSetting.toString(),
		)
	}

	override fun setDisplayTheme(useDarkMode: Boolean) {
		ioService.writeStringToInternalFile(
			fileName = DISPLAY_THEME_SETTINGS_PATH,
			input = useDarkMode.toString(),
		)
	}

	override fun setChatNotifications(allowNotifications: Boolean) {
		ioService.writeStringToInternalFile(
			fileName = CHAT_NOTIFICATIONS_SETTINGS_PATH,
			input = allowNotifications.toString(),
		)
	}

	override fun getDefaultDisplayTheme(): Boolean {
		return if (settingExists(DEFAULT_DISPLAY_SETTINGS_PATH)) {
			ioService.readStringFromInternalFile(DEFAULT_DISPLAY_SETTINGS_PATH).toBoolean()
		} else {
			true
		}
	}

	override fun getDisplayTheme(): Boolean {
		return if (settingExists(DISPLAY_THEME_SETTINGS_PATH)) {
			ioService.readStringFromInternalFile(DISPLAY_THEME_SETTINGS_PATH).toBoolean()
		} else {
			false
		}
	}

	override fun getChatNotifications(): Boolean {
		return if (settingExists(CHAT_NOTIFICATIONS_SETTINGS_PATH)) {
			ioService.readStringFromInternalFile(CHAT_NOTIFICATIONS_SETTINGS_PATH).toBoolean()
		} else {
			true
		}
	}

	companion object {
		private const val DEFAULT_DISPLAY_SETTINGS_PATH = "settings.defaultdisplaytheme.subletr"
		private const val DISPLAY_THEME_SETTINGS_PATH = "settings.displaytheme.subletr"
		private const val CHAT_NOTIFICATIONS_SETTINGS_PATH = "settings.chatnotifications.subletr"
	}
}
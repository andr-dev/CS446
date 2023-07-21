package org.uwaterloo.subletr.services


class SettingsService(
	private val ioService: IIoService,
): ISettingsService {
	private var useDeviceTheme: Boolean =
		if (settingExists(DEFAULT_DISPLAY_SETTINGS_PATH))
			ioService.readStringFromInternalFile(DEFAULT_DISPLAY_SETTINGS_PATH).toBoolean()
		else true
	private var useDarkTheme: Boolean =
		if (settingExists(DISPLAY_THEME_SETTINGS_PATH))
			ioService.readStringFromInternalFile(DISPLAY_THEME_SETTINGS_PATH).toBoolean()
		else false
	private var allowChatNotifications: Boolean =
		if (settingExists(CHAT_NOTIFICATIONS_SETTINGS_PATH))
			ioService.readStringFromInternalFile(CHAT_NOTIFICATIONS_SETTINGS_PATH).toBoolean()
		else true

	private fun settingExists(filePath: String): Boolean {
		return ioService.internalFileExists(filePath)
	}

	override fun setDefaultDisplayTheme(useOSSetting: Boolean) {
		ioService.writeStringToInternalFile(
			fileName = DEFAULT_DISPLAY_SETTINGS_PATH,
			input = useOSSetting.toString(),
		)
		useDeviceTheme = useOSSetting
	}

	override fun setDisplayTheme(useDarkMode: Boolean) {
		ioService.writeStringToInternalFile(
			fileName = DISPLAY_THEME_SETTINGS_PATH,
			input = useDarkMode.toString(),
		)
		useDarkTheme = useDarkMode
	}

	override fun setChatNotifications(allowNotifications: Boolean) {
		ioService.writeStringToInternalFile(
			fileName = CHAT_NOTIFICATIONS_SETTINGS_PATH,
			input = allowNotifications.toString(),
		)
		allowChatNotifications = allowNotifications
	}

	override fun getDefaultDisplayTheme(): Boolean {
		return useDeviceTheme
	}

	override fun getDisplayTheme(): Boolean {
		return useDarkTheme
	}

	override fun getChatNotifications(): Boolean {
		return allowChatNotifications
	}

	companion object {
		private const val DEFAULT_DISPLAY_SETTINGS_PATH = "settings.defaultdisplaytheme.subletr"
		private const val DISPLAY_THEME_SETTINGS_PATH = "settings.displaytheme.subletr"
		private const val CHAT_NOTIFICATIONS_SETTINGS_PATH = "settings.chatnotifications.subletr"
	}
}

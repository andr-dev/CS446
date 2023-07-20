package org.uwaterloo.subletr.services

interface ISettingsService {
	fun settingExists(filePath: String): Boolean
	fun setDefaultDisplayTheme(useOSSetting: Boolean)
	fun setDisplayTheme(useDarkMode: Boolean)
	fun setChatNotifications(allowNotifications: Boolean)
	fun getDefaultDisplayTheme(): Boolean
	fun getDisplayTheme(): Boolean
	fun getChatNotifications(): Boolean
}
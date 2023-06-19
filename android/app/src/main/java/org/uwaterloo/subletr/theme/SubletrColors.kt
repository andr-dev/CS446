@file:Suppress("MagicNumber")

package org.uwaterloo.subletr.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val subletrPink
	@Composable
	get() = if (isSystemInDarkTheme()) {
		subletrPinkDark
	} else {
		subletrPinkLight
	}

private val subletrPinkLight = Color(0xFFF4717F)
private val subletrPinkDark = Color(0xFFF4717F)

val textOnSubletrPink
	@Composable
	get() = if (isSystemInDarkTheme()) {
		textOnSubletrPinkDark
	} else {
		textOnSubletrPinkLight
	}
private val textOnSubletrPinkLight = Color((0xFFFFFFFF))
private val textOnSubletrPinkDark = Color((0xFFFFFFFF))

val unselectedGray
	@Composable
	get() = if (isSystemInDarkTheme()) {
		unselectedGrayDark
	} else {
		unselectedGrayLight
	}
private val unselectedGrayLight = Color(0xFF808080)
private val unselectedGrayDark = Color(0xFF808080)

val primaryTextColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		primaryTextColorDark
	} else {
		primaryTextColorLight
	}
private val primaryTextColorLight = Color((0xFF000000))
private val primaryTextColorDark = Color((0xFFFFFFFF))

val secondaryTextColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		secondaryTextColorDark
	} else {
		secondaryTextColorLight
	}
private val secondaryTextColorLight = Color((0xFF808080))
private val secondaryTextColorDark = Color((0xFF808080))
val darkerGrayButtonColor = Color((0xFFE5E5E5))
val secondaryButtonBackgroundColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		secondaryButtonBackgroundColorDark
	} else {
		secondaryButtonBackgroundColorLight
	}
private val secondaryButtonBackgroundColorLight = Color((0xFFF0F0F0))
private val secondaryButtonBackgroundColorDark = Color((0xFFF0F0F0))

val textFieldBackgroundColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		textFieldBackgroundColorDark
	} else {
		textFieldBackgroundColorLight
	}
private val textFieldBackgroundColorLight = Color((0xFFD9D9D9))
private val textFieldBackgroundColorDark = Color((0xFFD9D9D9))


val Purple900 = Color(0xFF4A148C)
val Purple500 = Color(0xFF9C27B0)

internal val SubletrDarkColorScheme = darkColorScheme(
	primary = subletrPinkDark,
	tertiary = Purple900,
	background = Color(0xFFFFFBFE),
	surface = Color(0xFFFFFBFE),
	onPrimary = Color.White,
	onSecondary = Color.White,
	onTertiary = Color.White,
	onBackground = Color(0xFF1C1B1F),
	onSurface = Color(0xFF1C1B1F),
)

internal val SubletrLightColorScheme = lightColorScheme(
	primary = subletrPinkLight,
	tertiary = Purple500,
	background = Color(0xFFFFFBFE),
	surface = Color(0xFFFFFBFE),
	onPrimary = Color.White,
	onSecondary = Color.White,
	onTertiary = Color.White,
	onBackground = Color(0xFF1C1B1F),
	onSurface = Color(0xFF1C1B1F),
)

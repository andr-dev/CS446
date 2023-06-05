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
	}
	else {
		subletrPinkLight
	}

private val subletrPinkLight = Color(0xFFF4717F)
private val subletrPinkDark = Color(0xFFF4717F)

val unselectedGray
	@Composable
	get() = if (isSystemInDarkTheme()) {
		unselectedGrayDark
	}
	else {
		unselectedGrayLight
	}
val unselectedGrayLight = Color(0xFF808080)
val unselectedGrayDark = Color(0xFF808080)

val secondaryTextColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		secondaryTextColorDark
	}
	else {
		secondaryTextColorLight
	}
val secondaryTextColorLight = Color((0xFF808080))
val secondaryTextColorDark = Color((0xFF808080))

val buttonBackgroundColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		buttonBackgroundColorDark
	}
	else {
		buttonBackgroundColorLight
	}
val buttonBackgroundColorLight = Color((0xFFD9D9D9))
val buttonBackgroundColorDark = Color((0xFFD9D9D9))

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

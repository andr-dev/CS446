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

val darkerGrayButtonColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		darkerGrayButtonColorDark
	}
	else {
		darkerGrayButtonColorLight
	}
private val darkerGrayButtonColorLight = Color((0xFFE5E5E5))
private val darkerGrayButtonColorDark = Color((0xFFE5E5E5))

val primaryBackgroundColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		primaryBackgroundColorDark
	} else {
		primaryBackgroundColorLight
	}
private val primaryBackgroundColorLight = Color((0xFFFFFFFF))
private val primaryBackgroundColorDark = Color((0xFF000000))

val secondaryBackgroundColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		secondaryBackgroundColorDark
	} else {
		secondaryBackgroundColorLight
	}
private val secondaryBackgroundColorLight = Color((0xFFF0F0F0))
private val secondaryBackgroundColorDark = Color((0xFFF0F0F0))

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
private val textFieldBackgroundColorLight = Color((0xFFF0F0F0))
private val textFieldBackgroundColorDark = Color((0xFFF0F0F0))

val squaredTextFieldBackgroundColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		squaredTextFieldBackgroundColorDark
	} else {
		squaredTextFieldBackgroundColorLight
	}
private val squaredTextFieldBackgroundColorLight = Color((0xFFFFFFFF))
private val squaredTextFieldBackgroundColorDark = Color((0xFFFFFFFF))

val textFieldBorderColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		textFieldBorderColorDark
	}
	else {
		textFieldBorderColorLight
	}
private val textFieldBorderColorLight = Color((0xFFE5E5E5))
private val textFieldBorderColorDark = Color((0xFFE5E5E5))

val surfaceColor
	@Composable
	get() = if (isSystemInDarkTheme()) {
		surfaceColorDark
	}
	else {
		surfaceColorLight
	}
private val surfaceColorLight = Color((0xFF1C1B1F))
private val surfaceColorDark = Color((0xFF1C1B1F))


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

@file:Suppress("MagicNumber", "TooManyFunctions")

package org.uwaterloo.subletr.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private fun getSubletrPink(darkTheme: Boolean): Color {
	return if (darkTheme) {
		subletrPinkDark
	} else {
		subletrPinkLight
	}
}

private val subletrPinkLight = Color(0xFFF4717F)
private val subletrPinkDark = Color(0xFFEF838F)

private fun getTextOnSubletrPink(darkTheme: Boolean): Color {
	return if (darkTheme) {
		textOnSubletrPinkDark
	} else {
		textOnSubletrPinkLight
	}
}
private val textOnSubletrPinkLight = Color((0xFFFFFFFF))
private val textOnSubletrPinkDark = Color((0xFF181213))

private fun getUnselectedGray(darkTheme: Boolean): Color {
	return if (darkTheme) {
		unselectedGrayDark
	} else {
		unselectedGrayLight
	}
}
private val unselectedGrayLight = Color(0xFF808080)
private val unselectedGrayDark = Color(0xFF808080)

private fun getPrimaryTextColor(darkTheme: Boolean): Color {
	return if (darkTheme) {
		primaryTextColorDark
	} else {
		primaryTextColorLight
	}
}
private val primaryTextColorLight = Color((0xFF000000))
private val primaryTextColorDark = Color((0xFFF6ECED))

private fun getSecondaryTextColor(darkTheme: Boolean): Color {
	return if (darkTheme) {
		secondaryTextColorDark
	} else {
		secondaryTextColorLight
	}
}
private val secondaryTextColorLight = Color((0xFF808080))
private val secondaryTextColorDark = Color((0xFFCFC0C1))

private fun getDarkerGrayButtonColor(darkTheme: Boolean): Color {
	return if (darkTheme) {
		darkerGrayButtonColorDark
	}
	else {
		darkerGrayButtonColorLight
}
	}
private val darkerGrayButtonColorLight = Color((0xFFE5E5E5))
private val darkerGrayButtonColorDark = Color((0xFF403637))

private fun getPrimaryBackgroundColor(darkTheme: Boolean): Color {
	return if (darkTheme) {
		primaryBackgroundColorDark
	} else {
		primaryBackgroundColorLight
	}
}
private val primaryBackgroundColorLight = Color((0xFFFFFBFE))
private val primaryBackgroundColorDark = Color((0xFF181213))

private fun getSecondaryBackgroundColor(darkTheme: Boolean): Color {
	return if (darkTheme) {
		secondaryBackgroundColorDark
	} else {
		secondaryBackgroundColorLight
	}
}
private val secondaryBackgroundColorLight = Color((0xFFF0F0F0))
private val secondaryBackgroundColorDark = Color((0xFF2B2930))

private fun getSecondaryButtonBackgroundColor(darkTheme: Boolean): Color {
	return if (darkTheme) {
		secondaryButtonBackgroundColorDark
	} else {
		secondaryButtonBackgroundColorLight
	}
}
private val secondaryButtonBackgroundColorLight = Color((0xFFF0F0F0))
private val secondaryButtonBackgroundColorDark = Color((0xFF2B2930))

private fun getTextFieldBackgroundColor(darkTheme: Boolean): Color {
	return if (darkTheme) {
		textFieldBackgroundColorDark
	} else {
		textFieldBackgroundColorLight
	}
}
private val textFieldBackgroundColorLight = Color((0xFFF0F0F0))
private val textFieldBackgroundColorDark = Color((0xFF2B2930))

private fun getSquaredTextFieldBackgroundColor(darkTheme: Boolean): Color {
	return if (darkTheme) {
		squaredTextFieldBackgroundColorDark
	} else {
		squaredTextFieldBackgroundColorLight
	}
}
private val squaredTextFieldBackgroundColorLight = Color(0xFFFFFFFF)
private val squaredTextFieldBackgroundColorDark = Color(0xFF000000)

private fun getTextFieldBorderColor(darkTheme: Boolean): Color {
	return if (darkTheme) {
		textFieldBorderColorDark
	}
	else {
		textFieldBorderColorLight
}
	}
private val textFieldBorderColorLight = Color((0xFFE5E5E5))
private val textFieldBorderColorDark = Color((0xFF928586))

private fun getBottomSheetColor(darkTheme: Boolean): Color {
	return if (darkTheme) {
		bottomSheetColorDark
	}
	else {
		bottomSheetColorLight
	}
}
private val bottomSheetColorLight = Color((0xFFFFFFFF))
private val bottomSheetColorDark = Color((0xFF181213))

val Purple900 = Color(0xFF4A148C)
val Purple500 = Color(0xFF9C27B0)
val mapCircleFill = Color(0x8042A5F5)
val mapCircleStroke = Color(0x991565C0)

@Immutable
data class SubletrCustomColorPalette(
	val subletrPink: Color = Color.Unspecified,
	val textOnSubletrPink: Color = Color.Unspecified,
	val unselectedGray: Color = Color.Unspecified,
	val primaryTextColor: Color = Color.Unspecified,
	val secondaryTextColor: Color = Color.Unspecified,
	val darkerGrayButtonColor: Color = Color.Unspecified,
	val primaryBackgroundColor: Color = Color.Unspecified,
	val secondaryBackgroundColor: Color = Color.Unspecified,
	val secondaryButtonBackgroundColor: Color = Color.Unspecified,
	val textFieldBackgroundColor: Color = Color.Unspecified,
	val squaredTextFieldBackgroundColor: Color = Color.Unspecified,
	val textFieldBorderColor: Color = Color.Unspecified,
	val bottomSheetColor: Color = Color.Unspecified,
)

val LocalSubletrCustomColorPalette: ProvidableCompositionLocal<SubletrCustomColorPalette> =
	staticCompositionLocalOf {
		SubletrCustomColorPalette()
	}

val OnLightSubletrCustomColorPalette = SubletrCustomColorPalette(
	subletrPink = getSubletrPink(darkTheme = false),
	textOnSubletrPink = getTextOnSubletrPink(darkTheme = false),
	unselectedGray = getUnselectedGray(darkTheme = false),
	primaryTextColor = getPrimaryTextColor(darkTheme = false),
	secondaryTextColor = getSecondaryTextColor(darkTheme = false),
	darkerGrayButtonColor = getDarkerGrayButtonColor(darkTheme = false),
	primaryBackgroundColor = getPrimaryBackgroundColor(darkTheme = false),
	secondaryBackgroundColor = getSecondaryBackgroundColor(darkTheme = false),
	secondaryButtonBackgroundColor = getSecondaryButtonBackgroundColor(darkTheme = false),
	textFieldBackgroundColor = getTextFieldBackgroundColor(darkTheme = false),
	squaredTextFieldBackgroundColor = getSquaredTextFieldBackgroundColor(darkTheme = false),
	textFieldBorderColor = getTextFieldBorderColor(darkTheme = false),
	bottomSheetColor = getBottomSheetColor(darkTheme = false)
)

val OnDarkSubletrCustomColorPalette = SubletrCustomColorPalette(
	subletrPink = getSubletrPink(darkTheme = true),
	textOnSubletrPink = getTextOnSubletrPink(darkTheme = true),
	unselectedGray = getUnselectedGray(darkTheme = true),
	primaryTextColor = getPrimaryTextColor(darkTheme = true),
	secondaryTextColor = getSecondaryTextColor(darkTheme = true),
	darkerGrayButtonColor = getDarkerGrayButtonColor(darkTheme = true),
	primaryBackgroundColor = getPrimaryBackgroundColor(darkTheme = true),
	secondaryBackgroundColor = getSecondaryBackgroundColor(darkTheme = true),
	secondaryButtonBackgroundColor = getSecondaryButtonBackgroundColor(darkTheme = true),
	textFieldBackgroundColor = getTextFieldBackgroundColor(darkTheme = true),
	squaredTextFieldBackgroundColor = getSquaredTextFieldBackgroundColor(darkTheme = true),
	textFieldBorderColor = getTextFieldBorderColor(darkTheme = true),
	bottomSheetColor = getBottomSheetColor(darkTheme = true)
)

internal val SubletrDarkColorScheme = darkColorScheme(
	primary = subletrPinkDark,
	tertiary = Purple900,
	background = primaryBackgroundColorDark,
	surface = primaryBackgroundColorDark,
	onPrimary = Color.Black,
	onSecondary = Color.Black,
	onTertiary = Color.Black,
	onBackground = Color(0xFF1C1B1F),
	onSurface = Color(0xFF1C1B1F),
)

internal val SubletrLightColorScheme = lightColorScheme(
	primary = subletrPinkLight,
	tertiary = Purple500,
	background = primaryBackgroundColorLight,
	surface = primaryBackgroundColorLight,
	onPrimary = Color.White,
	onSecondary = Color.White,
	onTertiary = Color.White,
	onBackground = Color(0xFF1C1B1F),
	onSurface = Color(0xFF1C1B1F),
)

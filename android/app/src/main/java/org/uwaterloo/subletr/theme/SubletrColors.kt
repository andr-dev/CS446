@file:Suppress("MagicNumber")

package org.uwaterloo.subletr.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Purple900 = Color(0xFF4A148C)

val Purple500 = Color(0xFF9C27B0)

val subletrPink = Color(0xFFF4717F)

val secondaryTextColor = Color((0xFF808080))
val buttonBackgroundColor = Color((0xFFD9D9D9))

internal val SubletrDarkColorScheme = darkColorScheme(
	primary = subletrPink,
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
	primary = subletrPink,
	tertiary = Purple500,
	background = Color(0xFFFFFBFE),
	surface = Color(0xFFFFFBFE),
	onPrimary = Color.White,
	onSecondary = Color.White,
	onTertiary = Color.White,
	onBackground = Color(0xFF1C1B1F),
	onSurface = Color(0xFF1C1B1F),
)

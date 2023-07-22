package org.uwaterloo.subletr.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val bottomBarItemText
	@Composable
	get() = TextStyle(
		color = MaterialTheme.subletrPalette.secondaryTextColor,
		fontFamily = FontFamily.SansSerif,
		fontWeight = FontWeight.Normal,
		fontSize = 10.sp,
		lineHeight = 12.sp,
		letterSpacing = 0.sp,
	)

val changePasswordTopBarTitle
	@Composable
	get() = TextStyle(
		color = MaterialTheme.subletrPalette.primaryTextColor,
		fontFamily = FontFamily.SansSerif,
		fontWeight = FontWeight.Bold,
		fontSize = 24.sp,
		lineHeight = 40.sp,
		letterSpacing = 0.sp,
	)
val filterTextFont
	@Composable
	get() = TextStyle(
		color = MaterialTheme.subletrPalette.primaryTextColor,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Medium,
		fontSize = 14.sp,
		letterSpacing = 0.sp,
		textAlign = TextAlign.Center
	)
val listingTitleFont
	@Composable
	get() = TextStyle(
		color = MaterialTheme.subletrPalette.primaryTextColor,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Bold,
		fontSize = 18.sp,
		letterSpacing = 0.sp,
		lineHeight = 20.sp,
		textAlign = TextAlign.Center
	)
val listingDescriptionFont
	@Composable
	get() = TextStyle(
		color = MaterialTheme.subletrPalette.secondaryTextColor,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Medium,
		fontSize = 14.sp,
		letterSpacing = 0.sp,
		lineHeight = 14.sp,
		textAlign = TextAlign.Center
	)
val filterBoldFont
	@Composable
	get() = TextStyle(
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Bold,
		fontSize = 16.sp,
		letterSpacing = 0.sp,
		textAlign = TextAlign.Center
	)
val filterRegularFont
	@Composable
	get() = TextStyle(
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp,
		letterSpacing = 0.sp,
		textAlign = TextAlign.Center
	)

val timeToDestinationFont
	@Composable
	get() = TextStyle(
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Normal,
		fontSize = 18.sp,
		letterSpacing = 0.sp,
		textAlign = TextAlign.Center
	)

val myChatBubbleFont
	@Composable
	get() = TextStyle(
		color = MaterialTheme.subletrPalette.textOnSubletrPink,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp,
		letterSpacing = 0.sp,
		textAlign = TextAlign.Center,
	)

val otherChatBubbleFont
	@Composable
	get() = TextStyle(
		color = MaterialTheme.subletrPalette.primaryTextColor,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp,
		letterSpacing = 0.sp,
		textAlign = TextAlign.Center,
	)

val avatarTextFont
	@Composable
	get() = TextStyle(
		color = MaterialTheme.subletrPalette.primaryTextColor,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Normal,
		fontSize = 20.sp,
		letterSpacing = 0.sp,
		textAlign = TextAlign.Center,
	)

val headerPrimaryTitle
	@Composable
	get() = TextStyle(
		color = MaterialTheme.subletrPalette.primaryTextColor,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Bold,
		fontSize = 16.sp,
		letterSpacing = 0.sp,
		textAlign = TextAlign.Center,
	)

val headerSecondaryTitle
	@Composable
	get() = TextStyle(
		color = MaterialTheme.subletrPalette.primaryTextColor,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Normal,
		fontSize = 12.sp,
		letterSpacing = 0.sp,
		textAlign = TextAlign.Center,
	)

val accountHeadingSmallFont
	@Composable
	get() = TextStyle(
		color = MaterialTheme.subletrPalette.secondaryTextColor,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.0.sp,
	)

val userRatingLabel
	@Composable
	get() = TextStyle(
		color =  MaterialTheme.subletrPalette.secondaryTextColor,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Medium,
		fontSize = 18.sp,
		lineHeight = 20.sp,
		letterSpacing = 0.5.sp,
	)

val SubletrTypography
	@Composable
	get() = Typography(
		bodyLarge = TextStyle(
			color = MaterialTheme.subletrPalette.primaryTextColor,
			fontFamily = FontFamily.Default,
			fontWeight = FontWeight.Normal,
			fontSize = 16.sp,
			lineHeight = 24.sp,
			letterSpacing = 0.5.sp,
		),
		titleLarge = TextStyle(
			color = MaterialTheme.subletrPalette.primaryTextColor,
			fontFamily = FontFamily.Default,
			fontWeight = FontWeight.Bold,
			fontSize = 50.sp,
			lineHeight = 28.sp,
			letterSpacing = 0.sp,
		),
		titleMedium = TextStyle(
			color = MaterialTheme.subletrPalette.primaryTextColor,
			fontFamily = FontFamily.Default,
			fontWeight = FontWeight.Bold,
			fontSize = 38.sp,
			lineHeight = 28.sp,
			letterSpacing = 0.sp,
		),
		titleSmall = TextStyle(
			color = MaterialTheme.subletrPalette.primaryTextColor,
			fontFamily = FontFamily.Default,
			fontWeight = FontWeight.Medium,
			fontSize = 25.sp,
			lineHeight = 28.sp,
			letterSpacing = 0.sp,
		),
		bodyMedium = TextStyle(
			color = MaterialTheme.subletrPalette.secondaryTextColor,
			fontFamily = FontFamily.Default,
			fontWeight = FontWeight.Light,
			fontSize = 20.sp,
			lineHeight = 24.sp,
			letterSpacing = 0.5.sp,
		),
		labelSmall = TextStyle(
			color = MaterialTheme.subletrPalette.secondaryTextColor,
			fontFamily = FontFamily.Default,
			fontWeight = FontWeight.Medium,
			fontSize = 11.sp,
			lineHeight = 16.sp,
			letterSpacing = 0.5.sp,
		),
		labelMedium = TextStyle(
			color = MaterialTheme.subletrPalette.secondaryTextColor,
			fontFamily = FontFamily.Default,
			fontWeight = FontWeight.Light,
			fontSize = 20.sp,
			lineHeight = 24.sp,
			letterSpacing = 0.5.sp,
		),
		displayMedium = TextStyle(
			color = MaterialTheme.subletrPalette.primaryTextColor,
			fontFamily = FontFamily.Default,
			fontWeight = FontWeight.Bold,
			fontSize = 19.sp,
			letterSpacing = 0.sp,
			textAlign = TextAlign.Center,
		),
		displaySmall = TextStyle(
			color = MaterialTheme.subletrPalette.secondaryTextColor,
			fontFamily = FontFamily.Default,
			fontWeight = FontWeight.Medium,
			fontSize = 15.sp,
			lineHeight = 16.sp,
			letterSpacing = 0.5.sp,
		),
		displayLarge = TextStyle(
			color = MaterialTheme.subletrPalette.primaryTextColor,
			fontFamily = FontFamily.Default,
			fontWeight = FontWeight.Bold,
			fontSize = 22.sp,
			letterSpacing = 0.sp,
			textAlign = TextAlign.Center,
		),
	)

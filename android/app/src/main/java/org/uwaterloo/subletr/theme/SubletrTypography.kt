package org.uwaterloo.subletr.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val bottomBarItemText
	@Composable
	get() = TextStyle(
		color = secondaryTextColor,
		fontFamily = FontFamily.SansSerif,
		fontWeight = FontWeight.Normal,
		fontSize = 10.sp,
		lineHeight = 12.sp,
		letterSpacing = 0.sp,
	)

val changePasswordTopBarTitle
	@Composable
	get() = TextStyle(
		color = primaryTextColor,
		fontFamily = FontFamily.SansSerif,
		fontWeight = FontWeight.Bold,
		fontSize = 24.sp,
		lineHeight = 40.sp,
		letterSpacing = 0.sp,
	)

val SubletrTypography = Typography(
	bodyLarge = TextStyle(
		color = Color.Black,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.5.sp
	),
	titleLarge = TextStyle(
		color = Color.Black,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Bold,
		fontSize = 50.sp,
		lineHeight = 28.sp,
		letterSpacing = 0.sp
	),
	titleMedium = TextStyle(
		color = Color.Black,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Bold,
		fontSize = 38.sp,
		lineHeight = 28.sp,
		letterSpacing = 0.sp
	),
	titleSmall = TextStyle(
		color = Color.Black,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Medium,
		fontSize = 25.sp,
		lineHeight = 28.sp,
		letterSpacing = 0.sp
	),
	bodyMedium = TextStyle(
		color = Color.Gray,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Light,
		fontSize = 20.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.5.sp
	),
	labelSmall = TextStyle(
		color = Color.Gray,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Medium,
		fontSize = 11.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.5.sp
	),
	labelMedium = TextStyle(

		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Light,
		fontSize = 20.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.5.sp
	),
	displayMedium = TextStyle(
		color = Color.Black,
		fontFamily = FontFamily.Default,
		fontWeight = FontWeight.Bold,
		fontSize = 19.sp,
		letterSpacing = 0.sp,
		textAlign = TextAlign.Center
	),
)

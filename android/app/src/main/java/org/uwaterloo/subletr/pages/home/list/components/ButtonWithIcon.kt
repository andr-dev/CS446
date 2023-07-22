package org.uwaterloo.subletr.pages.home.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun ButtonWithIcon(
	modifier: Modifier = Modifier,
	iconId: Int,
	onClick: () -> Unit,
	contentDescription: String,
	colors: ButtonColors = ButtonDefaults.buttonColors(
		containerColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
		contentColor = MaterialTheme.subletrPalette.primaryTextColor,
	),
	elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
	border: BorderStroke? = null,
) {
	SecondaryButton(
		onClick = onClick,
		modifier = modifier
			.wrapContentSize(align = Alignment.Center),
		contentPadding = PaddingValues(all = dimensionResource(id = R.dimen.zero)),
		colors = colors,
		border = border,
		elevation = elevation,
		content = {
			Icon(
				painter = painterResource(
					id = iconId
				),
				contentDescription = contentDescription,
				tint = MaterialTheme.subletrPalette.primaryTextColor,
			)
		},
	)
}

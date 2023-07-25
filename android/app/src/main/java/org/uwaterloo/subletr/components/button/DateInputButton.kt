package org.uwaterloo.subletr.components.button

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun DateInputButton(
	modifier: Modifier,
	labelStringId: Int,
	value: String,
	labelColor: Color = MaterialTheme.subletrPalette.secondaryTextColor,
	onClick: () -> Unit
) {
	RoundedTextField(
		modifier = modifier,
		placeholder = {
			Text(
				text = stringResource(id = R.string.placeholder_date),
				color = MaterialTheme.subletrPalette.secondaryTextColor,
			)
		},
		label = {
			Text(
				text = stringResource(id = labelStringId),
				color = labelColor,
			)
		},
		trailingIcon = {
			IconButton(
				onClick = onClick
			) {
				Icon(
					painter = painterResource(
						id = R.drawable.calendar_outline_gray_24
					),
					contentDescription = stringResource(id = R.string.open_date_picker),
				)
			}
		},
		value = value,
		readOnly = true,
		onValueChange = {},
	)
}

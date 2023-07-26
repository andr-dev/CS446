package org.uwaterloo.subletr.components.textfield

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun NumericalInputTextField(
	modifier: Modifier = Modifier,
	labelId: Int,
	uiStateValue: Int,
	triggerOnValueChange: () -> Unit = {},
	changeValue: (Int) -> Unit,
	attemptCreate: Boolean,
	prefix: @Composable (() -> Unit)?,
) {
	val numberPattern = remember { Regex("^\\d+\$") }

	RoundedTextField(
		modifier = modifier
			.fillMaxWidth()
			.border(
				width = dimensionResource(id = R.dimen.xxxs),
				color =
				if (!attemptCreate || uiStateValue > 0) MaterialTheme.subletrPalette.textFieldBorderColor
				else MaterialTheme.subletrPalette.warningColor,
				shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
			),
		placeholder = {
			Text(
				text = stringResource(id = labelId),
				color = MaterialTheme.subletrPalette.secondaryTextColor,
			)
		},
		label = {
			Text(
				text = stringResource(id = labelId),
				color =
				if (!attemptCreate || uiStateValue > 0)
					MaterialTheme.subletrPalette.secondaryTextColor
				else
					MaterialTheme.subletrPalette.warningColor,
			)
		},
		prefix = prefix,
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		value = if (uiStateValue == 0) "" else uiStateValue.toString(),
		onValueChange = {
			if (it.isEmpty()) {
				changeValue(0)
			} else if (it.matches(numberPattern)) {
				changeValue(it.toInt())
			}
		},
	)
}

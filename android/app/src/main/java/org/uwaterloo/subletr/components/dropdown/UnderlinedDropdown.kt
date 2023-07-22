package org.uwaterloo.subletr.components.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun <T> UnderlinedDropdown(
	modifier: Modifier = Modifier,
	dropdownItems: Array<T>,
	selectedDropdownItem: T,
	dropdownItemToString: @Composable (T) -> String,
	setSelectedDropdownItem: (T) -> Unit,
) {
	var expanded by remember {
		mutableStateOf(false)
	}

	Box(
		modifier = modifier
			.wrapContentSize(Alignment.TopEnd)
			.fillMaxWidth(1.0f)
	) {
		val subletrPink = MaterialTheme.subletrPalette.subletrPink
		val strokeWidthDp = dimensionResource(id = R.dimen.xxxxs)
		Button(
			modifier = Modifier
				.fillMaxWidth(1.0f)
				.height(dimensionResource(id = R.dimen.xl))
				.drawBehind {
					val strokeWidth = strokeWidthDp.toPx()
					val y = size.height - strokeWidth / 2

					drawLine(
						subletrPink,
						Offset(0f, y),
						Offset(size.width, y),
						strokeWidth
					)
				},
			onClick = { expanded = true },
			shape = RectangleShape,
			colors = ButtonDefaults.buttonColors(
				containerColor = Color.Transparent,
				disabledContainerColor = Color.Transparent,
				contentColor = MaterialTheme.subletrPalette.secondaryTextColor,
				disabledContentColor = MaterialTheme.subletrPalette.secondaryTextColor,
			),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(1.0f),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text = dropdownItemToString(selectedDropdownItem),
					color = MaterialTheme.subletrPalette.secondaryTextColor,
				)
				Icon(
					painter = painterResource(
						id =
						if (expanded) R.drawable.arrow_drop_up_soild_gray_24
						else R.drawable.arrow_drop_down_solid_gray_24
					),
					contentDescription = stringResource(
						id =
						if (expanded) R.string.drop_up_arrow
						else R.string.drop_down_arrow
					)
				)
			}
		}

		DropdownMenu(
			modifier = modifier
				.background(Color.Transparent),
			expanded = expanded,
			onDismissRequest = { expanded = false },
		) {
			dropdownItems.forEach {
				DropdownMenuItem(
					text = { Text(text = dropdownItemToString(it)) },
					onClick = {
						setSelectedDropdownItem(it)
						expanded = false
					},
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
@Suppress("UnusedPrivateMember")
private fun UnderlinedDropdownPreview() {
	UnderlinedDropdown(
		dropdownItems = arrayOf("Test 1", "Test 2"),
		selectedDropdownItem = "Test 1",
		dropdownItemToString = {it},
		setSelectedDropdownItem = {},
	)
}

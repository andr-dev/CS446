package org.uwaterloo.subletr.components.dropdown

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.theme.subletrPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> RoundedExposedDropdown(
	modifier: Modifier = Modifier,
	dropdownItems: Array<T>,
	labelId: Int,
	selectedDropdownItem: T,
	dropdownItemToString: @Composable (T) -> String,
	setSelectedDropdownItem: (T) -> Unit,
) {
	var expanded by remember {
		mutableStateOf(false)
	}

	ExposedDropdownMenuBox(
		modifier = modifier,
		expanded = expanded,
		onExpandedChange = { expanded = !expanded },
	) {
		RoundedTextField(
			modifier = modifier
				.fillMaxWidth()
				.menuAnchor()
				.border(
					dimensionResource(id = R.dimen.xxxs),
					MaterialTheme.subletrPalette.textFieldBorderColor,
					RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl))
				),
			readOnly = true,
			value = dropdownItemToString(selectedDropdownItem),
			onValueChange = {},
			label = { Text(text = stringResource(id = labelId)) },
			trailingIcon = {
				ExposedDropdownMenuDefaults.TrailingIcon(
					expanded = expanded
				)
			},
		)
		DropdownMenu(
			modifier = modifier.exposedDropdownSize(),
			expanded = expanded,
			onDismissRequest = { expanded = false },
		) {
			dropdownItems.forEach {
				DropdownMenuItem(
					modifier = modifier,
					onClick = {
						setSelectedDropdownItem(it)
						expanded = false
					},
					text = { Text(text = dropdownItemToString(it)) },
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun RoundedExposedDropdownPreview() {
	RoundedExposedDropdown(
		dropdownItems = arrayOf("Test 1", "Test 2"),
		selectedDropdownItem = "Test 1",
		dropdownItemToString = {it},
		setSelectedDropdownItem = {},
		labelId = R.string.app_name
	)
}

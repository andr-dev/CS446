package org.uwaterloo.subletr.components.addressautocomplete

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.google.android.libraries.places.api.model.AutocompletePrediction
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.theme.subletrPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressAutocomplete(
	modifier: Modifier = Modifier,
	fullAddress: String,
	addressAutocompleteOptions: ArrayList<AutocompletePrediction>,
	setAddress: (String) -> Unit,
	attemptingCreate: Boolean = false,
	onPredictionSelection: () -> Unit = {},
) {
	var autocompleteExpanded by remember { mutableStateOf(false) }
	val focusManager = LocalFocusManager.current

	ExposedDropdownMenuBox(
		modifier = modifier,
		expanded = autocompleteExpanded,
		onExpandedChange = { autocompleteExpanded = true },
	) {
		RoundedTextField(
			modifier = modifier
				.fillMaxWidth()
				.menuAnchor()
				.border(
					width = dimensionResource(id = R.dimen.xxxs),
					color =
					if (!attemptingCreate || fullAddress.isNotBlank())
						MaterialTheme.subletrPalette.textFieldBorderColor
					else
						MaterialTheme.subletrPalette.warningColor,
					shape = RoundedCornerShape(dimensionResource(id = R.dimen.xxxxl)),
				),
			placeholder = {
				Text(
					text = stringResource(id = R.string.address_format_label),
					color = MaterialTheme.subletrPalette.secondaryTextColor,
				)
			},
			label = {
				Text(
					text = stringResource(id = R.string.address),
					color =
						if (!attemptingCreate || fullAddress.isNotBlank())
							MaterialTheme.subletrPalette.secondaryTextColor
						else
							MaterialTheme.subletrPalette.warningColor,
				)
			},
			value = fullAddress,
			onValueChange = {
				setAddress(it)
			}
		)

		ExposedDropdownMenu(
			modifier = modifier,
			expanded = autocompleteExpanded,
			onDismissRequest = { autocompleteExpanded = false },
		) {
			addressAutocompleteOptions.map { prediction ->
				DropdownMenuItem(
					modifier = modifier,
					text = { Text(text = prediction.getFullText(null).toString()) },
					onClick = {
						setAddress(prediction.getFullText(null).toString())
						autocompleteExpanded = false
						focusManager.clearFocus()
						onPredictionSelection()
					},
				)
			}
		}
	}
}

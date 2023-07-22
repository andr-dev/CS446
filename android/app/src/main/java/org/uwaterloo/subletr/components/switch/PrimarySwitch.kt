package org.uwaterloo.subletr.components.switch

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun PrimarySwitch(
	checked: Boolean,
	onCheckedChange: ((Boolean) -> Unit)?,
	modifier: Modifier = Modifier,
	thumbContent: (@Composable () -> Unit)? = null,
	enabled: Boolean = true,
	colors: SwitchColors = SwitchDefaults.colors(
		checkedTrackColor = MaterialTheme.subletrPalette.subletrPink,
		checkedBorderColor = MaterialTheme.subletrPalette.subletrPink,
		uncheckedTrackColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
		uncheckedBorderColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
		checkedThumbColor = MaterialTheme.subletrPalette.textOnSubletrPink,
		uncheckedThumbColor = MaterialTheme.subletrPalette.textOnSubletrPink,
		disabledUncheckedTrackColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
		disabledUncheckedThumbColor = MaterialTheme.subletrPalette.textOnSubletrPink,
		disabledUncheckedBorderColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
	),
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
	Switch(
		checked = checked,
		onCheckedChange = onCheckedChange,
		modifier = modifier,
		thumbContent = thumbContent,
		enabled = enabled,
		colors = colors,
		interactionSource = interactionSource,
	)
}

@Preview(showBackground = true)
@Composable
@Suppress("UnusedPrivateMember")
private fun PrimarySwitchCheckedPreview() {
	PrimarySwitch(checked = true, onCheckedChange = {})
}

@Preview(showBackground = true)
@Composable
@Suppress("UnusedPrivateMember")
private fun PrimarySwitchUncheckedPreview() {
	PrimarySwitch(checked = false, onCheckedChange = {})
}

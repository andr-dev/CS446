package org.uwaterloo.subletr.components.switch

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.uwaterloo.subletr.theme.darkerGrayButtonColor
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.textOnSubletrPink

@Composable
fun PrimarySwitch(
	checked: Boolean,
	onCheckedChange: ((Boolean) -> Unit)?,
	modifier: Modifier = Modifier,
	thumbContent: (@Composable () -> Unit)? = null,
	enabled: Boolean = true,
	colors: SwitchColors = SwitchDefaults.colors(
		checkedTrackColor = subletrPink,
		checkedBorderColor = subletrPink,
		uncheckedTrackColor = secondaryButtonBackgroundColor,
		uncheckedBorderColor = secondaryButtonBackgroundColor,
		checkedThumbColor = textOnSubletrPink,
		uncheckedThumbColor = textOnSubletrPink,
		disabledUncheckedTrackColor = secondaryButtonBackgroundColor,
		disabledUncheckedThumbColor = textOnSubletrPink,
		disabledUncheckedBorderColor = secondaryButtonBackgroundColor,
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

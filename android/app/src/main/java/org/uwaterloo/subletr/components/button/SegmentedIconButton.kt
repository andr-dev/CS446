package org.uwaterloo.subletr.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.unselectedGray

@Suppress("LongParameterList")
@Composable
fun SegmentedIconButton(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	enabled: Boolean = true,
	colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	iconPainter: Painter,
	iconContentDescription: String?,
	selectedTint: Color,
	unselectedTint: Color,
	selected: Boolean,
	position: SegmentedIconButtonPosition,
) {
	val buttonShape = when (position) {
		SegmentedIconButtonPosition.START ->
			RoundedCornerShape(
				topStart = dimensionResource(id = R.dimen.xl),
				topEnd = dimensionResource(id = R.dimen.zero),
				bottomEnd = dimensionResource(id = R.dimen.zero),
				bottomStart = dimensionResource(id = R.dimen.xl),
			)

		SegmentedIconButtonPosition.MIDDLE ->
			RoundedCornerShape(
				topStart = dimensionResource(id = R.dimen.zero),
				topEnd = dimensionResource(id = R.dimen.zero),
				bottomEnd = dimensionResource(id = R.dimen.zero),
				bottomStart = dimensionResource(id = R.dimen.zero),
			)

		SegmentedIconButtonPosition.END ->
			RoundedCornerShape(
				topStart = dimensionResource(id = R.dimen.zero),
				topEnd = dimensionResource(id = R.dimen.xl),
				bottomEnd = dimensionResource(id = R.dimen.xl),
				bottomStart = dimensionResource(id = R.dimen.zero),
			)
	}

	Box(
		modifier = modifier
			.clip(
				shape = buttonShape,
			)
			.background(color = secondaryButtonBackgroundColor)
			.border(
				width = dimensionResource(id = R.dimen.xxxxs),
				color = if (selected) selectedTint else unselectedTint,
				shape = buttonShape,
			)
	) {
		IconButton(
			modifier = Modifier,
			onClick = onClick,
			enabled = enabled,
			colors = colors,
			interactionSource = interactionSource,
		) {
			Icon(
				painter = iconPainter,
				contentDescription = iconContentDescription,
				tint = if (selected) selectedTint else unselectedTint
			)
		}
	}
}

enum class SegmentedIconButtonPosition {
	START,
	MIDDLE,
	END,
}

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true)
@Composable
private fun SegmentedStartIconButtonUnselectedPreview() {
	SegmentedIconButton(
		iconPainter = painterResource(id = R.drawable.person_solid_pink_24),
		iconContentDescription = null,
		selectedTint = subletrPink,
		unselectedTint = unselectedGray,
		selected = false,
		onClick = {},
		position = SegmentedIconButtonPosition.START,
	)
}

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true)
@Composable
private fun SegmentedEndIconButtonSelectedPreview() {
	SegmentedIconButton(
		iconPainter = painterResource(id = R.drawable.person_solid_pink_24),
		iconContentDescription = null,
		selectedTint = subletrPink,
		unselectedTint = unselectedGray,
		selected = true,
		onClick = {},
		position = SegmentedIconButtonPosition.END,
	)
}

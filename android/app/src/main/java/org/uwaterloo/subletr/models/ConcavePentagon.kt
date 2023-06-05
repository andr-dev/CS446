package org.uwaterloo.subletr.models

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class ConcavePentagon : androidx.compose.ui.graphics.Shape {
	override fun createOutline(
		size: Size,
		layoutDirection: LayoutDirection,
		density: Density
	): Outline {
		val path = Path().apply {
			moveTo(0f, 0f)
			lineTo(size.width, 0f)
			lineTo(size.width, size.height)
			lineTo(size.width / 2f, size.height / 4 * 3)
			lineTo(0f, size.height)
			lineTo(0f, 0f)
		}

		return Outline.Generic(path = path);
	}
}

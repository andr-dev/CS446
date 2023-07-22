package org.uwaterloo.subletr.components.rating

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.textFieldBorderColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StaticRatingsBar(
	modifier: Modifier,
	rating: Float,
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.xxxs))
	) {
		(1..5).toList().forEach {
			RatingStar(rating =
			if (it - rating <= 0.0f) 1.0f
			else if (it - rating >= 1.0f) 0.0f
			else 1 - (it - rating))
		}
	}
}

private val starShape = GenericShape { size, _ ->
	addPath(starPath(size.height))
}

private val starPath = { size: Float ->
	Path().apply {
		val outerRadius: Float = size / 1.8f
		val innerRadius: Double = outerRadius / 2.5
		var rot: Double = Math.PI / 2 * 3
		val cx: Float = size / 2
		val cy: Float = size / 20 * 11
		var x: Float = cx
		var y: Float = cy
		val step = Math.PI / 5

		moveTo(cx, cy - outerRadius)
		repeat(5) {
			x = (cx + cos(rot) * outerRadius).toFloat()
			y = (cy + sin(rot) * outerRadius).toFloat()
			lineTo(x, y)
			rot += step

			x = (cx + cos(rot) * innerRadius).toFloat()
			y = (cy + sin(rot) * innerRadius).toFloat()
			lineTo(x, y)
			rot += step
		}
		close()
	}
}

@Composable
private fun RatingStar(
	rating: Float,
	ratingColor: Color = subletrPink,
	backgroundColor: Color = textFieldBorderColor,
) {
	BoxWithConstraints(
		modifier = Modifier
			.fillMaxHeight()
			.aspectRatio(1f)
			.clip(starShape),
	) {
		Canvas(modifier = Modifier.size(maxHeight)) {
			drawRect(
				brush = SolidColor(backgroundColor),
				size = Size(
					height = size.height * 1.4f,
					width = size.width * 1.4f
				),
				topLeft = Offset(
					x = -(size.width * 0.1f),
					y = -(size.height * 0.1f)
				),
			)
			if (rating > 0) {
				drawRect(
					brush = SolidColor(ratingColor),
					size = Size(
						height = size.height * 1.1f,
						width = size.width * rating
					)
				)
			}
		}
	}
}

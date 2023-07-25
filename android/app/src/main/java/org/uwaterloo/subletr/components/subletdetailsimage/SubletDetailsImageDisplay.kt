package org.uwaterloo.subletr.components.subletdetailsimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun SubletDetailsImageDisplay(
	modifier: Modifier = Modifier,
	imageBitmap: ImageBitmap? = null
) {
	if (imageBitmap != null) {
		Image(
			bitmap = imageBitmap,
			contentDescription = stringResource(id = R.string.listing_image),
			contentScale = ContentScale.Fit,
			modifier = modifier
				.size(dimensionResource(id = R.dimen.listing_details_image))
				.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xxs))),
		)
	} else {
		Image(
			painter = painterResource(id = R.drawable.room),
			contentDescription = stringResource(id = R.string.listing_image),
			contentScale = ContentScale.Fit,
			modifier = modifier
				.size(dimensionResource(id = R.dimen.listing_details_image))
				.clip(RoundedCornerShape(dimensionResource(id = R.dimen.xxs)))
				.border(
					width = dimensionResource(id = R.dimen.xxxxs),
					color = MaterialTheme.subletrPalette.textFieldBorderColor,
					shape = RoundedCornerShape(
						size = dimensionResource(id = R.dimen.xxs),
					),
				),
		)
	}
}

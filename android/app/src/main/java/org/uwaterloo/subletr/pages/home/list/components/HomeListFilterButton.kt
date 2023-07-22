package org.uwaterloo.subletr.pages.home.list.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.filterTextFont
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun FilterButton(
	modifier: Modifier = Modifier,
	filterName: String,
	onClick: () -> Unit,
) {
	Button(
		modifier = modifier
			.wrapContentSize()
			.border(
				width = dimensionResource(id = R.dimen.xxxxs),
				color = MaterialTheme.subletrPalette.textFieldBorderColor,
				shape = RoundedCornerShape(
					size = dimensionResource(id = R.dimen.m),
				),
			)
			.height(dimensionResource(id = R.dimen.l)),
		contentPadding = PaddingValues(
			horizontal = dimensionResource(id = R.dimen.s),
			vertical = dimensionResource(id = R.dimen.zero),
		),
		colors = ButtonDefaults.buttonColors(
			containerColor = MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
			contentColor = MaterialTheme.subletrPalette.secondaryTextColor,
		),
		shape = RoundedCornerShape(
			size = dimensionResource(id = R.dimen.s),
		),
		onClick = {
			onClick()
		},
	) {
		Row(
			modifier = Modifier.wrapContentWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			Text(
				text = filterName,
				style = filterTextFont,
			)

			Icon(
				painter = painterResource(
					id = R.drawable.arrow_drop_down_solid_gray_24
				),
				contentDescription = stringResource(
					id = R.string.drop_down_arrow
				)
			)
		}
	}
}

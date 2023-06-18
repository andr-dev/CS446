package org.uwaterloo.subletr.pages.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.subletrPink

@Composable
fun HomePageView(
	modifier: Modifier = Modifier,
) {
	LazyColumn(
		modifier = modifier
			.fillMaxWidth(1.0f)
			.wrapContentHeight()
			.imePadding()
			.padding(
				dimensionResource(id = R.dimen.s),
				dimensionResource(id = R.dimen.m),
				dimensionResource(id = R.dimen.s),
				dimensionResource(id = R.dimen.m)
			),
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		item {
			Row(
				modifier = modifier
					.fillMaxWidth(1.0f)
					.requiredHeight(64.dp)
					.background(Color.Red),
				verticalAlignment = Alignment.CenterVertically

			) {
				Text(text = "View Sublets", style = MaterialTheme.typography.titleMedium)
				Spacer(
					modifier = Modifier.width(50.dp)
				)
				ViewSwitch()
			}

		}
		item {
			Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.l)))
		}
		item {
			Row(
				modifier = modifier
					.fillMaxWidth(1.0f)
					.background(Color.Red)
					.requiredHeight(64.dp),
				verticalAlignment = Alignment.CenterVertically

			) {
				ButtonWithIcon(
					modifier = Modifier
						.width(dimensionResource(id = R.dimen.xl))
						.height(dimensionResource(id = R.dimen.l)),
					iconId = R.drawable.tune_round_black_24,
					onClick = {},
					contentDescription = "filter menu"
				)
			}
		}


	}
}

@Composable
fun ButtonWithIcon(
	modifier: Modifier = Modifier,
	iconId: Int,
	onClick: () -> Unit,
	contentDescription: String,
) {
	SecondaryButton(onClick = onClick,
		modifier = Modifier.wrapContentSize(align = Alignment.Center),
		contentPadding = PaddingValues(0.dp),
		content = {
			Box(
				modifier = modifier,
				contentAlignment = Alignment.Center
			) {
				Icon(
					painter = painterResource(
						id = iconId
					),
					contentDescription = contentDescription,
				)
			}
		})
}


@Composable
fun ViewSwitch(modifier: Modifier = Modifier) {
	var islistView = remember { mutableStateOf(true) }
	Row(
		modifier = modifier
			.wrapContentWidth(),
	) {
		Box(
			modifier = Modifier
				.width(dimensionResource(id = R.dimen.xl))
				.height(dimensionResource(id = R.dimen.l))
				.border(
					BorderStroke(
						1.dp,
						color = if (islistView.value) subletrPink else secondaryButtonBackgroundColor
					),
					shape = RoundedCornerShape(
						topStart = dimensionResource(id = R.dimen.s),
						topEnd = 0.dp,
						bottomStart = dimensionResource(id = R.dimen.s),
						bottomEnd = 0.dp,
					),
				)
				.clip(
					RoundedCornerShape(
						topStart = dimensionResource(id = R.dimen.s),
						topEnd = 0.dp,
						bottomStart = dimensionResource(id = R.dimen.s),
						bottomEnd = 0.dp,
					)
				)
				.background(if (islistView.value) Color.White else secondaryButtonBackgroundColor)
				.padding(
					dimensionResource(id = R.dimen.s),
					dimensionResource(id = R.dimen.xxs),
					dimensionResource(id = R.dimen.xs),
					dimensionResource(id = R.dimen.xxs)
				)
				.clickable {
					islistView.value = true

				},
			content = {
				Icon(
					painter = painterResource(
						id = R.drawable.view_list_outline_pink_24
					),
					contentDescription = "lol",
					tint = if (islistView.value) subletrPink else Color.Black
				)

			}

		)
		Box(
			modifier = Modifier
				.width(dimensionResource(id = R.dimen.xl))
				.height(dimensionResource(id = R.dimen.l))
				.border(
					BorderStroke(
						1.dp,
						color = if (!islistView.value) subletrPink else secondaryButtonBackgroundColor
					),
					shape = RoundedCornerShape(
						topStart = 0.dp,
						topEnd = dimensionResource(id = R.dimen.s),
						bottomStart = 0.dp,
						bottomEnd = dimensionResource(id = R.dimen.s)
					),
				)
				.clip(
					RoundedCornerShape(
						topStart = 0.dp,
						topEnd = dimensionResource(id = R.dimen.s),
						bottomStart = 0.dp,
						bottomEnd = dimensionResource(id = R.dimen.s)
					)
				)
				.background(if (!islistView.value) Color.White else secondaryButtonBackgroundColor)
				.padding(
					dimensionResource(id = R.dimen.xs),
					dimensionResource(id = R.dimen.xxs),
					dimensionResource(id = R.dimen.xs),
					dimensionResource(id = R.dimen.xxs)
				)
				.clickable {
					islistView.value = false
				},
			content = {
				Icon(
					painter = painterResource(
						id = R.drawable.map_solid_pink_24,
					),
					contentDescription = "lol",
					tint = if (!islistView.value) subletrPink else Color.Black
				)

			}
		)
	}

}
@Composable
fun filter(){
	
}
@Preview(showBackground = true)
@Composable
fun HomePageViewLoadingPreview() {
	HomePageView()
}

//@Preview(showBackground = true)
//@Composable
//fun LoginPageViewLoadedPreview() {
//	SubletrTheme {
//		LoginPageView(
//			uiState = LoginPageUiState.Loaded(
//				email = "",
//				password = "",
//				infoTextStringId = null,
//			),
//		)
//	}
//}

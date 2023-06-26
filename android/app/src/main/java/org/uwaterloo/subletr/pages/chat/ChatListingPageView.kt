package org.uwaterloo.subletr.pages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import org.uwaterloo.subletr.theme.textOnSubletrPink
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.subletrPink

@Composable
fun ChatListingPageView(
	modifier: Modifier = Modifier,
	viewModel : ChatListingPageViewModel = hiltViewModel()
){
	Column (modifier = modifier){
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(dimensionResource(id = R.dimen.xxxl))
				.padding(dimensionResource(id = R.dimen.xs))
				.background(Color.Transparent),
			contentAlignment = Alignment.BottomStart,
		) {
			Text(
				stringResource(id = R.string.Messages),
				style = MaterialTheme.typography.titleMedium,
			)
		}
		displayEntries()

	}

}


@Composable
fun ListItem(name : String, last_msg : String) {
	Box(
		modifier = Modifier
			.padding(
				vertical = dimensionResource(id = R.dimen.xxs),
				horizontal = dimensionResource(id = R.dimen.xs)
			)
			.wrapContentHeight()
			.fillMaxWidth(1.0f)
			.clip(
				RoundedCornerShape(
					dimensionResource(id = R.dimen.s)
				)
			)
			.background(secondaryButtonBackgroundColor)
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(name = name)
				Column(
					modifier = Modifier
						.weight(1f)
						.padding(vertical = dimensionResource(id = R.dimen.s)),
					//horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center,
				)

				{
					Text(text = "$name",
						style = MaterialTheme.typography.titleSmall)
					Text(text = last_msg,
						style = MaterialTheme.typography.bodySmall)
				}
				notification()
			}

	}
}

@Composable
fun Icon(name : String) {
	LazyColumn(
	) {
		item {
			Box(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.s))
					.clip(CircleShape),
			) {
				Box(
					modifier = Modifier
						.width(dimensionResource(id = R.dimen.xxl))
						.height(dimensionResource(id = R.dimen.xxl))
						.background(subletrPink),
					contentAlignment = Alignment.Center
				) {
					Text(
						text = name[0].toString(),
						style = MaterialTheme.typography.titleSmall,
						color = textOnSubletrPink
					)
				}

			}
		}
	}
}

@Composable
fun notification(n : Int = 1) {
	LazyColumn(
	) {
		item {
			Box(
				modifier = Modifier
					.padding(dimensionResource(id = R.dimen.s))
					.clip(CircleShape),
			) {
				Box(
					modifier = Modifier
						.width(dimensionResource(id = R.dimen.l))
						.height(dimensionResource(id = R.dimen.l))
						.background(Color(0xFF6ABC95)),
					contentAlignment = Alignment.Center
				) {
					Text(
						text = n.toString(),
						color = textOnSubletrPink,
						style = MaterialTheme.typography.labelLarge
					)
				}

			}
		}
	}
}
@Composable
fun displayEntries(names : List<String> = List(2){"Alex Lin"},
					msg : List<String> = List(2){"This is test dialog"}) {
	assert(names.size == msg.size)
	(names.indices).forEach { i ->
		ListItem(name = names[i], last_msg = msg[i])
	}
}

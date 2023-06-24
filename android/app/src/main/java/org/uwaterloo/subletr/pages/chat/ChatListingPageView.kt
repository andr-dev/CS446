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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.subletrPink

@Composable
fun ChatListingPageView(
	modifier: Modifier = Modifier,
	viewModel : ChatListingPageViewModel = hiltViewModel()
){
	Column {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(100.dp)
				.padding(10.dp)
				.background(Color.Transparent),
			contentAlignment = Alignment.BottomStart
		) {
			Text(
				text = "Messages",
				color = Color.Black,
				fontSize = 40.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(vertical = 8.dp)
			)
		}
		displayEntries()

	}

}


@Composable
fun ListItem(name : String, last_msg : String) {
	Surface(color = Color.LightGray,
		modifier = Modifier
			.padding(vertical = 5.dp, horizontal = 10.dp)
			.clip(RoundedCornerShape(40.dp)),
		) {



		Column(modifier = Modifier
			.fillMaxWidth()) {

			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(name)
				Column(modifier = Modifier
					.weight(1f)
					.padding(vertical = 40.dp),
					//horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center,)

				{
					Text(text = "$name :",
						fontSize = 20.sp,
						fontWeight = FontWeight.Bold)
					Text(text = last_msg,
						fontSize = 15.sp,
						color = Color.DarkGray)
				}
				notification()
			}
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
					.padding(dimensionResource(id = R.dimen.l))
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
						color = Color.White,
						fontSize = 32.sp,
						fontWeight = FontWeight.Bold
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
						color = Color.White,
						fontSize = 20.sp,
						fontWeight = FontWeight.Bold
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

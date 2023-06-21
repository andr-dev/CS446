package org.uwaterloo.subletr.pages.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.recyclerview.widget.RecyclerView
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.subletrPink

@Composable
fun ChatPageView(
	modifier: Modifier = Modifier,
	viewModel : ChatPageViewModel = hiltViewModel()
){
	//RecyclerView()
	Column {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(60.dp)
				.background(subletrPink),
			contentAlignment = Alignment.BottomCenter
		) {
			Text(
				text = "Chat",
				color = Color.White,
				fontSize = 16.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(vertical = 8.dp) // Adjust vertical padding as needed
			)
		}
		displayEntries()

	}

}


@Composable
fun ListItem(name : String, last_msg : String) {
	Surface(color = Color.White,
		modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp),
		border = BorderStroke(2.dp, Color.Black)
	) {



		Column(modifier = Modifier
			.padding(7.dp)
			.fillMaxWidth()) {

			Row() {
				Icon(name)
				Column(modifier = Modifier
					.weight(1f)
					.padding(vertical = 40.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center,)

				{
					Text(text = "$name :")
					Text(text = last_msg, style = MaterialTheme.typography.bodyMedium)
				}
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
						.border(BorderStroke(1.dp, Color.Black))
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
fun displayEntries(names : List<String> = List(2){"Alex Lin"},
					msg : List<String> = List(2){"This is test dialog"}) {
	assert(names.size == msg.size)
	(names.indices).forEach { i ->
		ListItem(name = names[i], last_msg = msg[i]) // Call your helper function for each entry
	}
}

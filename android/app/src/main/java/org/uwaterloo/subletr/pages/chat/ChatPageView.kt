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
import androidx.recyclerview.widget.RecyclerView
import org.uwaterloo.subletr.R

@Composable
fun ChatPageView(
	modifier: Modifier = Modifier

){
		ListItem(name = "1")


}


@Composable
fun ListItem(name : String) {
	Surface(color = Color.White,
		modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp),
		border = BorderStroke(2.dp, Color.Black)
	) {

		Icon("1")

		Column(modifier = Modifier
			.padding(24.dp)
			.fillMaxWidth()) {
			Row() {
				Column(modifier = Modifier
					.weight(1f)){
					//Text(text = "test1")
					//Text(text = "test2", style = MaterialTheme.typography.bodyMedium)
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
						.background(Color.Blue),
				)
				Text(text = "H", color = Color.Black)

			}



		}
	}
}

/*
@Composable
fun RecyclerView(names : List<String> = List(1000){"$it"}) {
	LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
		items(items = names) { names ->
			ListItem(name = name)
		}
	}
}*/
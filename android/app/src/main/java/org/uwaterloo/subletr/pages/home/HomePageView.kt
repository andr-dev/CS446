package org.uwaterloo.subletr.pages.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomePageView(
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		Text(text = "Home Page")
	}
}

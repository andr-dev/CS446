package org.uwaterloo.subletr.pages.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ChatPageView(
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		Text(text = "Chat Page View")
	}
}

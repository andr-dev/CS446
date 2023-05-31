package org.uwaterloo.subletr.pages.account

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AccountPageView(
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {
		Text(text = "AccountPageView")
	}
}

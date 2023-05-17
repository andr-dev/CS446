package org.uwaterloo.subletr.components.helloworld

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HelloWorldPageView(
	modifier: Modifier = Modifier,
	viewModel: HelloWorldViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.observeAsState(
		HelloWorldUiState()
	)

	Column(
		modifier = modifier
			.fillMaxSize(1.0f),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		if (uiState.displayStringId != null) {
			Text(
				text = stringResource(id = uiState.displayStringId!!)
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
fun HelloWorldPageViewPreview() {
	HelloWorldPageView()
}

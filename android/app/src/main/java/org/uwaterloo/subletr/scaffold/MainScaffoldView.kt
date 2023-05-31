package org.uwaterloo.subletr.scaffold

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.components.bottombar.BottomBarView
import org.uwaterloo.subletr.navigation.MainNavigation

@Composable
fun MainScaffoldView(
	modifier: Modifier = Modifier,
	viewModel: MainScaffoldViewModel = hiltViewModel(),
	mainAppState: MainAppState = rememberMainAppState(),
) {
	val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

	Scaffold(
		modifier = modifier,
		bottomBar = {
			BottomBarView(
				modifier = Modifier,
				navHostController = mainAppState.navHostController,
			)
		},
		snackbarHost = {
			SnackbarHost(
				hostState = snackbarHostState,
			)
		}
	) { paddingValues ->
		MainNavigation(
			modifier = Modifier.padding(
				paddingValues = paddingValues
			),
			navHostController = mainAppState.navHostController,
		)
	}
}

@Preview(showBackground = true)
@Composable
fun MainScaffoldViewPreview() {
	MainScaffoldView()
}

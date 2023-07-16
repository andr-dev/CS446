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
import androidx.navigation.compose.currentBackStackEntryAsState
import org.uwaterloo.subletr.api.infrastructure.ApiClient
import org.uwaterloo.subletr.components.bottombar.BottomBarView
import org.uwaterloo.subletr.navigation.MainNavigation
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.navigation.isTopLevelDestinationInHierarchy

@Composable
fun MainScaffoldView(
	modifier: Modifier = Modifier,
	viewModel: MainScaffoldViewModel = hiltViewModel(),
) {
	val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
	var currentDestination: NavigationDestination =
		if (ApiClient.accessToken == null) NavigationDestination.LOGIN
		else NavigationDestination.HOME

	NavigationDestination.values().forEach {
		if (
			viewModel
				.navHostController
				.currentBackStackEntryAsState()
				.value
				?.destination
				.isTopLevelDestinationInHierarchy(it)
		) {
			currentDestination = it
		}
	}

	Scaffold(
		modifier = modifier,
		bottomBar = {
			if (currentDestination.showBottomBar) {
				BottomBarView(
					modifier = Modifier,
					currentDestination = currentDestination,
				)
			}
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
			navHostController = viewModel.navHostController,
		)
	}
}

@Preview(showBackground = true)
@Composable
fun MainScaffoldViewPreview() {
	MainScaffoldView()
}

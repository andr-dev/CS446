package org.uwaterloo.subletr.scaffold

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import org.uwaterloo.subletr.components.bottombar.BottomBarView
import org.uwaterloo.subletr.navigation.MainNavigationView
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.navigation.isTopLevelDestinationInHierarchy
import org.uwaterloo.subletr.services.AuthenticationService
import org.uwaterloo.subletr.services.IoService
import org.uwaterloo.subletr.services.NavigationService
import org.uwaterloo.subletr.services.SnackbarService
import org.uwaterloo.subletr.theme.SubletrTypography
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun MainScaffoldView(
	modifier: Modifier = Modifier,
	viewModel: MainScaffoldViewModel = hiltViewModel(),
) {
	val snackbarHostState: SnackbarHostState = viewModel.snackbarService.snackbarHostState
	val currentBackStackEntry: NavBackStackEntry? by viewModel
		.navHostController
		.currentBackStackEntryAsState()

	LaunchedEffect(key1 = currentBackStackEntry) {
		NavigationDestination.values().forEach {
			if (
				currentBackStackEntry
					?.destination
					.isTopLevelDestinationInHierarchy(it)
			) {
				viewModel.currentDestination.value = it
			}
		}
	}

	Scaffold(
		modifier = modifier,
		bottomBar = {
			if (viewModel.currentDestination.value.showBottomBar) {
				BottomBarView(
					modifier = Modifier,
					currentDestination = viewModel.currentDestination.value,
				)
			}
		},
		snackbarHost = {
			SnackbarHost(
				hostState = snackbarHostState,
			) {
				Snackbar(
					containerColor = MaterialTheme.subletrPalette.bottomSheetColor,
					contentColor = MaterialTheme.subletrPalette.primaryTextColor,
				) {
					Text(
						text = it.visuals.message,
						style = SubletrTypography.bodyLarge,
					)
				}
			}
		}
	) { paddingValues ->
		MainNavigationView(
			modifier = Modifier.padding(
				paddingValues = paddingValues
			),
		)
	}
}

@Preview(showBackground = true)
@Composable
fun MainScaffoldViewPreview() {
	MainScaffoldView(
		viewModel = MainScaffoldViewModel(
			navigationService = NavigationService(
				context = LocalContext.current,
			),
			authenticationService = AuthenticationService(
				ioService = IoService(
					context = LocalContext.current,
				)
			),
			snackbarService = SnackbarService(),
		)
	)
}

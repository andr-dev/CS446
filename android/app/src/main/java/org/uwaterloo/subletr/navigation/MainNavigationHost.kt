package org.uwaterloo.subletr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.uwaterloo.subletr.components.homepage.HomePageView

@Composable
fun MainNavigationHost(
	modifier: Modifier = Modifier,
	navHostController: NavHostController,
) {
	NavHost(
		navController = navHostController,
		startDestination = "home",
	) {
		composable("home") {
			HomePageView(
				modifier = modifier
			)
		}
	}
}

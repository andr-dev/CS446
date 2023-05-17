package org.uwaterloo.subletr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.uwaterloo.subletr.components.helloworld.HelloWorldPageView

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
			HelloWorldPageView(
				modifier = modifier
			)
		}
	}
}

package org.uwaterloo.subletr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavigation(
	modifier: Modifier = Modifier
) {
	val navHostController = rememberNavController()
	MainNavigationHost(
		modifier = modifier,
		navHostController = navHostController,
	)
}

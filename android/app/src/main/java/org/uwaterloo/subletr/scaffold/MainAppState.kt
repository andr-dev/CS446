package org.uwaterloo.subletr.scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberMainAppState(
	navHostController: NavHostController = rememberNavController(),
): MainAppState {
	return remember(
		navHostController,
	) {
		MainAppState(
			navHostController = navHostController,
		)
	}
}

@Stable
class MainAppState (
	val navHostController: NavHostController,
)

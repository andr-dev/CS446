package org.uwaterloo.subletr.components.bottombar

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import org.uwaterloo.subletr.navigation.TopLevelDestination

@Composable
fun BottomBarView(
	modifier: Modifier = Modifier,
	navHostController: NavHostController,
	viewModel: BottomBarViewModel = hiltViewModel(),
) {
	val currentDestination = navHostController.currentBackStackEntryAsState().value?.destination

	NavigationBar(
		modifier = modifier,
	) {
		listOf(TopLevelDestination.HOME, TopLevelDestination.ACCOUNT, TopLevelDestination.CHAT).forEach {
			var selected: Boolean
			viewModel.apply {
				selected = currentDestination.isTopLevelDestinationInHierarchy(it)
			}

			NavigationBarItem(
				selected = selected,
				icon = {
					Icon(
						painter = painterResource(id = if (selected) it.selectedIconId else it.unselectedIconId),
						contentDescription = stringResource(id = it.bottomBarNavigationTextId),
					)
				},
				label = {
					Text(text = stringResource(id = it.bottomBarNavigationTextId))
				},
				onClick = {
					navHostController.navigate(
						it.name,
						navOptions {
							popUpTo(navHostController.graph.findStartDestination().id) {
								saveState = true
							}
							launchSingleTop = true
							restoreState = true
						}
					)
				},
			)
		}
	}
}

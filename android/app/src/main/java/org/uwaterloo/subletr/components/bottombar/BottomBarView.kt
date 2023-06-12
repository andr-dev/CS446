package org.uwaterloo.subletr.components.bottombar

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.theme.bottomBarItemText
import org.uwaterloo.subletr.theme.subletrPink
import org.uwaterloo.subletr.theme.unselectedGray

@Composable
fun BottomBarView(
	modifier: Modifier = Modifier,
	navHostController: NavHostController,
	currentDestination: NavigationDestination,
	@Suppress("UnusedParameter")
	viewModel: BottomBarViewModel = hiltViewModel(),
) {
	NavigationBar(
		modifier = modifier,
	) {
		NavigationDestination.values().forEach {
			if (it.bottomBarNavigationItems != null) {
				val selected: Boolean = it == currentDestination

				NavigationBarItem(
					selected = selected,
					icon = {
						Icon(
							painter = painterResource(
								id = if (selected) it.bottomBarNavigationItems.selectedIconId
								else it.bottomBarNavigationItems.unselectedIconId,
							),
							contentDescription = stringResource(
								id = it.bottomBarNavigationItems.bottomBarNavigationTextId,
							),
						)
					},
					label = {
						Text(
							text = stringResource(
								id = it.bottomBarNavigationItems.bottomBarNavigationTextId,
							),
							style = bottomBarItemText,
						)
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
					colors = NavigationBarItemDefaults.colors(
						selectedIconColor = subletrPink,
						unselectedIconColor = unselectedGray,
					),
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
@Suppress("UnusedPrivateMember")
fun BottomBarViewPreview() {
	BottomBarView(
		navHostController = rememberNavController(),
		currentDestination = NavigationDestination.ACCOUNT,
	)
}

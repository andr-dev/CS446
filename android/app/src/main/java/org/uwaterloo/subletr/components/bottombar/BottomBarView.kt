package org.uwaterloo.subletr.components.bottombar

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.navOptions
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.theme.bottomBarItemText
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun BottomBarView(
	modifier: Modifier = Modifier,
	currentDestination: NavigationDestination,
	viewModel: BottomBarViewModel = hiltViewModel(),
) {
	NavigationBar(
		modifier = modifier,
	) {
		NavigationDestination.values().forEach {
			if (it.bottomBarNavigationItems != null) {
				val selected: Boolean =
					currentDestination.rootNavPath.substringBefore("/") == it.rootNavPath

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
						viewModel.navHostController.navigate(
							it.name,
							navOptions {
								popUpTo(
									id = viewModel.navHostController.graph.findStartDestination().id
								) {
									saveState = true
								}
								launchSingleTop = true
								restoreState = true
							}
						)
					},
					colors = NavigationBarItemDefaults.colors(
						selectedIconColor = MaterialTheme.subletrPalette.subletrPink,
						unselectedIconColor = MaterialTheme.subletrPalette.unselectedGray,
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
		currentDestination = NavigationDestination.ACCOUNT,
	)
}

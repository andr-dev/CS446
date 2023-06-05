package org.uwaterloo.subletr.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

fun NavDestination?.isTopLevelDestinationInHierarchy(destination: NavigationDestination) =
	this?.hierarchy?.any {
		it.route?.contains(destination.rootNavPath, true) ?: false
	} ?: false
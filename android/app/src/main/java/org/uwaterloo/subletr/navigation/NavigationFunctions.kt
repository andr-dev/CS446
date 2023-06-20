package org.uwaterloo.subletr.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

fun NavDestination?.isTopLevelDestinationInHierarchy(destination: NavigationDestination) =
	this?.hierarchy?.any {
		it.route?.equals(destination.fullNavPath, true) ?: false
	} ?: false

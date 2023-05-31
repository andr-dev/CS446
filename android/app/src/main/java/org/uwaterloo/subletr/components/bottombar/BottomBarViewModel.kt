package org.uwaterloo.subletr.components.bottombar

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import dagger.hilt.android.lifecycle.HiltViewModel
import org.uwaterloo.subletr.navigation.TopLevelDestination
import javax.inject.Inject

@HiltViewModel
class BottomBarViewModel @Inject constructor(): ViewModel() {
	fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
		this?.hierarchy?.any {
			it.route?.contains(destination.name, true) ?: false
		} ?: false
}

package org.uwaterloo.subletr.services

import android.content.Context
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import javax.inject.Inject

class NavigationService @Inject constructor(
	context: Context,
): INavigationService {
	override val navHostController: NavHostController = NavHostController(context = context)

	init {
		listOf(ComposeNavigator(), DialogNavigator()).forEach {
			navHostController.navigatorProvider.addNavigator(it)
		}
	}

	companion object {
		const val NAV_HOST_CONTROLLER_BUNDLE_KEY = "NAV_HOST_CONTROLLER_BUNDLE_KEY"
	}
}

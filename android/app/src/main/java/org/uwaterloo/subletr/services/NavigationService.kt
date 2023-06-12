package org.uwaterloo.subletr.services

import android.util.Log
import androidx.navigation.NavHostController
import javax.inject.Inject

class NavigationService @Inject constructor(): INavigationService {
	private lateinit var navHostController: NavHostController

	override fun getNavHostController(): NavHostController {
		return navHostController
	}

	override fun setNavHostController(navHostControllerParam: NavHostController) {
		navHostController = navHostControllerParam
	}
}

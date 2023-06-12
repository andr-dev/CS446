package org.uwaterloo.subletr.services

import androidx.navigation.NavHostController

interface INavigationService {
	fun getNavHostController(): NavHostController
	fun setNavHostController(navHostControllerParam: NavHostController)
}

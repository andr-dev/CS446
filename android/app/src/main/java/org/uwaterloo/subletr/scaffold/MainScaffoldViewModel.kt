package org.uwaterloo.subletr.scaffold

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class MainScaffoldViewModel @Inject constructor(
	private val navigationService: INavigationService,
): ViewModel() {
	fun setNavHostController(navHostController: NavHostController) {
		navigationService.setNavHostController(navHostControllerParam = navHostController)
	}
}

package org.uwaterloo.subletr.navigation

import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import org.uwaterloo.subletr.api.infrastructure.ApiClient
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
	private val navigationService: INavigationService,
): SubletrViewModel<Any>() {
	val navHostController: NavHostController get() = navigationService.navHostController
	val startingDestination: NavigationDestination =
		if (ApiClient.accessToken == null)
			NavigationDestination.LOGIN
		else
			NavigationDestination.HOME
}

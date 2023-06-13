package org.uwaterloo.subletr.scaffold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class MainScaffoldViewModel @Inject constructor(
	private val navigationService: INavigationService,
	authenticationService: IAuthenticationService,
): ViewModel() {
	init {
		/*
		 * Keep synchronous to load proper first page
		 */
		viewModelScope.launch(Dispatchers.IO) {
			authenticationService.setAccessTokenFromInternalFile()
		}
	}

	fun setNavHostController(navHostController: NavHostController) {
		navigationService.setNavHostController(navHostControllerParam = navHostController)
	}
}

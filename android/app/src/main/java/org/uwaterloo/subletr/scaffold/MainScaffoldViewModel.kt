package org.uwaterloo.subletr.scaffold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.services.ISnackbarService
import javax.inject.Inject

@HiltViewModel
class MainScaffoldViewModel @Inject constructor(
	private val navigationService: INavigationService,
	authenticationService: IAuthenticationService,
	val snackbarService: ISnackbarService,
): ViewModel() {
	val navHostController: NavHostController get() = navigationService.navHostController

	init {
		/*
		 * Keep synchronous to load proper first page
		 */
		viewModelScope.launch(Dispatchers.IO) {
			authenticationService.setAccessTokenFromInternalFile()
		}
	}
}

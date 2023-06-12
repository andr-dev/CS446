package org.uwaterloo.subletr.components.bottombar

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class BottomBarViewModel @Inject constructor(
	private val navigationService: INavigationService,
): ViewModel() {
	val navHostController: NavHostController get() = navigationService.getNavHostController()
}

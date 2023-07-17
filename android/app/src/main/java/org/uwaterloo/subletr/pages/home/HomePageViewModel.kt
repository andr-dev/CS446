package org.uwaterloo.subletr.pages.home

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.pages.home.list.HomeListChildViewModel
import org.uwaterloo.subletr.pages.home.map.HomeMapChildViewModel
import org.uwaterloo.subletr.services.ILocationService
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
	private val navigationService: INavigationService,
	val homeListChildViewModel: HomeListChildViewModel,
	val homeMapChildViewModel: HomeMapChildViewModel,
	private val locationService: ILocationService,
) : SubletrViewModel<HomePageUiState>(
	homeListChildViewModel,
	homeMapChildViewModel,
) {
	val navHostController: NavHostController get() = navigationService.navHostController

	override val uiStateStream: Observable<HomePageUiState> = homeListChildViewModel.uiStateStream.map {
		it
	}

	init {
		viewModelScope.launch {
			val loc = locationService.getLocation()
			println(loc.toString())
		}
	}
}

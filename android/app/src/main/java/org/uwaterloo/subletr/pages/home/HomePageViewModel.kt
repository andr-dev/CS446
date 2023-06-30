package org.uwaterloo.subletr.pages.home

import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.pages.home.list.HomeListChildViewModel
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
	private val navigationService: INavigationService,
	val homeListViewModel: HomeListChildViewModel,
) : SubletrViewModel<HomePageUiState>(homeListViewModel) {
	val navHostController: NavHostController get() = navigationService.getNavHostController()

	override val uiStateStream: Observable<HomePageUiState> = homeListViewModel.uiStateStream.map {
		it
	}
}

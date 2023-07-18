package org.uwaterloo.subletr.pages.home.list

import androidx.navigation.NavHostController
import io.reactivex.rxjava3.subjects.PublishSubject
import org.uwaterloo.subletr.infrastructure.SubletrChildViewModel
import org.uwaterloo.subletr.pages.home.HomePageViewModel
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

class HomeListChildViewModel @Inject constructor(
	private val navigationService: INavigationService,
) : SubletrChildViewModel<HomeListUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController

	val getListingParamsStream: PublishSubject<HomePageViewModel.GetListingParams> =
		PublishSubject.create()

	override val uiStateStream: PublishSubject<HomeListUiState> = PublishSubject.create()
}

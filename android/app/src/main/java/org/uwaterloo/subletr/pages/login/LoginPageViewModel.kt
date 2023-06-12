package org.uwaterloo.subletr.pages.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.apis.DefaultApi
import org.uwaterloo.subletr.api.models.UserLoginRequest
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.INavigationService
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class LoginPageViewModel @Inject constructor(
	val api: DefaultApi,
	val navigationService: INavigationService,
) : ViewModel() {
	val navHostController get() = navigationService.getNavHostController()

	val emailStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val passwordStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val infoTextStringIdStream: BehaviorSubject<Optional<Int>> = BehaviorSubject.createDefault(Optional.empty())

	val uiStateStream: Observable<LoginPageUiState> = Observable.combineLatest(
		emailStream,
		passwordStream,
		infoTextStringIdStream,
	) {
		email, password, infoTextStringId ->
		LoginPageUiState.Loaded(
			email = email,
			password = password,
			infoTextStringId = infoTextStringId.getOrNull(),
		)
	}

	val loginStream: PublishSubject<LoginPageUiState.Loaded> = PublishSubject.create()

	init {
		loginStream.map {
			runBlocking {
				api.login(
					UserLoginRequest(
						email = it.email,
						password = it.password,
					)
				)
			}
		}
			.map {
				navHostController.navigate(NavigationDestination.HOME.rootNavPath)
			}
			.doOnError {
				infoTextStringIdStream.onNext(
					Optional.of(R.string.invalid_login_credentials_try_again)
				)
			}
			.subscribeOn(Schedulers.io())
			.onErrorResumeWith(Observable.never())
			.subscribe()
	}
}

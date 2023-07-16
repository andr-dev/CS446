package org.uwaterloo.subletr.pages.login

import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.apis.AuthenticationApi
import org.uwaterloo.subletr.api.models.UserLoginRequest
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.INavigationService
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class LoginPageViewModel @Inject constructor(
	private val authenticationApi: AuthenticationApi,
	private val navigationService: INavigationService,
	private val authenticationService: IAuthenticationService,
) : SubletrViewModel<LoginPageUiState>() {
	val navHostController: NavHostController get() = navigationService.navHostController

	val emailStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val passwordStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	private val infoTextStringIdStream: BehaviorSubject<Optional<Int>> = BehaviorSubject.createDefault(Optional.empty())

	override val uiStateStream: Observable<LoginPageUiState> = Observable.combineLatest(
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
			runCatching {
				runBlocking {
					authenticationApi.authLogin(
						UserLoginRequest(
							email = it.email,
							password = it.password,
						)
					)
				}
			}
				.onSuccess {
					authenticationService.setAccessToken(it.token)
					navHostController.navigate(NavigationDestination.HOME.fullNavPath)
				}
				.onFailure {
					infoTextStringIdStream.onNext(
						Optional.of(R.string.invalid_login_credentials_try_again)
					)
					authenticationService.deleteAccessToken()
				}
		}
			.onErrorResumeWith(Observable.never())
			.subscribeOn(Schedulers.io())
			.safeSubscribe()
	}
}

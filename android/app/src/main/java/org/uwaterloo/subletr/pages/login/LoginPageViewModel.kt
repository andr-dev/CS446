package org.uwaterloo.subletr.pages.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.apis.DefaultApi
import org.uwaterloo.subletr.api.models.UserLoginRequest
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.IAuthenticationService
import org.uwaterloo.subletr.services.INavigationService
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class LoginPageViewModel @Inject constructor(
	private val api: DefaultApi,
	private val navigationService: INavigationService,
	private val authenticationService: IAuthenticationService,
) : ViewModel() {
	private val disposables: MutableList<Disposable> = mutableListOf()

	val navHostController get() = navigationService.getNavHostController()

	val emailStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val passwordStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	private val infoTextStringIdStream: BehaviorSubject<Optional<Int>> = BehaviorSubject.createDefault(Optional.empty())

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
		disposables.add(
			loginStream.map {
				runCatching {
					runBlocking {
						api.authLogin(
							UserLoginRequest(
								email = it.email,
								password = it.password,
							)
						)
					}
				}
					.onSuccess {
						authenticationService.setAccessToken(it.token)
						navHostController.navigate(NavigationDestination.HOME.rootNavPath)
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
				.subscribe()
		)
	}

	override fun onCleared() {
		super.onCleared()
		disposables.forEach {
			it.dispose()
		}
	}
}

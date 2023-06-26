package org.uwaterloo.subletr.pages.createaccount

import android.util.Patterns
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
import org.uwaterloo.subletr.api.models.CreateUserRequest
import org.uwaterloo.subletr.enums.Gender
import org.uwaterloo.subletr.navigation.NavigationDestination
import org.uwaterloo.subletr.services.INavigationService
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull


@HiltViewModel
class CreateAccountPageViewModel @Inject constructor(
	navigationService: INavigationService,
	defaultApi: DefaultApi,
) : ViewModel() {
	private val disposables: MutableList<Disposable> = mutableListOf()
	val navHostController = navigationService.getNavHostController()


	val firstNameStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val lastNameStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val emailStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val passwordStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val confirmPasswordStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val genderStream: BehaviorSubject<Gender> = BehaviorSubject.createDefault(Gender.OTHER)

	private val firstNameInfoTextStringIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())
	private val lastNameInfoTextStringIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())
	private val emailInfoTextStringIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())
	private val passwordInfoTextStringIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())
	private val confirmPasswordInfoTextStringIdStream: BehaviorSubject<Optional<Int>> =
		BehaviorSubject.createDefault(Optional.empty())

	private val observables: List<Observable<*>> = listOf(
		firstNameStream,
		lastNameStream,
		emailStream,
		passwordStream,
		confirmPasswordStream,
		genderStream,
		firstNameInfoTextStringIdStream,
		lastNameInfoTextStringIdStream,
		emailInfoTextStringIdStream,
		passwordInfoTextStringIdStream,
		confirmPasswordInfoTextStringIdStream
	)

	val uiStateStream: Observable<CreateAccountPageUiState> = Observable.combineLatest(
		observables
	) {
		observing ->
		@Suppress("UNCHECKED_CAST")
		CreateAccountPageUiState.Loaded(
			firstName = observing[0] as String,
			lastName = observing[1] as String,
			email = observing[2] as String,
			password = observing[3] as String,
			confirmPassword = observing[4] as String,
			gender = observing[5] as Gender,
			firstNameInfoTextStringId = (observing[6] as Optional<Int>).getOrNull(),
			lastNameInfoTextStringId = (observing[7] as Optional<Int>).getOrNull(),
			emailInfoTextStringId = (observing[8] as Optional<Int>).getOrNull(),
			passwordInfoTextStringId = (observing[9] as Optional<Int>).getOrNull(),
			confirmPasswordInfoTextStringId = (observing[10] as Optional<Int>).getOrNull(),
		)
	}

	val createAccountStream: PublishSubject<CreateAccountPageUiState.Loaded> = PublishSubject.create()

	init {
		disposables.add(
			createAccountStream.map { uiState ->
				var validInput = true

				if (uiState.firstName.isBlank()) {
					firstNameInfoTextStringIdStream.onNext(Optional.of(R.string.first_name_blank_error))
					validInput = false
				} else {
					firstNameInfoTextStringIdStream.onNext(Optional.empty())
				}
				if (uiState.lastName.isBlank()) {
					lastNameInfoTextStringIdStream.onNext(Optional.of(R.string.last_name_blank_error))
					validInput = false
				} else {
					lastNameInfoTextStringIdStream.onNext(Optional.empty())
				}
				if (uiState.email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
					emailInfoTextStringIdStream.onNext(Optional.of(R.string.email_format_error))
					validInput = false
				} else {
					emailInfoTextStringIdStream.onNext(Optional.empty())
				}
				if (uiState.password.isBlank()) {
					passwordInfoTextStringIdStream.onNext(Optional.of(R.string.password_blank_error))
					validInput = false
				} else {
					passwordInfoTextStringIdStream.onNext(Optional.empty())
				}
				if (uiState.password != uiState.confirmPassword) {
					confirmPasswordInfoTextStringIdStream.onNext(Optional.of(R.string.password_not_match_error))
					validInput = false
				} else {
					confirmPasswordInfoTextStringIdStream.onNext(Optional.empty())
				}

				if (validInput) {
					runCatching {
						runBlocking {
							defaultApi.userCreate(
								CreateUserRequest(
									firstName = uiState.firstName,
									lastName = uiState.lastName,
									email = uiState.email,
									gender = uiState.gender.name,
									password = uiState.password,
								)
							)
						}
					}
						.onSuccess {
							navHostController.navigate(
								route = "${NavigationDestination.VERIFY_EMAIL.rootNavPath}/${it.userId}"
							)
						}
						.onFailure {
							// TODO: Do something
						}
				}
			}
				.subscribeOn(Schedulers.io())
				.onErrorResumeWith(Observable.never())
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

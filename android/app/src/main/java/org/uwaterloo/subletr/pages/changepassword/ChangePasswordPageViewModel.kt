package org.uwaterloo.subletr.pages.changepassword

import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.runBlocking
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.api.apis.UserApi
import org.uwaterloo.subletr.api.models.ChangePasswordUserRequest
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.services.INavigationService
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class ChangePasswordPageViewModel @Inject constructor(
	val navigationService: INavigationService,
	val userApi: UserApi,
): SubletrViewModel<ChangePasswordPageUiState>() {
	val navHostController get() = navigationService.navHostController

	val oldPasswordStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val newPasswordStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val confirmNewPasswordStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	private val infoTextStringIdStream: BehaviorSubject<Optional<Int>> = BehaviorSubject.createDefault(
		Optional.empty()
	)

	override val uiStateStream: Observable<ChangePasswordPageUiState> = Observable.combineLatest(
		oldPasswordStream,
		newPasswordStream,
		confirmNewPasswordStream,
		infoTextStringIdStream,
	) {
		oldPassword, newPassword, confirmNewPassword, infoTextStringId ->
		ChangePasswordPageUiState.Loaded(
			oldPassword = oldPassword,
			newPassword = newPassword,
			confirmNewPassword = confirmNewPassword,
			infoTextStringId = infoTextStringId.getOrNull(),
		)
	}

	val changePasswordStream: PublishSubject<ChangePasswordPageUiState.Loaded> = PublishSubject.create()

	init {
		changePasswordStream.map {
			var error = false
			if (it.oldPassword == it.newPassword) {
				infoTextStringIdStream.onNext(Optional.of(R.string.same_old_new_password_error))
				error = true
			}
			if (it.newPassword != it.confirmNewPassword) {
				infoTextStringIdStream.onNext(Optional.of(R.string.password_not_match_error))
				error = true
			}

			if (!error) {
				runCatching {
					runBlocking {
						userApi.userChangePassword(
							ChangePasswordUserRequest(
								passwordOld = it.oldPassword,
								passwordNew = it.newPassword,
							)
						)
					}
				}
					.onSuccess {
						infoTextStringIdStream.onNext(
							Optional.of(R.string.password_update_success)
						)
					}
					.onFailure {
						infoTextStringIdStream.onNext(
							Optional.of(R.string.password_update_error)
						)
					}
			}
		}
			.subscribeOn(Schedulers.io())
			.onErrorResumeWith(Observable.never())
			.safeSubscribe()
	}
}

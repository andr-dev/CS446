package org.uwaterloo.subletr.pages.changepassword

import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.infrastructure.SubletrViewModel
import org.uwaterloo.subletr.services.INavigationService
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class ChangePasswordPageViewModel @Inject constructor(
	val navigationService: INavigationService
): SubletrViewModel<ChangePasswordPageUiState>() {
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
			if (it.oldPassword == it.newPassword) {
				infoTextStringIdStream.onNext(Optional.of(R.string.same_old_new_password_error))
			}
			if (it.newPassword != it.confirmNewPassword) {
				infoTextStringIdStream.onNext(Optional.of(R.string.password_not_match_error))
			}
		}
			.subscribeOn(Schedulers.io())
			.onErrorResumeWith(Observable.never())
			.safeSubscribe()
	}
}

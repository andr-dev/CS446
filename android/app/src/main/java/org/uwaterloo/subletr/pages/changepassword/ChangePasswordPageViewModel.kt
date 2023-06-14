package org.uwaterloo.subletr.pages.changepassword

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.services.INavigationService
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class ChangePasswordPageViewModel @Inject constructor(
	val navigationService: INavigationService
): ViewModel() {
	private val disposables: MutableList<Disposable> = mutableListOf()

	val oldPasswordStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val newPasswordStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	val confirmNewPasswordStream: BehaviorSubject<String> = BehaviorSubject.createDefault("")
	private val infoTextStringIdStream: BehaviorSubject<Optional<Int>> = BehaviorSubject.createDefault(
		Optional.empty()
	)

	val uiStateStream: Observable<ChangePasswordPageUiState> = Observable.combineLatest(
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
		disposables.add(
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

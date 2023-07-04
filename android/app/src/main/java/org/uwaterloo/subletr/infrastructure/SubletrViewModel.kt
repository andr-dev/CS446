package org.uwaterloo.subletr.infrastructure

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

open class SubletrViewModel<UiState : Any>(
	vararg childViewModelsParam: SubletrChildViewModel<*>,
) : ViewModel() {
	private val disposables: MutableList<Disposable> = mutableListOf()
	private val childViewModels: MutableList<SubletrChildViewModel<*>> = mutableListOf()
	open val uiStateStream: Observable<UiState> = Observable.never()

	init {
		childViewModels.addAll(childViewModelsParam)
	}

	fun <T : Any> Observable<T>.safeSubscribe() {
		disposables.add(
			this.subscribe()
		)
	}

	override fun onCleared() {
		super.onCleared()

		childViewModels.forEach {
			it.onCleared()
		}

		disposables.forEach {
			it.dispose()
		}
	}
}

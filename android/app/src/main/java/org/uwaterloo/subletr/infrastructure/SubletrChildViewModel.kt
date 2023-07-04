package org.uwaterloo.subletr.infrastructure

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

open class SubletrChildViewModel<UiState : Any>(
	vararg childViewModelsParam: SubletrChildViewModel<*>,
) {
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

	fun onCleared() {
		childViewModels.forEach {
			it.onCleared()
		}

		disposables.forEach {
			it.dispose()
		}
	}
}

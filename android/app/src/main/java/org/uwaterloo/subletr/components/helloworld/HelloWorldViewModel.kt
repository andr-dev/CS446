package org.uwaterloo.subletr.components.helloworld

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.uwaterloo.subletr.R
import javax.inject.Inject

@HiltViewModel
class HelloWorldViewModel @Inject constructor(): ViewModel() {
	val uiState: MutableLiveData<HelloWorldUiState> by lazy {
		MutableLiveData<HelloWorldUiState>(
			HelloWorldUiState(
				displayStringId = R.string.hello_world,
			)
		)
	}
}

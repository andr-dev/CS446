package org.uwaterloo.subletr.pages.chat

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.uwaterloo.subletr.api.apis.DefaultApi
import org.uwaterloo.subletr.services.INavigationService
import javax.inject.Inject

@HiltViewModel
class ChatPageViewModel @Inject constructor(
	navigationService: INavigationService,
	defaultApi: DefaultApi,
) : ViewModel() {

}
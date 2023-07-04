package org.uwaterloo.subletr.pages.home.map

import android.util.Log
import kotlinx.coroutines.coroutineScope
import org.uwaterloo.subletr.infrastructure.SubletrChildViewModel
import org.uwaterloo.subletr.services.ILocationService
import javax.inject.Inject

class HomeMapChildViewModel @Inject constructor(
	private val locationService: ILocationService,
): SubletrChildViewModel<HomeMapUiState>() {
	suspend fun getLocation() {
		coroutineScope {
			Log.d("LocationLogs", locationService.getLocation().toString())
		}
	}
}

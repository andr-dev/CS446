package org.uwaterloo.subletr.pages.home.map

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import org.uwaterloo.subletr.services.LocationService
import org.uwaterloo.subletr.services.LocationService.Companion.locationPermissions
import org.uwaterloo.subletr.theme.SubletrTheme

@Composable
fun HomeMapChildView(
	modifier: Modifier = Modifier,
	viewModel: HomeMapChildViewModel,
) {
	val coroutineScope = rememberCoroutineScope()

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestMultiplePermissions(),
	) { permissions ->
		when {
			permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
				coroutineScope.launch {
					viewModel.getLocation()
				}
			}
			permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
				coroutineScope.launch {
					viewModel.getLocation()
				}
			} else -> {
				Log.d("LocationLogs", "No Location Permissions")
			}
		}
	}

	LazyColumn(
		modifier = modifier,
		content = {
			// TODO: Remove in future, for testing
			item {
				Button(
					onClick = {
						launcher.launch(locationPermissions)
					}
				) {
					Text(text = "Location in Logs")
				}
			}
		}
	)


}

@Preview
@Composable
fun HomeMapChildViewPreview() {
	SubletrTheme {
		HomeMapChildView(
			viewModel = HomeMapChildViewModel(
				locationService = LocationService(context = LocalContext.current)
			)
		)
	}
}

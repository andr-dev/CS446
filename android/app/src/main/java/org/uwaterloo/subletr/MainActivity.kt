package org.uwaterloo.subletr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.uwaterloo.subletr.scaffold.MainScaffoldView
import org.uwaterloo.subletr.services.ISettingsService
import org.uwaterloo.subletr.theme.SubletrTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	@Inject
	lateinit var settingsService: ISettingsService

	private val job: CompletableJob = SupervisorJob()
	private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO + job)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		WindowCompat.setDecorFitsSystemWindows(window, false)
		Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)

		settingsService.subscribe(coroutineScope = ioScope)

		setContent {
			SubletrTheme(
				darkTheme = if (settingsService.useDeviceTheme.value) {
					isSystemInDarkTheme()
				} else settingsService.useDarkTheme.value
			) {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					MainScaffoldView()
				}
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		job.cancel()
	}
}

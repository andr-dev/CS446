package org.uwaterloo.subletr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.AndroidEntryPoint
import org.uwaterloo.subletr.scaffold.MainScaffoldView
import org.uwaterloo.subletr.services.INavigationService
import org.uwaterloo.subletr.theme.SubletrTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	@Inject
	lateinit var navigationService: INavigationService

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		WindowCompat.setDecorFitsSystemWindows(window, false)
		Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)

		setContent {
			SubletrTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					MainScaffoldView()
				}
			}
		}
	}
}

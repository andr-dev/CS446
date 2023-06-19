package org.uwaterloo.subletr.pages.createlisting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.SubletrTypography

@Composable
fun CreateListingPageView(
	modifier: Modifier = Modifier,
	viewModel: CreateListingPageViewModel = hiltViewModel(),
	uiState: CreateListingPageUiState = viewModel.uiStateStream.subscribeAsState(
		CreateListingPageUiState.Loading
	).value,
) {
	val scrollState = rememberScrollState()
	if (uiState is CreateListingPageUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			CircularProgressIndicator()
		}
	} else if (uiState is CreateListingPageUiState.Loaded) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f)
				.imePadding()
				.verticalScroll(scrollState),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Spacer(
					modifier = Modifier.width(dimensionResource(id = R.dimen.xl))
				)

				Column(
					modifier = modifier,
				) {
					Text(
						text = stringResource(id = R.string.create_new_listing),
						style = SubletrTypography.titleMedium,
					)
				}
			}
		}
	}
}
package org.uwaterloo.subletr.pages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.pages.chat.components.ChatListItem
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun ChatListingPageView(
	modifier: Modifier = Modifier,
	viewModel: ChatListingPageViewModel = hiltViewModel(),
	uiState: ChatListingPageUiState = viewModel.uiStateStream.subscribeAsState(
		ChatListingPageUiState.Loading
	).value
){
	if (uiState is ChatListingPageUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(1.0f)
				.imePadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			CircularProgressIndicator()
		}
	} else if (uiState is ChatListingPageUiState.Loaded) {
		Scaffold(
			modifier = modifier,
			topBar = {
				Row(
					modifier = Modifier
						.fillMaxWidth(1.0f)
						.padding(
							start = dimensionResource(id = R.dimen.s),
						),
					verticalAlignment = Alignment.CenterVertically,
				) {
					Text(
						text = stringResource(id = R.string.messages),
						style = MaterialTheme.typography.titleMedium,
					)
				}
			},
			content = { padding: PaddingValues ->
				if (uiState.contacts.isEmpty()) {
					Column(
						modifier = modifier
							.fillMaxSize()
							.padding(padding)
							.background(Color.Transparent),
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.Center,
					) {
						Text(
							stringResource(id = R.string.empty_chat),
							color = MaterialTheme.subletrPalette.primaryTextColor,
						)
					}
				} else {
					LazyColumn(modifier = modifier.padding(padding)) {
						items(items = uiState.contacts) { contact: Contact ->
							ChatListItem(contact = contact)
						}
					}
				}
			}
		)
	}
}



@Preview(showBackground = true)
@Composable
fun ChatListLoadingPreview() {
	ChatListingPageView()
}

@Preview(showBackground = true)
@Composable
fun ChatListLoadedPreview() {
	SubletrTheme {
		ChatListingPageView(
			modifier = Modifier,
			viewModel = ChatListingPageViewModel(),
			uiState = ChatListingPageUiState.Loaded(contacts = emptyList())
		)
	}
}

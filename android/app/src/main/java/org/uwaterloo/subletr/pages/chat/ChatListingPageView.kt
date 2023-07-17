package org.uwaterloo.subletr.pages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import org.uwaterloo.subletr.theme.textOnSubletrPink
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
import org.uwaterloo.subletr.pages.account.AccountPageUiState
import org.uwaterloo.subletr.pages.home.ViewSwitch
import org.uwaterloo.subletr.pages.home.list.HomeListUiState
import org.uwaterloo.subletr.services.NavigationService
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.primaryTextColor
import org.uwaterloo.subletr.theme.secondaryButtonBackgroundColor
import org.uwaterloo.subletr.theme.subletrPink

@Composable
fun ChatListingPageView(
	modifier: Modifier = Modifier,
	viewModel : ChatListingPageViewModel = hiltViewModel(),
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
							color = primaryTextColor,
						)
					}
				} else {
					Column(modifier = modifier.padding(padding)) {
						DisplayEntries(uiState.contacts)
					}
				}
			})
	}
}

@Composable
fun ListItem(name : String, lastMsg : String) {
	Box(
		modifier = Modifier
			.padding(
				vertical = dimensionResource(id = R.dimen.xs),
				horizontal = dimensionResource(id = R.dimen.s),
			)
			.wrapContentHeight()
			.fillMaxWidth(1.0f)
			.clip(
				RoundedCornerShape(
					dimensionResource(id = R.dimen.s)
				)
			)
			.background(secondaryButtonBackgroundColor),
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(name = name)
				Column(
					modifier = Modifier
						.weight(1f)
						.padding(vertical = dimensionResource(id = R.dimen.s)),
					verticalArrangement = Arrangement.Center,
				)

				{
					Text(text = name,
						style = MaterialTheme.typography.titleSmall)
					Text(text = lastMsg,
						style = MaterialTheme.typography.bodySmall)
				}
				Notification()
			}

	}
}

@Composable
fun Icon(name : String) {
	Box(
		modifier = Modifier
			.padding(dimensionResource(id = R.dimen.s))
			.clip(CircleShape),
	) {
		Box(
			modifier = Modifier
				.width(dimensionResource(id = R.dimen.xxl))
				.height(dimensionResource(id = R.dimen.xxl))
				.background(subletrPink),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = name.first().toString(),
				style = MaterialTheme.typography.titleSmall,
				color = textOnSubletrPink,
			)
		}
	}
}

@Composable
fun Notification(n : Int = 1) {
	Box(
		modifier = Modifier
			.padding(dimensionResource(id = R.dimen.s))
			.clip(CircleShape),
	) {
		Box(
			modifier = Modifier
				.width(dimensionResource(id = R.dimen.l))
				.height(dimensionResource(id = R.dimen.l))
				.background(Color(0xFF6ABC95)),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = n.toString(),
				color = textOnSubletrPink,
				style = MaterialTheme.typography.labelLarge,
			)
		}
	}
}

@Composable
fun DisplayEntries(contacts : List<Contact>) {
	(contacts.indices).forEach { i ->
		ListItem(name = contacts[i].name, lastMsg = contacts[i].msg.last())
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
	val contact : Contact = Contact("Alex Lin", List(1){"test dialog"}, true)
	val contacts = List(2){contact }
	SubletrTheme {
		ChatListingPageView(
			modifier = Modifier,
			viewModel = ChatListingPageViewModel(NavigationService()),
			uiState = ChatListingPageUiState.Loaded(contacts = contacts)
		)
	}
}

package org.uwaterloo.subletr.pages.individualchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.textfield.RoundedTextField
import org.uwaterloo.subletr.models.ChatItemModel
import org.uwaterloo.subletr.pages.individualchat.components.IndividualChatHeader
import org.uwaterloo.subletr.pages.individualchat.components.MyChatBubble
import org.uwaterloo.subletr.pages.individualchat.components.OtherChatBubble
import org.uwaterloo.subletr.services.NavigationService
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.primaryBackgroundColor
import org.uwaterloo.subletr.theme.secondaryBackgroundColor
import org.uwaterloo.subletr.theme.sendButtonColor

@Composable
fun IndividualChatPageView(
	modifier: Modifier = Modifier,
	viewModel: IndividualChatPageViewModel = hiltViewModel(),
	uiState: IndividualChatPageUiState = viewModel
		.uiStateStream.subscribeAsState(initial = IndividualChatPageUiState.Loading).value,
) {
	if (uiState is IndividualChatPageUiState.Loading) {
		Column(
			modifier = modifier
				.fillMaxSize(fraction = 1.0f),
		) {
			CircularProgressIndicator()
		}
	}
	else if (uiState is IndividualChatPageUiState.Loaded) {
		Column(
			modifier = modifier
				.fillMaxSize(fraction = 1.0f),
		) {
			IndividualChatHeader(
				modifier = Modifier,
				basicInfo = uiState.basicInfo,
				navHostController = viewModel.navHostController,
			)
			LazyColumn(
				modifier = Modifier
					.weight(weight = 1.0f),
				verticalArrangement = Arrangement.Bottom,
			) {
				items(items = uiState.chatItems) { chatItem: ChatItemModel ->
					if (chatItem is ChatItemModel.MyChatItem) {
						MyChatBubble(myChatItem = chatItem)
					}
					else if (chatItem is ChatItemModel.OtherChatItem) {
						OtherChatBubble(otherChatItem = chatItem)
					}
				}
			}
			Box(
				modifier = Modifier
					.wrapContentSize()
					.clip(
						RoundedCornerShape(
							topStart = dimensionResource(id = R.dimen.s),
							topEnd = dimensionResource(id = R.dimen.s),
							bottomEnd = dimensionResource(id = R.dimen.zero),
							bottomStart = dimensionResource(id = R.dimen.zero),
						)
					),
			) {
				Row(
					modifier = Modifier
						.background(secondaryBackgroundColor)
						.fillMaxWidth(fraction = 1.0f),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center,
				) {
					RoundedTextField(
						modifier = Modifier.padding(dimensionResource(id = R.dimen.m)),
						value = uiState.message,
						onValueChange = {
							viewModel.messageStream.onNext(it)
						},
						placeholder = {
							Text(text = stringResource(id = R.string.message_ellipses))
						},
						colors = TextFieldDefaults.colors(
							unfocusedContainerColor = primaryBackgroundColor,
							focusedContainerColor = primaryBackgroundColor,
							unfocusedIndicatorColor = Color.Transparent,
							focusedIndicatorColor = Color.Transparent,
						),
					)

					IconButton(
						modifier = Modifier,
						onClick = {},
					) {
						Icon(
							painter = painterResource(
								id = R.drawable.send_solid_black_24,
							),
							contentDescription = stringResource(id = R.string.back_arrow),
							tint = sendButtonColor,
						)
					}
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun IndividualChatPageViewLoadingPreview() {
	SubletrTheme {
		IndividualChatPageView(
			modifier = Modifier,
			viewModel = IndividualChatPageViewModel(
				navigationService = NavigationService(
					context = LocalContext.current,
				),
			),
			uiState = IndividualChatPageUiState.Loading,
		)
	}
}

@Preview(showBackground = true)
@Composable
fun IndividualChatPageViewLoadedPreview() {
	SubletrTheme {
		IndividualChatPageView(
			modifier = Modifier,
			viewModel = IndividualChatPageViewModel(
				navigationService = NavigationService(
					context = LocalContext.current,
				),
			),
			uiState = IndividualChatPageUiState.Loaded(
				basicInfo = IndividualChatPageUiState.BasicInfo(
					contactName = "Abhed Shwarma",
					address = "123 University Ave.",
				),
				chatItems = listOf(
					ChatItemModel.MyChatItem("my test message"),
					ChatItemModel.OtherChatItem("other test message")
				),
				message = "",
			),
		)
	}
}

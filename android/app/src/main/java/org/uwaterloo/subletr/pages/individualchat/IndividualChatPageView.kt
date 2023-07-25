package org.uwaterloo.subletr.pages.individualchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.subletrPalette

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
						.background(color = MaterialTheme.subletrPalette.secondaryBackgroundColor)
						.fillMaxWidth(fraction = 1.0f),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center,
				) {
					RoundedTextField(
						modifier = Modifier
							.padding(
								vertical = dimensionResource(id = R.dimen.m),
							)
							.height(dimensionResource(id = R.dimen.xxl)),
						value = uiState.message,
						onValueChange = {
							viewModel.messageStream.onNext(it)
						},
						placeholder = {
							Text(text = stringResource(id = R.string.message_ellipses))
						},
						colors = TextFieldDefaults.colors(
							unfocusedContainerColor = MaterialTheme.subletrPalette.primaryBackgroundColor,
							focusedContainerColor = MaterialTheme.subletrPalette.primaryBackgroundColor,
							unfocusedIndicatorColor = Color.Transparent,
							focusedIndicatorColor = Color.Transparent,
						),
						shape = RoundedCornerShape(
							topStart = dimensionResource(id = R.dimen.xxl),
							topEnd = dimensionResource(id = R.dimen.zero),
							bottomEnd = dimensionResource(id = R.dimen.zero),
							bottomStart = dimensionResource(id = R.dimen.xxl),
						),
					)

					Box(
						modifier = Modifier
							.clip(
								RoundedCornerShape(
									topStart = dimensionResource(id = R.dimen.zero),
									topEnd = dimensionResource(id = R.dimen.xxl),
									bottomEnd = dimensionResource(id = R.dimen.xxl),
									bottomStart = dimensionResource(id = R.dimen.zero),
								),
							)
							.height(dimensionResource(id = R.dimen.xxl)),
						contentAlignment = Alignment.Center,
					) {
						IconButton(
							modifier = Modifier
								.background(color = MaterialTheme.subletrPalette.primaryBackgroundColor)
								.height(dimensionResource(id = R.dimen.xxl))
								.aspectRatio(ratio = 1.0f),
							onClick = {
								viewModel.sendMessage(
									uiState = uiState,
								)
							},
						) {
							Icon(
								painter = painterResource(
									id = R.drawable.send_solid_black_24,
								),
								contentDescription = stringResource(id = R.string.send_message),
								tint = MaterialTheme.subletrPalette.subletrPink,
							)
						}
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
			uiState = IndividualChatPageUiState.Loading,
		)
	}
}

package org.uwaterloo.subletr.pages.individualchat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.models.ChatItemModel
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.myChatBubbleFont
import org.uwaterloo.subletr.theme.subletrPalette

@Composable
fun MyChatBubble(modifier: Modifier = Modifier, myChatItem: ChatItemModel.MyChatItem) {
	Row(
		modifier = modifier
			.fillMaxWidth(fraction = 1.0f)
			.padding(dimensionResource(id = R.dimen.xs)),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.End,
	) {
		Spacer(
			modifier = Modifier
				.weight(
					weight = 0.2f,
					fill = true,
				),
		)
		Box(
			modifier = Modifier
				.weight(
					weight = 0.8f,
					fill = false,
				)
				.wrapContentHeight()
				.clip(
					RoundedCornerShape(
						topStart = dimensionResource(id = R.dimen.s),
						topEnd = dimensionResource(id = R.dimen.s),
						bottomEnd = dimensionResource(id = R.dimen.zero),
						bottomStart = dimensionResource(id = R.dimen.s),
					)
				)
		) {
			Box(
				modifier = Modifier
					.background(color = MaterialTheme.subletrPalette.subletrPink),
			) {
				Text(
					modifier = Modifier
						.padding(dimensionResource(id = R.dimen.s)),
					text = myChatItem.message,
					style = myChatBubbleFont,
					textAlign = TextAlign.Start,
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun MyChatBubblePreview() {
	SubletrTheme {
		MyChatBubble(
			myChatItem = ChatItemModel.MyChatItem(
				message = "my test message",
			),
		)
	}
}

@Preview(showBackground = true)
@Composable
fun MyLongChatBubblePreview() {
	SubletrTheme {
		MyChatBubble(
			myChatItem = ChatItemModel.MyChatItem(
				message = "my test message that is extra long because of testing of stuff like that",
			),
		)
	}
}

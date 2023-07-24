package org.uwaterloo.subletr.pages.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.pages.chat.Contact
import org.uwaterloo.subletr.theme.SubletrTheme
import org.uwaterloo.subletr.theme.subletrPalette


@Composable
fun ChatListItem(contact: Contact) {
	val name = contact.name
	val lastMsg = contact.msg.last()
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
			.background(MaterialTheme.subletrPalette.secondaryButtonBackgroundColor),
	) {
		Row(verticalAlignment = Alignment.CenterVertically) {
			Icon(name = name)
			Column(
				modifier = Modifier
					.weight(1f)
					.padding(vertical = dimensionResource(id = R.dimen.s)),
				verticalArrangement = Arrangement.Center,
			) {
				Text(text = name,
					style = MaterialTheme.typography.titleSmall)
				Text(text = lastMsg,
					style = MaterialTheme.typography.bodySmall)
			}
			if (contact.unread > 0) {
				Notification(contact.unread)
			}

		}
	}
}

@Composable
fun Icon(name: String) {
	Box(
		modifier = Modifier
			.padding(dimensionResource(id = R.dimen.s))
			.clip(CircleShape),
	) {
		Box(
			modifier = Modifier
				.width(dimensionResource(id = R.dimen.xxl))
				.height(dimensionResource(id = R.dimen.xxl))
				.background(MaterialTheme.subletrPalette.subletrPink),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = name.first().toString(),
				style = MaterialTheme.typography.titleSmall,
				color = MaterialTheme.subletrPalette.textOnSubletrPink,
			)
		}
	}
}

@Composable
fun Notification(n: Int = 1) {
	Box(
		modifier = Modifier
			.padding(dimensionResource(id = R.dimen.s))
			.clip(CircleShape),
	) {
		Box(
			modifier = Modifier
				.width(dimensionResource(id = R.dimen.l))
				.height(dimensionResource(id = R.dimen.l))
				.background(MaterialTheme.subletrPalette.subletrPink),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = n.toString(),
				color = MaterialTheme.subletrPalette.textOnSubletrPink,
				style = MaterialTheme.typography.labelLarge,
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
fun ChatListItemPreview() {
	SubletrTheme {
		ChatListItem(
			contact = Contact(
				name = "John Smith",
				msg = listOf("Hello", "Can I sublet your apartement?"),
				unread = 2,
			)
		)
	}
}
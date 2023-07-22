package org.uwaterloo.subletr.pages.home.list.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.components.button.PrimaryButton
import org.uwaterloo.subletr.components.button.SecondaryButton
import org.uwaterloo.subletr.theme.filterBoldFont
import org.uwaterloo.subletr.theme.subletrPalette


@Composable
fun BasicFilterLayout(
	modifier: Modifier = Modifier,
	contentModifier: Modifier = Modifier,
	@StringRes titleId: Int,
	closeAction: () -> Unit,
	content: @Composable (() -> Unit),
	updateFilterAndClose: () -> Unit,
	clearAction: () -> Unit,
	revertInput: () -> Unit = {},
) {
	Scaffold(
		modifier = modifier
			.fillMaxWidth(1.0f),
		containerColor = MaterialTheme.subletrPalette.primaryBackgroundColor,
		topBar = {
			Row(
				modifier = Modifier
					.fillMaxWidth(1.0f)
					.height(dimensionResource(id = R.dimen.xl))
					.padding(
						horizontal = dimensionResource(id = R.dimen.m),
						vertical = dimensionResource(id = R.dimen.xxs),
					),
				horizontalArrangement = Arrangement.SpaceBetween,
			)
			{
				Icon(
					modifier = Modifier.clickable {
						closeAction()
					},
					painter = painterResource(
						id = R.drawable.close_filled_black_24
					),
					contentDescription = stringResource(id = R.string.close),
				)
				Text(
					stringResource(id = titleId),
					style = MaterialTheme.typography.displayMedium,
					color = MaterialTheme.subletrPalette.primaryTextColor,
				)
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.m)))

			}
		},
		bottomBar = {
			Column(
				modifier = Modifier.height(dimensionResource(id = R.dimen.xxl)).background(MaterialTheme.subletrPalette.primaryBackgroundColor),
			) {
				FilterDefaultDivider()
				Row(
					modifier = Modifier
						.fillMaxWidth(1.0f)
						.padding(
							horizontal = dimensionResource(id = R.dimen.m),
							vertical = dimensionResource(id = R.dimen.xxs),
						),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
				) {
					Text(
						stringResource(id = R.string.clear),
						style = filterBoldFont,
						modifier = Modifier.clickable { clearAction() },
					)
					PrimaryButton(onClick = {
						updateFilterAndClose()

					}, content = {
						Text(
							stringResource(
								id = R.string.show_sublets,
							),
							color = MaterialTheme.subletrPalette.primaryBackgroundColor,
							style = filterBoldFont,

							)
					})
				}
			}

		}
	) { paddingValues ->
		Box(
			modifier = contentModifier
				.padding(
					top = paddingValues.calculateTopPadding(),
					bottom = dimensionResource(R.dimen.zero),
				)
				.padding(dimensionResource(id = R.dimen.m), dimensionResource(id = R.dimen.zero))
				.fillMaxWidth(1.0f),
		) {
			content()
		}
	}
}

@Composable
fun TextFieldWithErrorIndication(
	modifier: Modifier = Modifier,
	value: String,
	onValueChange: (String) -> Unit,
	isError: Boolean,
	placeholderString: String,
	suffix: @Composable (() -> Unit)? = null,
) {
	TextField(
		modifier = modifier
			.width(dimensionResource(id = R.dimen.xxxxxxl))
			.height(dimensionResource(id = R.dimen.xl))
			.border(
				width = dimensionResource(id = R.dimen.xxxxs),
				color = if (isError) Color.Red else Color.Transparent,
				shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs))
			),
		value = value,
		colors = TextFieldDefaults.colors(
			unfocusedContainerColor = MaterialTheme.subletrPalette.textFieldBackgroundColor,
			focusedContainerColor = MaterialTheme.subletrPalette.textFieldBackgroundColor,
			unfocusedIndicatorColor = Color.Transparent,
			focusedIndicatorColor = Color.Transparent,
			errorIndicatorColor = Color.Transparent,
			errorContainerColor = MaterialTheme.subletrPalette.textFieldBackgroundColor,
		),
		textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
		onValueChange = onValueChange,
		isError = isError,
		singleLine = true,
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		shape = RoundedCornerShape(dimensionResource(id = R.dimen.xs)),
		placeholder = {
			Text(
				text = placeholderString,
				color = MaterialTheme.subletrPalette.secondaryTextColor,
				style = TextStyle(fontSize = 14.sp),
			)
		},
		suffix = suffix,

		)
}

@Composable
fun DefaultFilterButton(
	modifier: Modifier = Modifier,
	isSelected: Boolean,
	onClick: () -> Unit,
	text: String,
) {
	SecondaryButton(
		modifier = modifier
			.defaultMinSize(minWidth = dimensionResource(id = R.dimen.xxxxs), minHeight = dimensionResource(id = R.dimen.xxxxs)),
		onClick = onClick,
		contentPadding = PaddingValues(
			horizontal = dimensionResource(id = R.dimen.s),
			vertical = dimensionResource(id = R.dimen.xs),
		),
		content = { Text(text = text, color = if (isSelected) Color.White else MaterialTheme.subletrPalette.primaryTextColor) },
		colors = ButtonDefaults.buttonColors(
			containerColor =
			if (isSelected) MaterialTheme.subletrPalette.subletrPink
			else MaterialTheme.subletrPalette.secondaryButtonBackgroundColor,
		),
	)

}
@Composable
fun FilterDefaultDivider(modifier: Modifier = Modifier){
	Divider(
		color = MaterialTheme.subletrPalette.textFieldBackgroundColor,
		modifier = modifier
			.height(dimensionResource(id = R.dimen.xxxxs))
			.fillMaxWidth(),
	)
}

fun verifyNewBoundVal(
	newVal: String,
	lowerBound: String? = null,
	upperBound: String? = null,
): Boolean {
	if (newVal.isEmpty()) {
		return true
	}
	val newInt = newVal.toIntOrNull()
	var result = false
	if (newInt != null && newInt >= 0) {
		if (lowerBound == null) {
			if (upperBound!!.isEmpty()) {
				result = true
			}
			val ub = upperBound.toIntOrNull()
			if (ub == null || newInt <= ub) {
				result = true
			}
		} else {
			if (lowerBound.isEmpty()) {
				result = true
			}
			val lb = lowerBound.toIntOrNull()
			if (lb == null || newInt >= lb) {
				result = true
			}
		}
	}
	return result

}

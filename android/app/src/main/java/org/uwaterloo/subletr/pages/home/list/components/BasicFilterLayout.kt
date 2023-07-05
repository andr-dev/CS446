package org.uwaterloo.subletr.pages.home.list.components

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import org.uwaterloo.subletr.theme.SubletrLightColorScheme
import org.uwaterloo.subletr.theme.filterBoldFont
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.textFieldBackgroundColor


@Composable
fun BasicFilterLayout(
	modifier: Modifier = Modifier,
	@StringRes titleId: Int,
	closeAction: () -> Unit,
	content: @Composable (() -> Unit)? = null,
	updateFilterAndClose: () -> Unit,
	clearAction: () -> Unit,
	revertInput: () -> Unit,
) {
	Scaffold(
		modifier = modifier
			.fillMaxWidth(1.0f),
		containerColor = SubletrLightColorScheme.onPrimary,
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
						revertInput()
					},
					painter = painterResource(
						id = R.drawable.close_filled_black_24
					),
					contentDescription = stringResource(id = R.string.close),
				)
				Text(
					stringResource(id = titleId),
					style = MaterialTheme.typography.displayMedium
				)
				Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.m)))

			}
		},
		bottomBar = {
			Column(
				modifier = Modifier.height(dimensionResource(id = R.dimen.xxl)),
			) {
				Divider(
					color = textFieldBackgroundColor,
					modifier = Modifier
						.height(dimensionResource(id = R.dimen.xxxxs))
						.fillMaxWidth(),
				)
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
							color = SubletrLightColorScheme.onPrimary,
							style = filterBoldFont,

							)
					})
				}
			}

		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(
					top = paddingValues.calculateTopPadding(),
					bottom = dimensionResource(R.dimen.zero),
				)
				.padding(dimensionResource(id = R.dimen.m), dimensionResource(id = R.dimen.zero))
				.fillMaxWidth(1.0f),
		) {
			if (content != null) {
				content()
			}
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
			unfocusedContainerColor = textFieldBackgroundColor,
			focusedContainerColor = textFieldBackgroundColor,
			unfocusedIndicatorColor = Color.Transparent,
			focusedIndicatorColor = Color.Transparent,
			errorIndicatorColor = Color.Transparent,
			errorContainerColor = textFieldBackgroundColor,
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
				color = secondaryTextColor,
				style = TextStyle(fontSize = 14.sp),
			)
		},
		suffix = suffix,

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

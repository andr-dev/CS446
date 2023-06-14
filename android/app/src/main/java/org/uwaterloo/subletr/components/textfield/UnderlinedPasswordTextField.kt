package org.uwaterloo.subletr.components.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.theme.subletrPink

@Composable
@Suppress("LongParameterList")
fun UnderlinedPasswordTextField(
	value: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	readOnly: Boolean = false,
	textStyle: TextStyle = LocalTextStyle.current,
	label: @Composable (() -> Unit)? = null,
	placeholder: @Composable (() -> Unit)? = null,
	leadingIcon: @Composable (() -> Unit)? = null,
	trailingIcon: @Composable (() -> Unit)? = null,
	prefix: @Composable (() -> Unit)? = null,
	suffix: @Composable (() -> Unit)? = null,
	supportingText: @Composable (() -> Unit)? = null,
	isError: Boolean = false,
	visualTransformation: VisualTransformation? = null,
	keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
	keyboardActions: KeyboardActions = KeyboardActions.Default,
	singleLine: Boolean = true,
	maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
	minLines: Int = 1,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	shape: Shape = TextFieldDefaults.shape,
	colors: TextFieldColors = TextFieldDefaults.colors(
		unfocusedContainerColor = Color.Transparent,
		focusedContainerColor = Color.Transparent,
		unfocusedIndicatorColor = subletrPink,
		focusedIndicatorColor = subletrPink,
	),
) {
	var passwordVisible by rememberSaveable { mutableStateOf(false) }

	TextField(
		value = value,
		onValueChange = onValueChange,
		modifier = modifier,
		enabled = enabled,
		readOnly = readOnly,
		textStyle = textStyle,
		label = label,
		placeholder = placeholder,
		leadingIcon = leadingIcon,
		trailingIcon = trailingIcon
			?: {
				IconButton(onClick = { passwordVisible = !passwordVisible }) {
					Icon(
						imageVector =
						if (passwordVisible) Icons.Filled.Visibility
						else Icons.Filled.VisibilityOff,
						contentDescription =
						if (passwordVisible) stringResource(
							id = R.string.hide_password
						)
						else stringResource(
							id = R.string.show_password
						),
					)
				}
			},
		prefix = prefix,
		suffix = suffix,
		supportingText = supportingText,
		isError = isError,
		visualTransformation = visualTransformation
			?: if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
		keyboardOptions = keyboardOptions,
		keyboardActions = keyboardActions,
		singleLine = singleLine,
		maxLines = maxLines,
		minLines = minLines,
		interactionSource = interactionSource,
		shape = shape,
		colors = colors,
	)
}

@Preview(showBackground = true)
@Composable
@Suppress("UnusedPrivateMember")
private fun UnderlinedPasswordTextFieldPreview() {
	UnderlinedPasswordTextField(
		value = "Test TextField Value",
		onValueChange = {},
	)
}

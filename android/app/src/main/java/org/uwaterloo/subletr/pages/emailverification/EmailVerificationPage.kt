package org.uwaterloo.subletr.pages.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.pages.login.ELEMENT_WIDTH
import org.uwaterloo.subletr.pages.login.LoginPageUiState
import org.uwaterloo.subletr.theme.buttonBackgroundColor
import org.uwaterloo.subletr.theme.secondaryTextColor
import org.uwaterloo.subletr.theme.subletrPink

@Composable
fun EmailVerificationPage(
        modifier: Modifier = Modifier
) {
    var verficationCode by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier.fillMaxSize(1.0f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ){

        Text(
            text = stringResource(id = R.string.email_verification),
            style = MaterialTheme.typography.titleMedium,


        )
//        Spacer(
//            modifier = Modifier.weight(weight = 10.0f)
//        )
        Text(
            text = stringResource(id = R.string.email_verification_instruction),
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier.fillMaxWidth(0.77f)
        )

//        Row(modifier = modifier.fillMaxWidth(0.77f),
//            horizontalArrangement = Arrangement.spacedBy(5.dp)
//
//        ) {

        VerificationCodeTextField()
        Button(
            modifier = Modifier
                .fillMaxWidth(ELEMENT_WIDTH)
                .height(50.dp),
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = subletrPink,
                contentColor = Color.White,
            )
        ) {
            Text(
                text = stringResource(id = R.string.verify),
                color = Color.White,
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(ELEMENT_WIDTH)
                .height(50.dp),
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonBackgroundColor,
                contentColor = Color.White,
            )
        ) {
            Text(
                text = stringResource(id = R.string.resend_code),
                color = Color.White,
            )
        }

    }

}


@Composable
fun TextFieldBox(index:Int, focusManager:FocusManager) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var backgroundColor by remember { mutableStateOf(buttonBackgroundColor) }
    var borderColor by remember { mutableStateOf(buttonBackgroundColor) }
    TextField(
        modifier = Modifier
            .width(54.dp)
            .height(56.dp)
            .wrapContentSize(align = Alignment.Center)
            .border(BorderStroke(2.dp, borderColor), shape = RoundedCornerShape(5.dp))
            .onFocusChanged {
                if (it.isFocused) {
                    borderColor = subletrPink
                    backgroundColor = Color.White
                }

            },

        shape = RoundedCornerShape(size = 5.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = backgroundColor,
            focusedContainerColor = backgroundColor,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,


            ),
        singleLine = true,
        textStyle = TextStyle(
            color = Color.Black,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center

        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        value = text,

        onValueChange = {
            text = it
            println(text)
            if (text.text != " " && text.text != "" ){
                focusManager.moveFocus(FocusDirection.Right)
            }

        }
    )
}
@Composable
fun VerificationCodeTextField() {
    var focus by remember { mutableStateOf(0) }
    val focusManager = LocalFocusManager.current
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (0 until 5).forEach {
            TextFieldBox(
                index = it,

                focusManager
            )
        }


    }
}
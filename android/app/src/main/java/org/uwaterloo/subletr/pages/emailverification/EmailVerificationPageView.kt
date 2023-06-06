package org.uwaterloo.subletr.pages.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.pages.emailverification.EmailVerificationPageUiState
import org.uwaterloo.subletr.pages.emailverification.EmailVerificationPageViewModel
import org.uwaterloo.subletr.pages.login.ELEMENT_WIDTH
import org.uwaterloo.subletr.pages.login.LoginPageUiState
import org.uwaterloo.subletr.theme.buttonBackgroundColor
import org.uwaterloo.subletr.theme.subletrPink

@Composable
fun EmailVerificationPageView(
    modifier: Modifier = Modifier,
    viewModel: EmailVerificationPageViewModel = hiltViewModel(),
    uiState: EmailVerificationPageUiState = viewModel.uiStateStream.subscribeAsState(
            EmailVerificationPageUiState.Loading
        ).value
) {

    if (uiState is EmailVerificationPageUiState.Loading) {
        Column(
            modifier = modifier
                .fillMaxSize(1.0f)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(id = R.string.loading)
            )
        }
    } else if (uiState is EmailVerificationPageUiState.Loaded)
    {Column(
        modifier = modifier.fillMaxSize(1.0f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,


    ){
        Spacer(
            modifier = Modifier.weight(weight = 8.0f)
        )
        Text(
            text = stringResource(id = R.string.email_verification),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(id = R.string.email_verification_instruction),
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier.fillMaxWidth(0.77f) ,
        )
        Spacer(
            modifier = Modifier.weight(weight = 13.0f)
        )
        VerificationCodeTextField(viewModel)
        Spacer(
            modifier = Modifier.weight(weight = 20.0f)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(ELEMENT_WIDTH)
                .height(40.dp),
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = subletrPink,
                contentColor = Color.White,
            )
        ) {
            Text(
                text = stringResource(id = R.string.verify),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Justify
            )
        }
        Spacer(
            modifier = Modifier.height(20.dp)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(ELEMENT_WIDTH)
                .height(40.dp),
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonBackgroundColor,
                contentColor = Color.White,
            )
        ) {
            Text(
                text = stringResource(id = R.string.resend_code),
                color = Color.Gray,
                style = MaterialTheme.typography.labelMedium,

            )
        }
        Spacer(
            modifier = Modifier.weight(weight = 13.0f)
        )

    }}

}


@Composable
fun TextFieldBox(index:Int, focusManager:FocusManager,viewModel: EmailVerificationPageViewModel,uiState: EmailVerificationPageUiState = viewModel.uiStateStream.subscribeAsState(
    EmailVerificationPageUiState.Loading
).value) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var btnGrey = buttonBackgroundColor
    var sublrPink = subletrPink

    var backgroundColor by remember { mutableStateOf(btnGrey) }
    var borderColor by remember { mutableStateOf(btnGrey) }
    if (uiState is EmailVerificationPageUiState.Loaded){


    TextField(
        modifier = Modifier
            .width(54.dp)
            .height(56.dp)
            .wrapContentSize(align = Alignment.Center)
            .border(BorderStroke(2.dp, borderColor), shape = RoundedCornerShape(5.dp))
            .onFocusChanged {
                if (it.isFocused) {
                    borderColor = sublrPink
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

        value = uiState.verificationCode[index],

        onValueChange = {
           if (it.length <= 1 ) {
               val newCode = uiState.verificationCode.toMutableList()
               newCode[index] = it
               viewModel.updateUiState(
                   EmailVerificationPageUiState.Loaded(
                       verificationCode = newCode.toList()
                   )
               )
           }
            if (text.text != " " && text.text != "" ){
                focusManager.moveFocus(FocusDirection.Right)
            }

        }
    )}
}
@Composable
fun VerificationCodeTextField( viewModel: EmailVerificationPageViewModel) {
    val focusManager = LocalFocusManager.current
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (0 until 5).forEach {
            TextFieldBox(
                index = it,
                focusManager,
                viewModel
            )
        }

    }
}
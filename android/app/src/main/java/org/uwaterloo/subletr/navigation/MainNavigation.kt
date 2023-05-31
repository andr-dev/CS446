package org.uwaterloo.subletr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.uwaterloo.subletr.pages.account.AccountPageView
import org.uwaterloo.subletr.pages.chat.ChatPageView
import org.uwaterloo.subletr.pages.createaccount.CreateAccountPageView
import org.uwaterloo.subletr.pages.home.HomePageView
import org.uwaterloo.subletr.pages.login.LoginPageView

@Composable
fun MainNavigation(
	modifier: Modifier = Modifier,
	navHostController: NavHostController = rememberNavController(),
) {
	NavHost(
		navController = navHostController,
		startDestination = "login",
	) {
		composable("login") {
			LoginPageView(
				modifier = modifier,
			)
		}
		composable("createaccount") {
			CreateAccountPageView(
				modifier = modifier,
			)
		}
		composable("home") {
			HomePageView(
				modifier = modifier,
			)
		}
		composable("account") {
			AccountPageView(
				modifier = modifier,
			)
		}
		composable("chat") {
			ChatPageView(
				modifier = modifier,
			)
		}
	}
}

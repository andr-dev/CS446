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
import org.uwaterloo.subletr.pages.home.EmailVerificationPage
import org.uwaterloo.subletr.pages.home.HomePageView
import org.uwaterloo.subletr.pages.login.LoginPageView

@Composable
fun MainNavigation(
	modifier: Modifier = Modifier,
	navHostController: NavHostController = rememberNavController(),
) {
	NavHost(
		navController = navHostController,
		startDestination = NavigationDestination.VERIFY_EMAIL.rootNavPath,
	) {
		composable(NavigationDestination.LOGIN.rootNavPath) {
			LoginPageView(
				modifier = modifier,
				navHostController = navHostController,
			)
		}
		composable(NavigationDestination.CREATE_ACCOUNT.rootNavPath) {
			CreateAccountPageView(
				modifier = modifier,
			)
		}
		composable(NavigationDestination.VERIFY_EMAIL.rootNavPath) {
			EmailVerificationPage(
				modifier = modifier,
			)
		}
		composable(NavigationDestination.HOME.rootNavPath) {
			HomePageView(
				modifier = modifier,
			)
		}
		composable(NavigationDestination.ACCOUNT.rootNavPath) {
			AccountPageView(
				modifier = modifier,
			)
		}
		composable(NavigationDestination.CHAT.rootNavPath) {
			ChatPageView(
				modifier = modifier,
			)
		}
	}
}

package org.uwaterloo.subletr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.uwaterloo.subletr.api.infrastructure.ApiClient
import org.uwaterloo.subletr.pages.account.AccountPageView
import org.uwaterloo.subletr.pages.changepassword.ChangePasswordPageView
import org.uwaterloo.subletr.pages.chat.ChatPageView
import org.uwaterloo.subletr.pages.createaccount.CreateAccountPageView
import org.uwaterloo.subletr.pages.createlisting.CreateListingPageView
import org.uwaterloo.subletr.pages.emailverification.EmailVerificationPageView
import org.uwaterloo.subletr.pages.home.HomePageView
import org.uwaterloo.subletr.pages.listingdetails.ListingDetailsPageView
import org.uwaterloo.subletr.pages.login.LoginPageView

@Composable
fun MainNavigation(
	modifier: Modifier = Modifier,
	navHostController: NavHostController = rememberNavController(),
) {
	val startingDestination =
		if (ApiClient.accessToken == null) NavigationDestination.LOGIN.fullNavPath
		else NavigationDestination.HOME.fullNavPath

	NavHost(
		navController = navHostController,
		startDestination = startingDestination,
	) {
		composable(NavigationDestination.LOGIN.fullNavPath) {
			LoginPageView(
				modifier = modifier,
			)
		}
		composable(NavigationDestination.CREATE_ACCOUNT.fullNavPath) {
			CreateAccountPageView(
				modifier = modifier,
			)
		}
		composable(
			route = NavigationDestination.VERIFY_EMAIL.fullNavPath,
			arguments = listOf(navArgument("userId") { type = NavType.IntType }),
		) {
			EmailVerificationPageView(
				modifier = modifier,
				userId = it.arguments?.getInt("userId"),
			)
		}
		composable(NavigationDestination.HOME.fullNavPath) {
			HomePageView(
				modifier = modifier,
			)
		}
		composable(NavigationDestination.ACCOUNT.fullNavPath) {
			AccountPageView(
				modifier = modifier,
			)
		}
		composable(NavigationDestination.CHANGE_PASSWORD.fullNavPath) {
			ChangePasswordPageView(
				modifier = modifier,
			)
		}
		composable(NavigationDestination.CHAT.fullNavPath) {
			ChatPageView(
				modifier = modifier,
			)
		}
		composable(
			route = NavigationDestination.LISTING_DETAILS.fullNavPath,
			arguments = listOf(navArgument("listingId") { type = NavType.IntType }),
		) {
			ListingDetailsPageView(
				modifier = modifier,
			)
		}
		composable(NavigationDestination.CREATE_LISTING.rootNavPath) {
			CreateListingPageView(
				modifier = modifier,
			)
		}
	}
}

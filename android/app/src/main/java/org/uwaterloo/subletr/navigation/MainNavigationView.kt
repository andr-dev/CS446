package org.uwaterloo.subletr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.uwaterloo.subletr.pages.account.AccountPageView
import org.uwaterloo.subletr.pages.changepassword.ChangePasswordPageView
import org.uwaterloo.subletr.pages.chat.ChatListingPageView
import org.uwaterloo.subletr.pages.createaccount.CreateAccountPageView
import org.uwaterloo.subletr.pages.createlisting.CreateListingPageView
import org.uwaterloo.subletr.pages.emailverification.EmailVerificationPageView
import org.uwaterloo.subletr.pages.home.HomePageView
import org.uwaterloo.subletr.pages.individualchat.IndividualChatPageView
import org.uwaterloo.subletr.pages.listingdetails.ListingDetailsPageView
import org.uwaterloo.subletr.pages.login.LoginPageView
import org.uwaterloo.subletr.pages.managelisting.ManageListingPageView
import org.uwaterloo.subletr.pages.profile.ProfilePageView
import org.uwaterloo.subletr.pages.watcardverification.WatcardVerificationPageView

@Composable
fun MainNavigationView(
	modifier: Modifier = Modifier,
	mainNavigationViewModel: MainNavigationViewModel = hiltViewModel(),
) {
	NavHost(
		navController = mainNavigationViewModel.navHostController,
		startDestination = mainNavigationViewModel.startingDestination.fullNavPath,
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
		composable(NavigationDestination.VERIFY_WATCARD.rootNavPath) {
			WatcardVerificationPageView(
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
		composable(NavigationDestination.CHAT_LISTING.fullNavPath) {
			ChatListingPageView(
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
		composable(NavigationDestination.CREATE_LISTING.fullNavPath) {
			CreateListingPageView(
				modifier = modifier,
			)
		}
		composable(
			route = NavigationDestination.INDIVIDUAL_CHAT_PAGE.fullNavPath,
			arguments = listOf(navArgument("userId") { type = NavType.IntType }
			),
		) {
			IndividualChatPageView(
				modifier = modifier,
			)
		}
		composable(
			route = NavigationDestination.PROFILE.fullNavPath,
			arguments = listOf(navArgument("userId") { type = NavType.IntType }
			),
		) {
			ProfilePageView(
				modifier = modifier,
			)
		}
		composable(
			route = NavigationDestination.MANAGE_LISTING.fullNavPath,
			arguments = listOf(navArgument("listingId") { type = NavType.IntType} )
		) {
			ManageListingPageView(
				modifier = modifier,
			)
		}
	}
}

package org.uwaterloo.subletr.navigation

import org.uwaterloo.subletr.R

enum class NavigationDestination(
	val rootNavPath: String,
	val fullNavPath: String = rootNavPath,
	val bottomBarNavigationItems: BottomBarNavigationItems? = null,
	val showBottomBar: Boolean = false,
) {
	LOGIN(
		rootNavPath = "login",
	),
	CREATE_ACCOUNT(
		rootNavPath = "createaccount",
	),
	VERIFY_EMAIL(
		rootNavPath = "verifyemail",
		fullNavPath = "verifyemail/{userId}",
	),
	HOME(
		rootNavPath = "home",
		bottomBarNavigationItems = BottomBarNavigationItems(
			selectedIconId = R.drawable.home_solid_pink_24,
			unselectedIconId = R.drawable.home_outline_gray_24,
			bottomBarNavigationTextId = R.string.home,
		),
		showBottomBar = true,
	),
	ACCOUNT(
		rootNavPath = "account",
		bottomBarNavigationItems = BottomBarNavigationItems(
			selectedIconId = R.drawable.person_solid_pink_24,
			unselectedIconId = R.drawable.person_outline_gray_24,
			bottomBarNavigationTextId = R.string.account,
		),
		showBottomBar = true,
	),
	CHANGE_PASSWORD(
		rootNavPath = "account/change-password",
		bottomBarNavigationItems = null,
		showBottomBar = true,
	),
	CHAT(
		rootNavPath = "chat",
		bottomBarNavigationItems = BottomBarNavigationItems(
			selectedIconId = R.drawable.chat_bubble_solid_pink_24,
			unselectedIconId = R.drawable.chat_bubble_outline_gray_24,
			bottomBarNavigationTextId = R.string.chat,
		),
		showBottomBar = true,
	),
	LISTING_DETAILS(
		rootNavPath = "home/listingdetails",
		fullNavPath = "home/listingdetails/{listingId}",
		bottomBarNavigationItems = null,
		showBottomBar = true,
	),
	CREATE_LISTING(
		rootNavPath = "home/createlisting",
	),
}

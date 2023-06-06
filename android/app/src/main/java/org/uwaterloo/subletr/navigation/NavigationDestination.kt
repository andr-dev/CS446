package org.uwaterloo.subletr.navigation

import org.uwaterloo.subletr.R

enum class NavigationDestination(
	val rootNavPath: String,
	val bottomBarNavigationItems: BottomBarNavigationItems?,
	val showBottomBar: Boolean = false,
) {
	LOGIN(
		rootNavPath = "login",
		bottomBarNavigationItems = null,
	),
	CREATE_ACCOUNT(
		rootNavPath = "createaccount",
		bottomBarNavigationItems = null,
	),
	VERIFY_EMAIL(
		rootNavPath = "verifyemail",
		bottomBarNavigationItems = null,
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
	CHAT(
		rootNavPath = "chat",
		bottomBarNavigationItems = BottomBarNavigationItems(
			selectedIconId = R.drawable.chat_bubble_solid_pink_24,
			unselectedIconId = R.drawable.chat_bubble_outline_gray_24,
			bottomBarNavigationTextId = R.string.chat,
		),
		showBottomBar = true,
	)
}

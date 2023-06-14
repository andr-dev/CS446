package org.uwaterloo.subletr.navigation

import org.uwaterloo.subletr.R

enum class NavigationDestination(
	val rootNavPath: String,
	val bottomBarNavigationItems: BottomBarNavigationItems? = null,
	val showBottomBar: Boolean = false,
	val parent: NavigationDestination? = null,
) {
	LOGIN(
		rootNavPath = "login",
	),
	CREATE_ACCOUNT(
		rootNavPath = "createaccount",
	),
	VERIFY_EMAIL(
		rootNavPath = "verifyemail",
		parent = CREATE_ACCOUNT,
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
		parent = ACCOUNT,
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

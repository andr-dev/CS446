package org.uwaterloo.subletr.navigation

import org.uwaterloo.subletr.R

enum class TopLevelDestination(
	val selectedIconId: Int,
	val unselectedIconId: Int,
	val bottomBarNavigationTextId: Int,
) {
	HOME(
		selectedIconId = R.drawable.home_solid_pink_24,
		unselectedIconId = R.drawable.home_outline_gray_24,
		bottomBarNavigationTextId = R.string.home,
	),
	ACCOUNT(
		selectedIconId = R.drawable.person_solid_pink_24,
		unselectedIconId = R.drawable.person_outline_gray_24,
		bottomBarNavigationTextId = R.string.account,
	),
	CHAT(
		selectedIconId = R.drawable.chat_bubble_solid_pink_24,
		unselectedIconId = R.drawable.chat_bubble_outline_gray_24,
		bottomBarNavigationTextId = R.string.chat,
	)
}

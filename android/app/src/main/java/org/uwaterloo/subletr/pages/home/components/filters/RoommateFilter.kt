package org.uwaterloo.subletr.pages.home.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.enums.Gender

@Composable
fun RoommateFilter(
	currentGenderPref: Gender,
	updateGenderFilter: (Gender) -> Unit,
	closeAction: () -> Unit,
) {
	var genderPref by remember {
		mutableStateOf(
			currentGenderPref
		)
	}
	BasicFilterLayout(
		modifier = Modifier.height(200.dp),
		titleId = R.string.roommate_preference,

		content = {
			Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.xs)))
			Row(
				modifier = Modifier.fillMaxWidth(1.0f),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.s)),
			) {

				DefaultFilterButton(
					isSelected = (genderPref == Gender.OTHER),
					onClick = { genderPref = Gender.OTHER },
					text = stringResource(
						id = R.string.any,
					),
				)
				DefaultFilterButton(
					isSelected = (genderPref == Gender.FEMALE),
					onClick = { genderPref = Gender.FEMALE },
					text = stringResource(
						id = R.string.female,
					),
				)
				DefaultFilterButton(
					isSelected = (genderPref == Gender.MALE),
					onClick = { genderPref = Gender.MALE },
					text = stringResource(
						id = R.string.male,
					),
				)
			}
		},
		closeAction = closeAction,
		clearAction = {
			genderPref = Gender.OTHER
		},
		revertInput = {
			genderPref = currentGenderPref
		},
		updateFilterAndClose = {
			updateGenderFilter(genderPref)
			closeAction()
		}


	)

}

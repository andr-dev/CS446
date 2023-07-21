package org.uwaterloo.subletr.components.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import org.uwaterloo.subletr.R
import org.uwaterloo.subletr.pages.createlisting.ImageUploadMethodButton
import org.uwaterloo.subletr.theme.primaryBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerBottomSheet(
	bottomSheetState: SheetState,
	onDismissRequest: () -> Unit,
	onTakePhotoClick: () -> Unit,
	onChooseImageClick: () -> Unit,
) {
	ModalBottomSheet(
		modifier = Modifier,
		onDismissRequest = onDismissRequest,
		sheetState = bottomSheetState,
		containerColor = primaryBackgroundColor,
		dragHandle = { BottomSheetDefaults.DragHandle(
			width = dimensionResource(id = R.dimen.xl)
		)},
		content = {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(dimensionResource(id = R.dimen.xxxxxxxl))
					.background(primaryBackgroundColor)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.fillMaxHeight()
						.padding(horizontal = dimensionResource(id = R.dimen.l)),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					ImageUploadMethodButton(
						R.drawable.photo_camera_outline_black_64,
						stringResource(id = R.string.take_photo),
						onClick = onTakePhotoClick,
					)

					ImageUploadMethodButton(
						R.drawable.photo_outline_black_64,
						stringResource(id = R.string.choose_image),
						onClick = onChooseImageClick,
					)
				}
			}
		},
	)
}

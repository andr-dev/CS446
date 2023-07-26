package org.uwaterloo.subletr.services

import androidx.compose.material3.SnackbarHostState

class SnackbarService: ISnackbarService {
	override val snackbarHostState = SnackbarHostState()
}

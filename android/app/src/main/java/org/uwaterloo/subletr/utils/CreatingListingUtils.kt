package org.uwaterloo.subletr.utils

import org.uwaterloo.subletr.pages.createlisting.CreateListingPageUiState

fun canCreate(uiState: CreateListingPageUiState.Loaded): Boolean {
	return !addressIsEmpty(uiState.address) &&
		uiState.price > 0 &&
		uiState.startDate != "" &&
		uiState.endDate != "" &&
		uiState.numBedrooms > 0 &&
		uiState.numBathrooms > 0 &&
		uiState.totalNumBathrooms > 0 &&
		uiState.totalNumBedrooms > 0
}

fun addressIsEmpty(addressModel: CreateListingPageUiState.AddressModel): Boolean {
	return addressModel.addressCity == "" &&
		addressModel.addressLine == "" &&
		addressModel.addressPostalCode == "" &&
		addressModel.fullAddress == ""
}

fun addressHasEmpty(addressModel: CreateListingPageUiState.AddressModel): Boolean {
	return addressModel.addressCity.isBlank() ||
		addressModel.addressLine.isBlank() ||
		addressModel.addressPostalCode.isBlank() ||
		addressModel.fullAddress.isBlank()
}

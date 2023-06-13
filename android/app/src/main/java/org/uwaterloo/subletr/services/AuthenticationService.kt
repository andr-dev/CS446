package org.uwaterloo.subletr.services

import org.uwaterloo.subletr.api.infrastructure.ApiClient

class AuthenticationService(
	private val ioService: IIoService,
): IAuthenticationService {
	override fun accessTokenExists(): Boolean {
		return ioService.internalFileExists(ACCESS_TOKEN_PATH)
	}

	override fun setAccessToken(accessToken: String) {
		ioService.writeStringToInternalFile(
			fileName = ACCESS_TOKEN_PATH,
			input = accessToken,
		)
		ApiClient.accessToken = accessToken
	}

	override fun deleteAccessToken() {
		ioService.deleteInternalFile(
			fileName = ACCESS_TOKEN_PATH,
		)
		ApiClient.accessToken = null
	}

	override fun setAccessTokenFromInternalFile() {
		if (accessTokenExists()) {
			ApiClient.accessToken = ioService.readStringFromInternalFile(ACCESS_TOKEN_PATH)
		}
	}

	companion object {
		private const val ACCESS_TOKEN_PATH = "accesstoken.subletr"
	}
}

package org.uwaterloo.subletr.services

import android.util.Log
import com.squareup.moshi.Moshi
import org.uwaterloo.subletr.api.infrastructure.ApiClient
import org.uwaterloo.subletr.models.AuthenticatedUser
import java.util.Base64

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

	override fun isAuthenticatedUser(): AuthenticatedUser? {
		if (accessTokenExists()) {
			val token = ioService.readStringFromInternalFile(ACCESS_TOKEN_PATH)

			val chunks = token.split(".")

			val decoder= Base64.getUrlDecoder()
//			val payload = String(decoder.decode(chunks[1]))

			val payload = String(Base64.getUrlDecoder().decode(chunks[1].toByteArray()))

			val moshi = Moshi.Builder().build()
			val jsonAdapter = moshi.adapter<AuthenticatedUser>(AuthenticatedUser::class.java)
			val authenticatedUser = jsonAdapter.fromJson(payload)
			Log.d("Logs", payload)
//			if (authenticatedUser != null) {
//				Log.d("Logs", authenticatedUser.userId.toString())
//			}

//			return authenticatedUser

			return AuthenticatedUser(
				1, 2
			)

		} else {
			return null
		}
	}

	companion object {
		private const val ACCESS_TOKEN_PATH = "accesstoken.subletr"
	}
}

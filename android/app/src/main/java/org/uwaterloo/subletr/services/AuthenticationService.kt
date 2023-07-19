package org.uwaterloo.subletr.services

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

	/*
	* Only run after application init
	* */
	override fun isAuthenticatedUser(): AuthenticatedUser? {
		val token = ApiClient.accessToken
		return if (accessTokenExists() && token != null) {
			val chunks = token.split(".")
			val payload = String(Base64.getUrlDecoder().decode(chunks[1].toByteArray()))

			val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
			val jsonAdapter = moshi.adapter(AuthenticatedUser::class.java)
			jsonAdapter.fromJson(payload)
		} else {
			null
		}
	}

	companion object {
		private const val ACCESS_TOKEN_PATH = "accesstoken.subletr"
	}
}

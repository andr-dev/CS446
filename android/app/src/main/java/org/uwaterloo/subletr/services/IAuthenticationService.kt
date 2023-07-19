package org.uwaterloo.subletr.services

import org.uwaterloo.subletr.models.AuthenticatedUser

interface IAuthenticationService {
	fun setAccessToken(accessToken: String)
	fun deleteAccessToken()
	fun accessTokenExists(): Boolean
	fun setAccessTokenFromInternalFile()
	fun isAuthenticatedUser(): AuthenticatedUser?
}

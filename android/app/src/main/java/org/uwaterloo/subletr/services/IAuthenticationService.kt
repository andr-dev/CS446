package org.uwaterloo.subletr.services

interface IAuthenticationService {
	fun setAccessToken(accessToken: String)
	fun deleteAccessToken()
	fun accessTokenExists(): Boolean
	fun setAccessTokenFromInternalFile()
}

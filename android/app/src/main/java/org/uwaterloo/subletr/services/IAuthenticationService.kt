package org.uwaterloo.subletr.services

import org.uwaterloo.subletr.models.AuthenticatedUser

interface IAuthenticationService {
	fun setAccessToken(accessToken: String)
	fun deleteAccessToken()
	fun accessTokenExists(): Boolean
	fun setAccessTokenFromInternalFile()
	/*
	* Only run after application init
	* */
	fun isAuthenticatedUser(): AuthenticatedUser?
}

package org.uwaterloo.subletr.models

import com.squareup.moshi.Json

data class AuthenticatedUser(

	val aud: String,

	@Json(name = "sub")
	val userId: Int,

	val exp: Long,
)

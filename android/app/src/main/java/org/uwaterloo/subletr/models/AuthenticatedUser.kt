package org.uwaterloo.subletr.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthenticatedUser(

	@Json(name = "sub")
	val userId: Int,

	@Json(name = "iat")
	val iat: Long,
)

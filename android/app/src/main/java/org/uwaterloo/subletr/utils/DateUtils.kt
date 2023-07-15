package org.uwaterloo.subletr.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun parseUTCDateTimeToLocal(dateTimeString: String): String {
	val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
	val utcDateTime = LocalDateTime.parse(dateTimeString, formatter)

	val utcZoneId = ZoneId.of("UTC")
	val localZoneId = ZoneId.systemDefault()

	val utcZonedDateTime = ZonedDateTime.of(utcDateTime, utcZoneId)
	val localDateTime = utcZonedDateTime.withZoneSameInstant(localZoneId).toLocalDateTime()

	return localDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
}

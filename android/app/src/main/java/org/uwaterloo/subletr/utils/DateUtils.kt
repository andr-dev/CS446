package org.uwaterloo.subletr.utils

import android.text.format.DateFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun useLocalization(): DateTimeFormatter {
	val locale = Locale.getDefault()
	return DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, "MMMM d, YYYY"))
}

fun parseUTCDateTimeToLocal(dateTimeString: String): String {
	val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
	val utcDateTime = LocalDateTime.parse(dateTimeString, formatter)

	val utcZoneId = ZoneId.of("UTC")
	val localZoneId = ZoneId.systemDefault()

	val utcZonedDateTime = ZonedDateTime.of(utcDateTime, utcZoneId)
	val localDateTime = utcZonedDateTime.withZoneSameInstant(localZoneId).toLocalDateTime()

	return localDateTime.format(useLocalization())
}

fun OffsetDateTime.localize(): String {
	return this.atZoneSameInstant(ZoneId.systemDefault()).format(useLocalization())
}

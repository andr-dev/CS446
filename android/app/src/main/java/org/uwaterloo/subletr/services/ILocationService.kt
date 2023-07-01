package org.uwaterloo.subletr.services

import android.location.Location

interface ILocationService {
	suspend fun getLocation(): Location?
}

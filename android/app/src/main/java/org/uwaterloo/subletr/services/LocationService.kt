package org.uwaterloo.subletr.services

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class LocationService @Inject constructor(
	val context: Context,
): ILocationService {
	override suspend fun getLocation(): Location? {
		val hasAccessCoarseLocationPermission: Boolean =
			context.checkSelfPermission(
				android.Manifest.permission.ACCESS_COARSE_LOCATION
			) == PackageManager.PERMISSION_GRANTED

		val hasAccessFineLocationPermission: Boolean =
			context.checkSelfPermission(
				android.Manifest.permission.ACCESS_FINE_LOCATION
			) == PackageManager.PERMISSION_GRANTED

		val locationManager: LocationManager = context.getSystemService(
			Context.LOCATION_SERVICE,
		) as LocationManager

		val isGpsEnabled: Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
			|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

		val fusedLocationProviderClient: FusedLocationProviderClient =
			LocationServices.getFusedLocationProviderClient(context)

		if (!isGpsEnabled &&
			!(hasAccessCoarseLocationPermission || hasAccessFineLocationPermission)) {
			return null
		}

		return suspendCancellableCoroutine { cont ->
			fusedLocationProviderClient.lastLocation.apply {
				if (isComplete) {
					if (isSuccessful) {
						cont.resume(result) {}
					} else {
						cont.resume(null) {}
					}
					return@suspendCancellableCoroutine
				}
				addOnSuccessListener {
					cont.resume(it) {}
				}
				addOnFailureListener {
					cont.resume(null) {}
				}
				addOnCanceledListener {
					cont.cancel()
				}
			}
		}
	}

	companion object {
		val locationPermissions: Array<String> = arrayOf(
			android.Manifest.permission.ACCESS_COARSE_LOCATION,
			android.Manifest.permission.ACCESS_FINE_LOCATION,
		)
	}
}

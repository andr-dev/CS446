package org.uwaterloo.subletr.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.uwaterloo.subletr.R

class NotificationService: INotificationService, FirebaseMessagingService() {
	override fun onMessageReceived(message: RemoteMessage) {
		if (
			ActivityCompat.checkSelfPermission(
				this,
				Manifest.permission.POST_NOTIFICATIONS
			) == PackageManager.PERMISSION_GRANTED && message.notification != null
		) {
			val channel = NotificationChannel(
				MAIN_NOTIFICATION_CHANNEL,
				"Notification",
				NotificationManager.IMPORTANCE_DEFAULT,
			)

			NotificationManagerCompat.from(this).createNotificationChannel(
				channel
			)

			val notification = Notification.Builder(this, MAIN_NOTIFICATION_CHANNEL)
				.setContentTitle(message.notification?.title)
				.setContentText(message.notification?.body)
				.setSmallIcon(R.drawable.ic_launcher)
				.setAutoCancel(true)
				.build()


			NotificationManagerCompat
				.from(this)
				.notify(1, notification)
		}

		super.onMessageReceived(message)
	}

	override fun onNewToken(token: String) {
		// TODO: Figure out what to do here
		super.onNewToken(token)
	}

	companion object {
		const val MAIN_NOTIFICATION_CHANNEL = "MAIN_CHANNEL"
	}
}

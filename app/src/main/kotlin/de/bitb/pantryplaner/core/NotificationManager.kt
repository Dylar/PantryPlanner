package de.bitb.pantryplaner.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import de.bitb.pantryplaner.R

object NotifyManager {

    private const val NOTIFICATION_ID = 66642777
    const val ACTION_REFRESH_PAGE = "de.bitb.pantryplaner.action.refresh"

    fun showNotification(context: Context) {
        val channel = NotificationChannel(
            context.getString(R.string.notification_channel_id),
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        channel.description = context.getString(R.string.notification_channel_description)

        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java)!!
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(context, MainActivity::class.java)
        intent.action = ACTION_REFRESH_PAGE
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder =
            NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Noch alles frisch?")
                .setContentText("Klick hier um deinen Bestand zu aktualisieren")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}


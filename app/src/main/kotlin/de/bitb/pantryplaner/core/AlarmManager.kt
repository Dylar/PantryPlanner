package de.bitb.pantryplaner.core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.usecase.AlertUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

object AlertManager {
    private const val ALARM_REQUEST_CODE = 77723666

    fun setRepeatingAlarm(context: Context) {
        // Get the AlarmManager service
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create a calendar object with the desired alarm time
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, 8) // Set the desired hour
            set(Calendar.MINUTE, 1) // Set the desired minute
        }

        // Create a PendingIntent with the intent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val repeatingInterval =
            if (BuildConfig.FLAVOR == "DEV") AlarmManager.INTERVAL_FIFTEEN_MINUTES
            else AlarmManager.INTERVAL_DAY
        // Set the repeating alarm using AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            repeatingInterval,
            pendingIntent
        )
    }
}

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alertUseCases: AlertUseCases

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val showNotification = alertUseCases.itemAlertUC()
            if (showNotification is Resource.Success && showNotification.data == true) {
                NotifyManager.showNotification(context)
            }
        }
    }
}

package de.bitb.pantryplaner.core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.usecase.AlertUseCases
import java.util.Calendar
import javax.inject.Inject

object AlertManager {
    private const val ALARM_REQUEST_CODE = 77723666

    fun setRepeatingAlarm(context: Context) {
        val isDev = BuildConfig.DEBUG

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        if (isDev) calendar.add(Calendar.MINUTE, 1)
        else calendar.set(Calendar.HOUR_OF_DAY, 8)

        val isNewVersion = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        val pendingIntentFlags =
            if (isNewVersion) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            Intent(context, AlarmReceiver::class.java),
            pendingIntentFlags
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val repeatingInterval =
            if (isDev) AlarmManager.INTERVAL_FIFTEEN_MINUTES
            else AlarmManager.INTERVAL_DAY

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis + repeatingInterval,
            pendingIntent
        )
    }
}

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alertUseCases: AlertUseCases

    override fun onReceive(context: Context, intent: Intent) {
        //TODO fix this page
//        CoroutineScope(Dispatchers.IO).launch {
//            val showNotification = alertUseCases.refreshAlertUC()
//            if (showNotification is Result.Success && showNotification.data == true) {
//                NotifyManager.showNotification(context)
//            }
//        }
    }
}

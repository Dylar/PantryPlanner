package de.bitb.pantryplaner.core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Resource
import de.bitb.pantryplaner.data.SettingsRepository
import de.bitb.pantryplaner.data.model.Settings
import de.bitb.pantryplaner.usecase.AlertUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

object AlertManager {
    private const val ALARM_REQUEST_CODE = 77723666

    fun setRepeatingAlarm(context: Context) {
        val isDev = BuildConfig.FLAVOR == "DEV"

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        if (isDev) calendar.set(Calendar.MINUTE, 1)
        else calendar.set(Calendar.HOUR_OF_DAY, 8)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val repeatingInterval =
            if (isDev) AlarmManager.INTERVAL_FIFTEEN_MINUTES
            else AlarmManager.INTERVAL_DAY

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
    lateinit var settingsRepo: SettingsRepository

    @Inject
    lateinit var alertUseCases: AlertUseCases

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val settings = settingsRepo.getSettings().first()
            if (settings is Resource.Success && settings.data?.refreshAlert == true) {
                val showNotification = alertUseCases.refreshAlertUC()
                if (showNotification is Resource.Success && showNotification.data == true) {
                    NotifyManager.showNotification(context)
                }
            }
        }
    }
}

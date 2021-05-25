package com.example.scheduler.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.scheduler.core.Date
import java.util.*

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            Log.d("DBG","Booted")
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val contentIntent = Intent(context, DailyUpdateReceiver::class.java)
            val contentPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val cal = Date.current().getCalendar()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 1)
            Log.d("DBG", "Daily updater: ${cal.timeInMillis}, ${System.currentTimeMillis()}")
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                contentPendingIntent
            )
        }
    }
}
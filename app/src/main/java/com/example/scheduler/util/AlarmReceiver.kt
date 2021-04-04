package com.example.scheduler.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.scheduler.MainActivity
import com.example.scheduler.R

class AlarmReceiver : BroadcastReceiver() {

  private lateinit var mNotificationManager: NotificationManager

  // Notification channel ID.
  private val PRIMARY_CHANNEL_ID = R.string.default_channel_id.toString()

  override fun onReceive(context: Context, intent: Intent) {
    // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
    Log.d("DBG", "Received")
    mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    deliverNotification(context)
  }

  private fun deliverNotification(context: Context) {
    val contentIntent = Intent(context, MainActivity::class.java)
    val NOTIFICATION_ID = contentIntent.getIntExtra("id", 0)
    val contentPendingIntent = PendingIntent.getActivity(
      context,
      NOTIFICATION_ID,
      contentIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
      .setSmallIcon(R.drawable.circle_toggle_button)
      .setContentTitle("EVENT")
      .setContentText(contentIntent.getStringExtra("event"))
      .setContentIntent(contentPendingIntent)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setDefaults(NotificationCompat.DEFAULT_ALL)
    mNotificationManager.notify(NOTIFICATION_ID, builder.build());
  }
}
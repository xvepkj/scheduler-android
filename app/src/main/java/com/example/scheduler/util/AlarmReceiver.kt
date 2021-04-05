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
import android.R as R1

class AlarmReceiver : BroadcastReceiver()  {
  private lateinit var mNotificationManager: NotificationManager
  private val NOTIFICATION_ID = 0

  override fun onReceive(context: Context, intent: Intent) {
    // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
    Log.d("DBG", "Received")
    mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    deliverNotification(context, intent)
  }

  private fun deliverNotification(context: Context, receivedIntent: Intent) {
    val NOTIFICATION_ID = receivedIntent.getIntExtra("id", 0)

    val PRIMARY_CHANNEL_ID = context.getString(R.string.default_channel_id)
    val contentIntent = Intent(context, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
      context,
      NOTIFICATION_ID,
      contentIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
      .setSmallIcon(R1.drawable.sym_def_app_icon)
      .setContentTitle("EVENT")
      .setContentText(receivedIntent.getStringExtra("event").toString())
      .setContentIntent(contentPendingIntent)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setDefaults(NotificationCompat.DEFAULT_ALL)
    mNotificationManager.notify(NOTIFICATION_ID, builder.build());
  }
}
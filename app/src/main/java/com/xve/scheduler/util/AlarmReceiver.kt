package com.xve.scheduler.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.xve.scheduler.MainActivity
import com.xve.scheduler.R

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
    val eventArray = receivedIntent.getStringArrayExtra("event")
    val builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
      .setLargeIcon(generateBitmapFromVectorDrawable(context,R.drawable.clock))
      .setSmallIcon(R.drawable.ic_baseline_access_time_filled_24)
      .setColor(ContextCompat.getColor(context, R.color.dark_pink))
      .setContentTitle(eventArray?.get(0))
      .setContentText(eventArray?.get(1) + "       " +eventArray?.get(2))
      .setContentIntent(contentPendingIntent)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setDefaults(NotificationCompat.DEFAULT_ALL)
    mNotificationManager.notify(NOTIFICATION_ID, builder.build());
  }
  fun generateBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId) as Drawable
    val bitmap = Bitmap.createBitmap(
      drawable.intrinsicWidth,
      drawable.intrinsicHeight,
      Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
  }
}
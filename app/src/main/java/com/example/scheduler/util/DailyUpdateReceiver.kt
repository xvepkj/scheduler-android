package com.example.scheduler.util
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.scheduler.core.Date
import com.example.scheduler.core.ScheduledEvent
import com.example.scheduler.core.Time
import com.example.scheduler.core.Worker
import io.paperdb.Book
import io.paperdb.Paper
import java.util.*

class DailyUpdateReceiver : BroadcastReceiver() {

  private lateinit var worker: Worker
  private lateinit var history: Book
  private lateinit var context: Context
  private lateinit var pendingIntents: MutableList<PendingIntent>

  override fun onReceive(context: Context, intent: Intent) {
    // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
    Log.d("DBG", "Updating")
    Paper.init(context)

    worker = Paper.book().read("worker")
    history = Paper.book("history")
    this.context = context
    setAlarms()
  }
  fun setAlarms() {
    // set alarms for events of current date
    Log.d("DBG", "Setting alarms")
    // retrieve
    pendingIntents = Paper.book().read("pendingIntents")
    // remove all alarms: for each id in ids, remove alarm
    for (i in pendingIntents.indices){
      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      val contentIntent = Intent(context.applicationContext, AlarmReceiver::class.java)
      val contentPendingIntent = PendingIntent.getBroadcast(
        context.applicationContext,
        i,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
      )
      alarmManager.cancel(contentPendingIntent)
    }
    pendingIntents.clear()

    // Get today's schedule: from history
    val today = Date.current()
    if (!history.contains(today.toString())) {
      history.write(today.toString(), worker.generate(today))
    }
    val todaySchedule: List<ScheduledEvent> = history.read(today.toString())

    Log.d("DBG", "Today's events: $todaySchedule")
    for (i in todaySchedule.indices) {
      val e = todaySchedule[i]
      // for events with start time > current time, set alarms
      if (e.startTime >= Time.now()) setAlarm(todaySchedule[i], i)
    }
    // persist
    Paper.book().write("pendingIntents", pendingIntents)
  }
  private fun setAlarm(event: ScheduledEvent, id: Int) {
    Log.d("DBG", "Event: $event")
    val contentIntent = Intent(context.applicationContext, AlarmReceiver::class.java)
    contentIntent.putExtra("event", event.toString())
    val contentPendingIntent = PendingIntent.getBroadcast(
      context.applicationContext,
      id,
      contentIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )
    pendingIntents.add(contentPendingIntent)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val todayCal = Date.current().getCalendar()
    todayCal.set(Calendar.HOUR_OF_DAY, event.startTime.h)
    todayCal.set(Calendar.MINUTE, event.startTime.m)
    // var triggerTime = System.currentTimeMillis() + t
    val triggerTime = todayCal.timeInMillis
    Log.d("DBG", "alarm after ${(triggerTime - System.currentTimeMillis()) / 1000} seconds")
    alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerTime,1000L, contentPendingIntent)
  }
}
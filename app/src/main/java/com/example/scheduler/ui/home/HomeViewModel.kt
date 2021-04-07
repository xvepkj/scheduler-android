package com.example.scheduler.ui.home

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.scheduler.core.*
import com.example.scheduler.core.Date
import com.example.scheduler.util.DailyUpdateReceiver
import io.paperdb.Book
import io.paperdb.Paper
import java.util.*

class HomeViewModel(val app: Application) : AndroidViewModel(app) {

  val worker: Worker
    get() = Paper.book().read("worker")

  private val history: Book = Paper.book("history")

  private val pendingIntents: MutableList<PendingIntent> = mutableListOf()

  private val _schedule: MutableLiveData<List<ScheduledEvent>> = MutableLiveData()
  val schedule: MutableLiveData<List<ScheduledEvent>>
    get() = _schedule

  init {
    if (!Paper.book().contains("worker")) {
      Paper.book().write("worker", Worker())
    }
    if (!Paper.book().contains("pendingIntents")) {
      Paper.book().write("pendingIntents", mutableListOf<PendingIntent>())
    }
    _schedule.value = mutableListOf()

    if (!Paper.book().contains("initialized")) {
      setupDailyUpdater()
      Paper.book().write("initialized", true)
    }
  }

  fun setupDailyUpdater() {
    val alarmManager = app.getSystemService(ALARM_SERVICE) as AlarmManager
    val contentIntent = Intent(app.applicationContext, DailyUpdateReceiver::class.java)
    val contentPendingIntent = PendingIntent.getBroadcast(
      app.applicationContext,
      0,
      contentIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )

    val cal = Date.current().getCalendar()
    cal.set(Calendar.HOUR_OF_DAY, 19)
    cal.set(Calendar.MINUTE, 5)
    Log.d("DBG", "Daily updater: ${cal.timeInMillis}, ${System.currentTimeMillis()}")
    alarmManager.setRepeating(
      AlarmManager.RTC_WAKEUP,
      cal.timeInMillis,
      AlarmManager.INTERVAL_DAY,
      contentPendingIntent
    )
  }

  fun forceUpdate() {
    val alarmManager = app.getSystemService(ALARM_SERVICE) as AlarmManager
    val contentIntent = Intent(app.applicationContext, DailyUpdateReceiver::class.java)
    val contentPendingIntent = PendingIntent.getBroadcast(
      app.applicationContext,
      0,
      contentIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )

    Log.d("DBG", "Force update")
    alarmManager.setWindow(
      AlarmManager.RTC_WAKEUP,
      System.currentTimeMillis(),
      1000L,
      contentPendingIntent
    )
  }

  fun addToPool (activeTemplate: ActiveTemplate) {
    val w = worker
    w.addToPool(activeTemplate)
    updateWorker(w)
  }

  fun loadSchedule (date : Date) {
    val d = date.toString()
    _schedule.value =
      if (date < Date.current()) {
        // load history
        if (!history.contains(d)) mutableListOf()
        else history.read(d)
       } else if (date == Date.current()) {
         // current day
         if (!history.contains(d)) {
           history.write(d, worker.generate(date))
         }
         history.read(d)
      } else {
        worker.generate(date)
      }
  }
  fun addCustomEvent(e : ScheduledEvent){
    val date = HomeFragment.selecteddate
    val d = date.toString()
    if(date == Date.current()){
      val his : MutableList<ScheduledEvent> = history.read(d)
      Log.d("DBG",his.toString())
      his.add(e)
      history.write(d,his)
      forceUpdate()
    }
    else {
      val template = ScheduleTemplate("")
      template.add(e)
      val activeTemplate = ActiveTemplate(template, false)
      activeTemplate.addDay(date)
      addToPool(activeTemplate)
      Log.d("DBG","TODO")
    }
  }
  fun updateWorker (w: Worker) {
    Paper.book().write("worker", w)
  }

  /*
  fun setAlarms() {
    // set alarms for events of current date

    // remove all alarms: for each id in ids, remove alarm
    val alarmManager = app.getSystemService(ALARM_SERVICE) as AlarmManager
    for (p in pendingIntents) alarmManager.cancel(p)
    pendingIntents.clear()

    // Get today's schedule: from history
    // val todaySchedule: List<ScheduledEvent> = history.read(Date.current().toString())
    val todaySchedule = worker.generate(Date.current()) // for now
    for (i in todaySchedule.indices) {
      val e = todaySchedule[i]
      if (e.startTime > Time.now()) setAlarm(todaySchedule[i], i)
    }
    // for events with start time > current time, set alarms
  }
  private fun setAlarm(event: ScheduledEvent, id: Int) {
    val contentIntent = Intent(app.applicationContext, AlarmReceiver::class.java)
    contentIntent.putExtra("event", event.toString())
    val contentPendingIntent = PendingIntent.getBroadcast(
      app.applicationContext,
      id,
      contentIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )
    pendingIntents.add(contentPendingIntent)
    val alarmManager = app.getSystemService(ALARM_SERVICE) as AlarmManager
    val todayCal = Date.current().getCalendar()
    todayCal.set(Calendar.HOUR_OF_DAY, event.startTime.h)
    todayCal.set(Calendar.MINUTE, event.startTime.m)

    // var triggerTime = System.currentTimeMillis() + t
    val triggerTime = todayCal.timeInMillis
    Log.d("DBG", "alarm after ${(triggerTime - System.currentTimeMillis()) / 1000} seconds")

    alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerTime,1000L, contentPendingIntent)
  }
   */
}
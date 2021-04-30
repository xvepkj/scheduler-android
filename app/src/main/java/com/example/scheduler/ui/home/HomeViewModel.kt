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
  private val extraEvents: Book = Paper.book("extraEvents")

  private var _schedule: MutableLiveData<List<ScheduledEvent>> = MutableLiveData()
  val schedule: MutableLiveData<List<ScheduledEvent>>
    get() = _schedule

  init {
    if (!Paper.book().contains("worker")) {
      Paper.book().write("worker", Worker())
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
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 22)
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
    if (activeTemplate.satisfies(Date.current())) {
      val template : ScheduleTemplate = Paper.book("templates").read(activeTemplate.templatename)
      for (e in template.events) {
        addCustomEvent(e, Date.current())
      }
    }
    val w = worker
    w.addToPool(activeTemplate)
    updateWorker(w)
  }
  fun removeFromPool(index : Int){
    val w = worker
    w.removeFromPool(index)
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
          val s : MutableList<ScheduledEvent> = mutableListOf()
          if (extraEvents.contains(d)) {
            s.addAll(extraEvents.read(d))
            extraEvents.delete(d)
          }
          s.addAll(worker.generate(date))
          s.sortBy { it.startTime }
          history.write(d, s)
          loadEventsToTag(s)
        }
        history.read(d)
      } else {
        val s = mutableListOf<ScheduledEvent>()
        if (extraEvents.contains(d)) {
          val events: MutableList<ScheduledEvent> = extraEvents.read(d)
          for (i in events.indices) events[i].index = i
          s.addAll(events)
        }
        s.addAll(worker.generate(date))
        s.sortBy { it.startTime }
        s
      }
  }

  fun addCustomEvent(e : ScheduledEvent, date : Date){
    val d = date.toString()
    if(date == Date.current()){
      val his : MutableList<ScheduledEvent> = history.read(d)
      Log.d("DBG",his.toString())
      his.add(e)
      his.sortBy { it.startTime }
      history.write(d,his)
      forceUpdate()
      //Statistics Part
      if(e.eventType != EventType.UNTRACKED) {
        val map: MutableMap<Date, Pair<Time, Time>> = Paper.book().read(e.tag)
        var current = Pair(Time(0, 0), Time(0, 0))
        if (map.containsKey(Date.current()))
          current = map[Date.current()]!!
        map[Date.current()] = Pair(current.first,current.second + (e.endTime-e.startTime))
        Log.d("DBG",map.toString())
        Paper.book().write(e.tag,map)
      }
    }
    else {
      /* add to extraEvents */
      val events: MutableList<ScheduledEvent> =
        if (extraEvents.contains(d)) extraEvents.read(d)
        else mutableListOf()
      events.add(e)
      extraEvents.write(d, events)
    }
  }


  fun removeEvent(i : Int, date : Date){
    Log.d("DBG",date.toString())
    val d = date.toString()
    if(date == Date.current()){
      val his : MutableList<ScheduledEvent> = history.read(d)
      val e = his[i]
      his.removeAt(i).toString()
      his.sortBy { it.startTime }
      history.write(d,his)
      forceUpdate()
      //Statistics Part
      if(e.eventType != EventType.UNTRACKED){
        val mapOfTag : MutableMap<Date, Pair<Time, Time>> = Paper.book().read(e.tag)
        val current = mapOfTag[Date.current()]!!
        var donePart = Time(0,0)
        if(e.completed==1)
          donePart = e.endTime - e.startTime
        mapOfTag[Date.current()]=Pair(current.first - donePart,current.second-(e.endTime - e.startTime))
        Log.d("DBG",mapOfTag.toString())
        Paper.book().write(e.tag,mapOfTag)
      }
    }
    else {
      val events: MutableList<ScheduledEvent> = extraEvents.read(d.toString())
      events.removeAt(i)
      extraEvents.write(d.toString(),events)
    }
  }
  fun updateEvent (i : Int,completed_fraction : Int){
    Log.d("DBG", "$i $completed_fraction")
    val his : MutableList<ScheduledEvent> = history.read(Date.current().toString())
    Log.d("DBG", his.toString())
    val event = his[i]
    event.completed=completed_fraction
    Log.d("DBG", event.toString())
    his[i] = event
    history.write(Date.current().toString(),his)
    //Statistics Part
      val mapOfTag : MutableMap<Date, Pair<Time, Time>> = Paper.book().read(event.tag)
      val current = mapOfTag[Date.current()]!!
      if(completed_fraction==0)
        mapOfTag[Date.current()]=Pair(current.first - (event.endTime-event.startTime),current.second)
      else
        mapOfTag[Date.current()]=Pair(current.first + (event.endTime-event.startTime),current.second)
      Log.d("DBG",mapOfTag.toString())
      Paper.book().write(event.tag,mapOfTag)
  }
  fun updateWorker (w: Worker) {
    Paper.book().write("worker", w)
  }

  fun loadEventsToTag(eventList : MutableList<ScheduledEvent>){
    for(event in eventList){
      if(event.eventType != EventType.UNTRACKED) {
        val map: MutableMap<Date, Pair<Time, Time>> = Paper.book().read(event.tag)
        var current = Pair(Time(0, 0), Time(0, 0))
        if (map.containsKey(Date.current()))
          current = map[Date.current()]!!
        map[Date.current()] = Pair(current.first,current.second + (event.endTime-event.startTime))
        Log.d("DBG",map.toString())
        Paper.book().write(event.tag,map)
      }
    }
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
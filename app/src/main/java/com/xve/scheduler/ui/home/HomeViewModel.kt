package com.xve.scheduler.ui.home

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xve.scheduler.core.*
import com.xve.scheduler.core.Date
import com.xve.scheduler.util.DailyUpdateReceiver
import io.paperdb.Book
import io.paperdb.Paper
import java.util.*
import kotlin.math.round


class HomeViewModel(val app: Application) : AndroidViewModel(app) {

  val worker: Worker
    get() = Paper.book().read("worker")

  private val history: Book = Paper.book("history")
  private val extraEvents: Book = Paper.book("extraEvents")

  var logged_event_index: Int = -1

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
    cal.set(Calendar.MINUTE, 0)
    Log.d("DBG", "Daily updater: ${cal.timeInMillis}, ${System.currentTimeMillis()}")
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      alarmManager.setExactAndAllowWhileIdle(
              AlarmManager.RTC_WAKEUP,
              cal.timeInMillis,
              contentPendingIntent
      )
    }
    else {
      alarmManager.setExact(
              AlarmManager.RTC_WAKEUP,
              cal.timeInMillis,
              contentPendingIntent
      )
    }
  }

  fun forceUpdate() {
    val alarmManager = app.getSystemService(ALARM_SERVICE) as AlarmManager
    val contentIntent = Intent(app.applicationContext, DailyUpdateReceiver::class.java)
    contentIntent.putExtra("type",2)
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

  fun addToPool(activeTemplate: ActiveTemplate) {
    if (activeTemplate.satisfies(Date.current())) {
      val template : ScheduleTemplate = Paper.book("templates").read(activeTemplate.templatename)
      for (e in template.events)
        addCustomEvent(e, Date.current())
    }
    val w = worker
    w.addToPool(activeTemplate)
    updateWorker(w)
  }
  fun removeFromPool(index: Int){
    val w = worker
    w.removeFromPool(index)
    updateWorker(w)
  }
  fun loadSchedule(date: Date) {
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

  fun addCustomEvent(e: ScheduledEvent, date: Date){
    val d = date.toString()
    if(date == Date.current()){
      val his : MutableList<ScheduledEvent> = history.read(d)
      Log.d("DBG", his.toString())
      his.add(e)
      his.sortBy { it.startTime }
      history.write(d, his)
      forceUpdate()
      //Statistics Part
      if(e.eventType != EventType.UNTRACKED) {
        val statList: MutableList<MutableMap<Date, Pair<Long, Long>>> = Paper.book("stats").read("list")
        val map: MutableMap<Date, Pair<Long, Long>> = statList[e.tagId]
        var current = Pair(0L, 0L)
        if (map.containsKey(Date.current()))
          current = map[Date.current()]!!
        map[Date.current()] = Pair(
          current.first,
          current.second + (e.endTime.toMillis() - e.startTime.toMillis())
        )
        Log.d("DBG", map.toString())
        Paper.book("stats").write("list", statList)
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


  fun removeEvent(i: Int, date: Date){
    Log.d("DBG", date.toString())
    val d = date.toString()
    if(date == Date.current()){
      val his : MutableList<ScheduledEvent> = history.read(d)
      val e = his[i]
      his.removeAt(i).toString()
      his.sortBy { it.startTime }
      history.write(d, his)
      forceUpdate()
      //Statistics Part
      if(e.eventType != EventType.UNTRACKED){
        val statList: MutableList<MutableMap<Date, Pair<Long, Long>>> = Paper.book("stats").read("list")
        val mapOfTag : MutableMap<Date, Pair<Long, Long>> = statList[e.tagId]
        val current = mapOfTag[Date.current()]!!
        val donePart =
          if (e.eventType == EventType.LOGGED) {
            ((e.endTime.toMillis() - e.startTime.toMillis()) * e.log_progress).toLong()
          } else {
            (e.endTime.toMillis() - e.startTime.toMillis()) * e.completed
          }
        mapOfTag[Date.current()]=Pair(
          current.first - donePart,
          current.second - (e.endTime.toMillis() - e.startTime.toMillis())
        )
        Log.d("DBG", mapOfTag.toString())
        Paper.book("stats").write("list", statList)
      }
    }
    else {
      val events: MutableList<ScheduledEvent> = extraEvents.read(d)
      events.removeAt(i)
      extraEvents.write(d, events)
    }
  }
  fun updateEvent(i: Int, completed_fraction: Int){
    Log.d("DBG", "$i $completed_fraction")
    val his : MutableList<ScheduledEvent> = history.read(Date.current().toString())
    Log.d("DBG", his.toString())
    val event = his[i]
    event.completed=completed_fraction
    Log.d("DBG", event.toString())
    his[i] = event
    history.write(Date.current().toString(), his)
    //Statistics Part

      val statList: MutableList<MutableMap<Date, Pair<Long, Long>>> = Paper.book("stats").read("list")
      val mapOfTag : MutableMap<Date, Pair<Long, Long>> = statList[event.tagId]
      val current = mapOfTag[Date.current()]!!
      if(completed_fraction==0)
        mapOfTag[Date.current()]=Pair(
          current.first - (event.endTime.toMillis() - event.startTime.toMillis()),
          current.second
        )
      else
        mapOfTag[Date.current()]=Pair(
          current.first + (event.endTime.toMillis() - event.startTime.toMillis()),
          current.second
        )
      Log.d("DBG", mapOfTag.toString())
      Paper.book("stats").write("list", statList)
  }


  /* For retrieving logged event */
  fun getLoggedEvent() =
    (history.read(Date.current().toString()) as MutableList<ScheduledEvent>)[logged_event_index]

  /* For logged events */
  fun updateLoggedEventProgress(progress: Double) {
    val his : MutableList<ScheduledEvent> = history.read(Date.current().toString())
    Log.d("DBG", his.toString())
    val event = his[logged_event_index]
    val progress_change = progress - event.log_progress
    event.log_progress = progress
    Log.d("DBG", event.toString())
    his[logged_event_index] = event
    history.write(Date.current().toString(), his)
    //Statistics Part

    val statList: MutableList<MutableMap<Date, Pair<Long, Long>>> = Paper.book("stats").read("list")
    val mapOfTag : MutableMap<Date, Pair<Long, Long>> = statList[event.tagId]
    val current = mapOfTag[Date.current()]!!
    // TODO: Store statistics as long/long instead of time/time?
    mapOfTag[Date.current()]=Pair(
      current.first + round((event.endTime.toMillis() - event.startTime.toMillis()) * progress_change).toLong(),
      current.second
    )
    Log.d("DBG", mapOfTag.toString())
    Paper.book("stats").write("list", statList)
  }

  fun updateWorker(w: Worker) = Paper.book().write("worker", w)


  fun loadEventsToTag(eventList: MutableList<ScheduledEvent>){
    for(event in eventList){
      if(event.eventType != EventType.UNTRACKED) {

        val statList: MutableList<MutableMap<Date, Pair<Long, Long>>> = Paper.book("stats").read("list")
        val map: MutableMap<Date, Pair<Long, Long>> = statList[event.tagId]
        var current = Pair(0L, 0L)
        if (map.containsKey(Date.current()))
          current = map[Date.current()]!!
        map[Date.current()] = Pair(
          current.first,
          current.second + (event.endTime.toMillis() - event.startTime.toMillis())
        )
        Log.d("DBG", map.toString())
        Paper.book("stats").write("list", statList)
      }
    }
  }
}
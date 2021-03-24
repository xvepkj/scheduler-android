package com.example.scheduler.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scheduler.core.*

class HomeViewModel : ViewModel() {
  private val _worker: Worker = Worker()
  val worker: Worker
    get() = _worker

  private val _schedule: MutableLiveData<List<ScheduledEvent>> = MutableLiveData()
  val schedule: MutableLiveData<List<ScheduledEvent>>
    get() = _schedule

  init {
    _schedule.value = mutableListOf()

    val templateSingle1 = ScheduleTemplate()
    val templateSingle2 = ScheduleTemplate()
    templateSingle1.add(ScheduledEvent("single1", Time(10, 0), Time(11, 0)))
    templateSingle2.add(ScheduledEvent("single2", Time(10, 0), Time(11, 0)))

    val activeTemplate1 = ActiveTemplate(templateSingle1, true)
    val activeTemplate2 = ActiveTemplate(templateSingle2, false)
    activeTemplate1.setRepeatCriteria(
      RepeatCriteria(
        Date(24,3,2021),
        RepeatType.FREQUENCY,
        mutableListOf(2)
      )
    )
    activeTemplate2.addDay(Date(25, 3, 2021))
    activeTemplate2.addDay(Date(26, 3, 2021))

    worker.addToPool(activeTemplate1)
    worker.addToPool(activeTemplate2)
  }

  fun addToPool (activeTemplate: ActiveTemplate) {
    worker.addToPool(activeTemplate)
  }

  fun loadSchedule (d : Date) {
    _schedule.value = _worker.generate(d)
  }
}
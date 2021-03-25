package com.example.scheduler.ui.templates.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scheduler.core.ScheduleTemplate
import com.example.scheduler.core.ScheduledEvent
import com.example.scheduler.core.Time

class TemplateViewModel : ViewModel() {
  private val _templates : MutableLiveData<MutableList<ScheduleTemplate>> = MutableLiveData()
  val templates: LiveData<MutableList<ScheduleTemplate>>
    get() = _templates

  init {
    _templates.value = mutableListOf()

    val template1 = ScheduleTemplate()
    val template2 = ScheduleTemplate()
    template1.add(ScheduledEvent("t1-a", Time(0, 0), Time(1, 0)))
    template1.add(ScheduledEvent("t1-b", Time(1, 30), Time(2, 30)))
    template1.add(ScheduledEvent("t1-c", Time(4, 0), Time(4, 30)))

    template2.add(ScheduledEvent("t2-a", Time(6, 0), Time(6, 30)))
    template2.add(ScheduledEvent("t2-b", Time(7, 0), Time(8, 0)))
    _templates.value!!.add(template1)
    _templates.value!!.add(template2)
  }

  fun addTemplate (template: ScheduleTemplate) {
    _templates.value?.add(template)
  }
}
package com.example.scheduler.ui.templates.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scheduler.core.ScheduleTemplate
import com.example.scheduler.core.ScheduledEvent

class TemplateAddViewModel : ViewModel() {
  // TODO: Implement the ViewModel
  private var _template : MutableLiveData<ScheduleTemplate> = MutableLiveData()
  val template: LiveData<ScheduleTemplate>
    get() = _template

  init {
    _template.value = ScheduleTemplate()
  }

  fun addEvent (event: ScheduledEvent) {
    val tmp = _template.value
    tmp?.add(event)
    _template.value = tmp
  }

  fun removeEvent (index: Int) {
    _template.value?.remove(index)
  }

  fun clear() {
    _template.value = ScheduleTemplate()
  }
}
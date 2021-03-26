package com.example.scheduler.ui.templates.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scheduler.core.ScheduleTemplate
import com.example.scheduler.core.ScheduledEvent

class TemplateAddViewModel : ViewModel() {

  private var _events : MutableLiveData<MutableList<ScheduledEvent>> = MutableLiveData(mutableListOf())
  val events get() = _events

  fun clear() {
    _events.value?.clear()
  }
}
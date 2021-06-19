package com.xve.scheduler.ui.templates.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xve.scheduler.core.ScheduledEvent

class TemplateAddViewModel : ViewModel() {
  var template_name : String = ""
  private var _events : MutableLiveData<MutableList<ScheduledEvent>> = MutableLiveData(mutableListOf())
  val events get() = _events

  fun clear() {
    template_name = ""
    _events.value?.clear()
  }
}
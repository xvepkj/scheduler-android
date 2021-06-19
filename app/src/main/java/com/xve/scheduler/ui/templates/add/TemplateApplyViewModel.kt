package com.xve.scheduler.ui.templates.add

import androidx.lifecycle.ViewModel
import com.xve.scheduler.core.Date
import com.xve.scheduler.core.ScheduleTemplate

class TemplateApplyViewModel : ViewModel() {
  var template = ScheduleTemplate("test")
  var customDatesList: MutableList<Date> = mutableListOf()
}
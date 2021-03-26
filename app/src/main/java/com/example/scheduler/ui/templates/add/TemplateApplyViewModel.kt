package com.example.scheduler.ui.templates.add

import androidx.lifecycle.ViewModel
import com.example.scheduler.core.ActiveTemplate
import com.example.scheduler.core.Date
import com.example.scheduler.core.ScheduleTemplate

class TemplateApplyViewModel : ViewModel() {
  var template = ScheduleTemplate()
  var customDatesList: MutableList<Date> = mutableListOf()
}
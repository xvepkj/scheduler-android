package com.example.scheduler.core

class ActiveTemplate(val template: ScheduleTemplate, val repeats: Boolean) {
  private lateinit var repeatCriteria: RepeatCriteria
  private var daySelection: MutableList<String> = mutableListOf<String>()

  // For repeat only
  fun setRepeatCriteria (r: RepeatCriteria) {
    repeatCriteria = r
  }

  // For selection only
  fun addDay(d : String) {
    // TODO
  }

  // For selection only
  fun removeDay(d : String) {
    // TODO
  }
}
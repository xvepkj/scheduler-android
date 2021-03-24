package com.example.scheduler.core

import java.time.Duration
import java.util.*

class Worker {
  private var pool: MutableList<ActiveTemplate> = mutableListOf()

  fun generate (date: Date): List<ScheduledEvent> {
    val res : MutableList<ScheduledEvent> = mutableListOf()
    for(at in pool){
      if(existintemplate(at,date)){
        res.addAll(at.template.events)
      }
    }
    //TODO Sorting
    return mutableListOf()
  }

  private fun existintemplate(at: ActiveTemplate,date:Date): Boolean {
    if(at.repeats) {
      if(date < at.repeatCriteria.startDate) return false
      if (at.repeatCriteria.repeatType == RepeatType.WEEKLY) {
        val cal : Calendar = Calendar.getInstance()
        cal.set(date.year,date.month,date.day)
        val requiredDay = cal.get(Calendar.DAY_OF_WEEK)
        if(at.repeatCriteria.list.contains(requiredDay))
          return true
      } else if (at.repeatCriteria.repeatType == RepeatType.MONTHLY) {
        return at.repeatCriteria.list.contains(date.day)
      } else if (at.repeatCriteria.repeatType == RepeatType.FREQUENCY) {
        val freq : Long = at.repeatCriteria.list[0].toLong()
        val diff= Date.difference(date,at.repeatCriteria.startDate)
        if(diff % freq == 0L)
          return true
      }
    }
    else {
        val dateIndex = at.daySelection.indexOf(date)
        return (dateIndex!=-1)
    }
    return TODO()
  }

  fun addToPool (activeTemplate: ActiveTemplate) {
    pool.add(activeTemplate)
  }

  fun removeFromPool (index: Int) {
    pool.removeAt(index)
  }
}
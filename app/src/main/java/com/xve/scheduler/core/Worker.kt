package com.xve.scheduler.core

import io.paperdb.Paper

class Worker {

  private var pool: MutableList<ActiveTemplate> = mutableListOf()

  fun generate (date: Date): List<ScheduledEvent> {
    val res : MutableList<ScheduledEvent> = mutableListOf()
    for(at in pool){
      val template : ScheduleTemplate = Paper.book("templates").read(at.templatename)
      if(at.satisfies(date)) res.addAll(template.events)}
    //TODO Sorting
    return res
  }

  fun addToPool (activeTemplate: ActiveTemplate) {
    pool.add(activeTemplate)
  }

  fun removeFromPool (index: Int) {
    pool.removeAt(index)
  }

  fun getPool(): List<ActiveTemplate> = pool
}
package com.example.scheduler.core

class Worker {
  private var pool: MutableList<ActiveTemplate> = mutableListOf()

  fun generate (date: Date): List<ScheduledEvent> {
    val res : MutableList<ScheduledEvent> = mutableListOf()
    for(at in pool){
      if(at.satisfies(date)) res.addAll(at.template.events)
    }
    //TODO Sorting
    return res
  }

  fun addToPool (activeTemplate: ActiveTemplate) {
    pool.add(activeTemplate)
  }

  fun removeFromPool (index: Int) {
    pool.removeAt(index)
  }
}
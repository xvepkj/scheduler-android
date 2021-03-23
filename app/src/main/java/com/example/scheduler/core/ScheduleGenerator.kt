package com.example.scheduler.core

class Worker {
  private var pool: MutableList<ActiveTemplate> = mutableListOf()

  fun generate (date: Date): List<ScheduledEvent> {
    // TODO
    return mutableListOf()
  }

  fun addToPool (activeTemplate: ActiveTemplate) {
    pool.add(activeTemplate)
  }

  fun removeFromPool (index: Int) {
    pool.removeAt(index)
  }
}
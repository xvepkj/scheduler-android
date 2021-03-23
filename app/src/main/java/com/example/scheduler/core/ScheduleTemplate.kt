package com.example.scheduler.core

class ScheduleTemplate {
  private val events: MutableList<ScheduledEvent> = mutableListOf<ScheduledEvent>()

  fun add (event: ScheduledEvent) {
    events.add(event)
    // TODO: Validation, events should be non-intersecting
  }

  fun remove (index: Int) {
    events.removeAt(index)
    // TODO: Validation of index
  }

  fun clone(): ScheduleTemplate {
    // TODO
    return ScheduleTemplate()
  }
}
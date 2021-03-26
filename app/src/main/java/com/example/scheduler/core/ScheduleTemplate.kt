package com.example.scheduler.core

class ScheduleTemplate (val name: String){
  private val _events: MutableList<ScheduledEvent> = mutableListOf<ScheduledEvent>()
  val events : List<ScheduledEvent>
    get() = _events
  fun add (event: ScheduledEvent) {
    _events.add(event)
    // TODO: Validation, events should be non-intersecting
  }

  fun remove (index: Int) {
    _events.removeAt(index)
    // TODO: Validation of index
  }

  fun clone(cloneName: String): ScheduleTemplate {
    // TODO
    return ScheduleTemplate(cloneName)
  }

  override fun toString() = events.toString()
}
package com.example.scheduler.core

class ActiveTemplate(val templatename: String, val repeats: Boolean) {
  private lateinit var _repeatCriteria: RepeatCriteria
  private var _daySelection: MutableList<Date> = mutableListOf<Date>()
  val repeatCriteria: RepeatCriteria
    get() = _repeatCriteria
  val daySelection:List<Date>
    get() = _daySelection
  // For repeat only
  fun setRepeatCriteria (r: RepeatCriteria) {
    _repeatCriteria = r
  }

  fun satisfies (date: Date): Boolean {
    return if (repeats)
      repeatCriteria.satisfies(date)
    else daySelection.indexOf(date) != -1
  }

  // For selection only
  fun addDay(d : Date) {
    _daySelection.add(d)
    // TODO Validation
  }
  // For selection only
  fun removeDay(d : Date) {
    for(i in 0 until daySelection.size)
      if(daySelection[i] ==d) {
        _daySelection.removeAt(i)
        break
      }
    // TODO
  }
  override fun toString() = "$templatename,$repeats,${if (repeats) repeatCriteria else ""},$daySelection"
}
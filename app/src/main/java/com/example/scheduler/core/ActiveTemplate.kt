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
  override fun toString() : String {
    var output: String = templatename.toString() + "\n"
    if (repeats) {
      output += repeatCriteria.repeatType.toString() + "\n"
      output += "Start: " + repeatCriteria.startDate.toString() + "\n"
      if (repeatCriteria.repeatType == RepeatType.WEEKLY) {
        val weekdayList : MutableList<String> = mutableListOf()
        for(day in repeatCriteria.list.indices){
          weekdayList.add(
          when(day){
            0 -> "Sa"
            1 -> "Su"
            2 -> "Mo"
            3 -> "Tu"
            4 -> "We"
            5 -> "Th"
            6 -> "Fr"
            else -> "?"
          })
        }
        output += "Weekdays: $weekdayList"
      } else if (repeatCriteria.repeatType == RepeatType.MONTHLY) {
        output += "Dates: " + repeatCriteria.list
      }
      else {
        output += "Frequency: ${repeatCriteria.list[0]}"
      }
    }
    else {
      output += "CUSTOM SELECTION" + "\n"
      output += "Dates: $daySelection"
    }
    return output
  }
}
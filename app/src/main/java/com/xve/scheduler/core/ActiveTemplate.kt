package com.xve.scheduler.core

class ActiveTemplate(val templatename: String, val repeats: Boolean) {
  private lateinit var _repeatCriteria: RepeatCriteria
  private var _daySelection: MutableList<Date> = mutableListOf()

  private val repeatCriteria: RepeatCriteria
    get() = _repeatCriteria
  private val daySelection:List<Date>
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
    for(i in daySelection.indices)
      if(daySelection[i] ==d) {
        _daySelection.removeAt(i)
        break
      }
    // TODO
  }
  override fun toString() : String {
    var output: String = templatename.addNewLine()
    if (repeats) {
      output += repeatCriteria.repeatType.toString().addNewLine()
      output += "Start: " + repeatCriteria.startDate.toString().addNewLine()
      output += when (repeatCriteria.repeatType) {
        RepeatType.WEEKLY -> {
          val weekdayList : MutableList<String> = mutableListOf()
          for(day in repeatCriteria.list)
            weekdayList.add(getWeekDayString(day))

          "Weekdays: $weekdayList"
        }
        RepeatType.MONTHLY -> "Dates: " + repeatCriteria.list
        else -> "Frequency: ${repeatCriteria.list[0]}"
      }
    }
    else output += "CUSTOM SELECTION".addNewLine() + "Dates: $daySelection"

    return output
  }

  fun String.addNewLine() = this + "\n"

  fun getWeekDayString(day : Int) = when(day){
    1 -> "Su"
    2 -> "Mo"
    3 -> "Tu"
    4 -> "We"
    5 -> "Th"
    6 -> "Fr"
    7 -> "Sa"
    else -> "?"
  }
}
package com.xve.scheduler.core

import java.util.*

class RepeatCriteria (val startDate: Date,
                      val repeatType: RepeatType,
                      val list: MutableList<Int>){
  init {
    // TODO: Validate
    // weekly repeat - days (S, M, T, W, etc.) 0-7
    // monthly repeat - days (1,...,31)
    // frequency - every x days
  }

  fun satisfies (date: Date): Boolean {
    if (date < startDate) return false
    return when (repeatType) {
      RepeatType.WEEKLY -> list.contains(date.getCalendar().get(Calendar.DAY_OF_WEEK))
      RepeatType.FREQUENCY -> Date.difference(date, startDate) % list[0] == 0
      RepeatType.MONTHLY -> list.contains(date.day)
    }
  }

  override fun toString() = "(start:$startDate, $repeatType, $list)"
}
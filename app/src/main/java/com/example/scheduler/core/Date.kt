package com.example.scheduler.core

import java.util.*
import kotlin.math.abs

class Date (val day: Int, val month: Int, val year: Int){

  init {
    // TODO Validation
  }

  override fun toString(): String = "%02d-%02d-%04d".format(day, month, year)

  operator fun compareTo(other :Date) : Int {
    val t = listOf(year - other.year, month - other.month, day - other.day)
    for (i in t) {
      if (i != 0) return i / abs(i)
    }
    return 0
  }

  override operator fun equals(other: Any?) =
    when (other) {
      is Date -> Triple(other.day, other.month, other.year) == Triple(day, month, year)
      else -> false
    }

  fun getCalendar() : Calendar{
    val cal = Calendar.getInstance()
    cal.set(year,month-1,day)
    cal.set(Calendar.MILLISECOND,0)
    cal.set(Calendar.HOUR,0)
    cal.set(Calendar.MINUTE,0)
    cal.set(Calendar.SECOND,0)
    return cal
  }

  override fun hashCode(): Int {
    var result = day
    result = 31 * result + month
    result = 31 * result + year
    return result
  }

  companion object {
    val millisInDay : Long = 24 * 60 * 60 * 1000
    fun difference(d1 : Date,d2 : Date) : Int {
      val t1 = d1.getCalendar().timeInMillis
      val t2 = d2.getCalendar().timeInMillis
      val diff = abs(t1 - t2)
      assert(diff % millisInDay == 0L) // for local testing only
      return (diff / millisInDay).toInt()
    }
  }
}
package com.example.scheduler.core

import java.util.*
import kotlin.math.abs

class Date (val day: Int, val month: Int, val year: Int){
  override fun toString(): String = "$day-$month-$year"
  public fun getWeekday(){

  }
  operator fun compareTo(other :Date) : Int{
    if(year < other.year)return -1
    if(year > other.year)return 1
    if(month < other.month)return -1
    if(month > other.month)return 1
    if(day < other.day)return -1
    if(day > other.day)return 1
    return 0
  }
  fun getCalendar() : Calendar{
    val cal = Calendar.getInstance()
    cal.set(year,month,day)
    cal.set(Calendar.MILLISECOND,0)
    cal.set(Calendar.HOUR,0)
    cal.set(Calendar.MINUTE,0)
    cal.set(Calendar.SECOND,0)
    return cal
  }
  companion object {
    fun difference(d1 : Date,d2 : Date) : Long{
      val t1 = d1.getCalendar().timeInMillis
      val t2 = d2.getCalendar().timeInMillis
      return abs(t1-t2) /(24*60*60*1000)

    }
  }
}
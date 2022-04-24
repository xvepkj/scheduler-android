package com.xve.scheduler.core

import java.util.*

class Time (val h: Int, val m: Int) : Comparable<Time> {

  override fun toString()= "%02d:%02d".format(h, m)

  override operator fun compareTo(other: Time):Int{
    return when {
        other.h > h -> -1
        other.h < h -> 1
        else -> {
          when {
            other.m > m -> -1
            other.m < m -> 1
            else -> 0
          }
        }
    }
  }

  companion object {
    fun now(): Time {
      val cal = Calendar.getInstance()
      return Time(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }

    fun timeFromMillis(ms: Long): Time {
      val minutes = ms / 60000L
      return Time((minutes / 60).toInt(), (minutes % 60).toInt())
    }
  }

  fun toMillis() = (h * 3600 + m * 60) * 1000L
}
package com.example.scheduler.core

import java.lang.Math.round
import java.util.*

class Time (val h: Int, val m: Int) : Comparable<Time> {
  override fun toString()= "%02d:%02d".format(h, m)

  override operator fun compareTo(other: Time):Int{
    return if(other.h > h) -1
    else if(other.h < h) 1
    else {
      if(other.m > m) -1
      else if(other.m < m) 1
      else 0
    }
  }

  companion object {
    fun now(): Time {
      val cal = Calendar.getInstance()
      return Time(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }

    fun timeFromMillis(ms: Long): Time {
      val x = ms / 1000000L
      return Time((x / 60).toInt(), (x % 60).toInt())
    }
  }

  fun toMillis() : Long {
    return (h * 3600 + m * 60) * 1000L
  }
}
package com.example.scheduler.core

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
    operator fun plus(other : Time) : Time{
        return  Time(h + other.h + (m + other.m)/60, (m + other.m)% 60)
    }
    operator fun minus(other: Time) : Time{
        val totalMinutes = h*60 + m - other.h*60 - other.m
        return Time(totalMinutes/60,totalMinutes%60)
    }
    companion object {
        fun now(): Time {
            val cal = Calendar.getInstance()
            return Time(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        }
    }
}
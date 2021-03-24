package com.example.scheduler.core

class Time(val h: Int, val m: Int) {
    override fun toString()= "$h:$m"
    operator fun compareTo(other: Time):Int{
        return if(other.h > h) -1
        else if(other.h < h) 1
        else {
            if(other.m > m) -1
            else if(other.m < m) 1
            else 0
        }
    }
}
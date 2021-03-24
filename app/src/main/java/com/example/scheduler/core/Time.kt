package com.example.scheduler.core

class Time(val h: Int, val m: Int) {
    override fun toString()= "%02d:%02d".format(h, m)
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
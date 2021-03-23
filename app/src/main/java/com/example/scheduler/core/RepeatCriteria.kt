package com.example.scheduler.core

class RepeatCriteria (val repeatType: RepeatType, val list: MutableList<Int>){
  init {
    // TODO: Validate
    // weekly repeat - days (M, T, W, etc.) 0-7
    // monthly repeat - days (1,...,31)
    // frequency - every x days
  }
}
package com.example.scheduler.core

class ScheduledEvent (
    var name: String,
    var startTime: Time,
    var endTime: Time
){
    init{
        //TODO Validation
    }

    override fun toString() = "($name, $startTime, $endTime)"

}
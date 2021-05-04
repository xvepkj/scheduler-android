package com.example.scheduler.core

class ScheduledEvent (
    var name: String,
    var startTime: Time,
    var endTime: Time,
    var eventType: EventType,
    var tag : String
){
    init{
        //TODO Validation
    }
    override fun toString() = "($name, $startTime, $endTime)"

    /* Extra info */
   var index = -1
   var completed = 0
   var log_progress = 0.0
}
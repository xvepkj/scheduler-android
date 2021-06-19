package com.xve.scheduler.core

class ScheduledEvent (
    var name: String,
    var startTime: Time,
    var endTime: Time,
    var eventType: EventType,
    var tagId : Int
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
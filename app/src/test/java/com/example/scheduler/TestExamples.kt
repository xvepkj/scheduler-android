package com.example.scheduler

import com.example.scheduler.core.*
import org.junit.Before
import org.junit.Test

class TestExamples {

  private var template1 = ScheduleTemplate()
  private var template2 = ScheduleTemplate()
  private var templateSingle1 = ScheduleTemplate()
  private var templateSingle2 = ScheduleTemplate()


  @Before
  fun init() {
    template1.add(ScheduledEvent("t1-a", Time(0, 0), Time(1, 0)))
    template1.add(ScheduledEvent("t1-b", Time(1, 30), Time(2, 30)))
    template1.add(ScheduledEvent("t1-c", Time(4, 0), Time(4, 30)))

    template2.add(ScheduledEvent("t2-a", Time(6, 0), Time(6, 30)))
    template2.add(ScheduledEvent("t2-b", Time(7, 0), Time(8, 0)))
    template2.add(ScheduledEvent("t2-c", Time(9, 0), Time(9, 30)))

    templateSingle1.add(ScheduledEvent("single1", Time(10, 0), Time(11, 0)))
    templateSingle2.add(ScheduledEvent("single2", Time(10, 0), Time(11, 0)))
  }

  @Test
  fun test1() {
    val activeTemplate = ActiveTemplate(template1, false)
    activeTemplate.addDay(Date(25, 3, 2021))
    activeTemplate.addDay(Date(27, 3, 2021))
    activeTemplate.addDay(Date(28, 3, 2021))

    val worker = Worker()
    worker.addToPool(activeTemplate)
    val schedule = worker.generate(Date(25, 3, 2021))
    println(schedule)
  }

  @Test
  fun test2() {
    // Simple Frequency test
    val at1 = ActiveTemplate(template1, true)
    at1.setRepeatCriteria(RepeatCriteria(Date(24, 3, 2021), RepeatType.FREQUENCY, mutableListOf(2)))

    val at2 = ActiveTemplate(template2, true)
    at2.setRepeatCriteria(RepeatCriteria(Date(24, 3, 2021), RepeatType.FREQUENCY, mutableListOf(3)))

    val worker = Worker()
    worker.addToPool(at1)
    worker.addToPool(at2)

    val schedule1 = worker.generate(Date(24, 3, 2021))
    println(schedule1)
    assert(schedule1.size == 6)

    val schedule2 = worker.generate(Date(26, 3, 2021))
    println(schedule2)
    assert(schedule2.size == 3)

    val schedule3 = worker.generate(Date(30, 3, 2021))
    println(schedule3)
    assert(schedule3.size == 6)
  }

  @Test
  fun test3() {
    // Simple Frequency test
    val at1 = ActiveTemplate(template1, true)
    at1.setRepeatCriteria(RepeatCriteria(Date(24, 3, 2021), RepeatType.WEEKLY, mutableListOf(1, 2, 3)))

    val at2 = ActiveTemplate(template2, true)
    at2.setRepeatCriteria(RepeatCriteria(Date(24, 3, 2021), RepeatType.WEEKLY, mutableListOf(2, 4, 6)))

    val worker = Worker()
    worker.addToPool(at1)
    worker.addToPool(at2)

    val schedule1 = worker.generate(Date(24, 3, 2021))
    println(schedule1)
    assert(schedule1.size == 3)

    val schedule2 = worker.generate(Date(25, 3, 2021))
    println(schedule2)
    assert(schedule2.size == 0)

    val schedule3 = worker.generate(Date(26, 3, 2021))
    println(schedule3)
    assert(schedule3.size == 3)

    val schedule4 = worker.generate(Date(27, 3, 2021))
    println(schedule4)
    assert(schedule4.size == 0)

    val schedule5 = worker.generate(Date(28, 3, 2021))
    println(schedule5)
    assert(schedule5.size == 3)

    val schedule6 = worker.generate(Date(29, 3, 2021))
    println(schedule6)
    assert(schedule6.size == 6)

    val schedule7 = worker.generate(Date(30, 3, 2021))
    println(schedule7)
    assert(schedule7.size == 3)
  }
}
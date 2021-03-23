import com.example.scheduler.core.*
import org.junit.Test

class TestExamples {

  @Test
  fun sampleTest() {
    // Creating events
    var a = ScheduledEvent("event_1", 1, 101)
    var b = ScheduledEvent("event_2", 120, 200)

    // Creating template
    var template = ScheduleTemplate()
    template.add(a)
    template.add(b)

    // Creating active templates
    var repeatingActiveTemplate = ActiveTemplate(template, true)
    var selectionActiveTemplate = ActiveTemplate(template, false)

    // setting
    var repeatCriteria = RepeatCriteria(RepeatType.FREQUENCY, mutableListOf(1))
    repeatingActiveTemplate.setRepeatCriteria(repeatCriteria)

    selectionActiveTemplate.addDay("23-03-2021")
    selectionActiveTemplate.addDay("24-03-2021")
    selectionActiveTemplate.addDay("25-03-2021")

    val worker = Worker()
    worker.addToPool(selectionActiveTemplate)
    worker.addToPool(repeatingActiveTemplate)

    val todaySchedule = worker.generate(Date(23, 3, 2021))
    println(todaySchedule.toString())
  }


}
package com.xve.scheduler.ui.Statistics

import androidx.lifecycle.ViewModel
import com.xve.scheduler.core.Date
import com.xve.scheduler.core.Tag
import com.xve.scheduler.core.Time
import io.paperdb.Paper

class StatisticsViewModel : ViewModel() {

  val tags : MutableList<Tag>
    get() = Paper.book("tags").read("list")

  fun loadStatistics(index: Int, numDays: Int = -1): Pair<String,String> {
    val statList: MutableList<Map<Date, Pair<Long, Long>>> = Paper.book("stats").read("list")
    val statsMap: Map<Date, Pair<Long, Long>> = statList[index]
    var doneTime = 0L
    var totalTime = 0L
    for(date in statsMap.keys){
      if (numDays != -1 && Date.difference(Date.current(), date) >= numDays) continue
      doneTime += statsMap[date]!!.first
      totalTime += statsMap[date]!!.second
    }

    return Pair(tags[index].name,getStatisticsDisplayString(Time.timeFromMillis(doneTime), Time.timeFromMillis(totalTime)))
  }

  private fun getStatisticsDisplayString(done : Time, total : Time) =
    "${done.h.toString() + "h" + done.m.toString() + "m"} / ${total.h.toString() + "h" + total.m.toString() + "m"}"
}
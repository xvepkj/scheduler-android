package com.example.scheduler.ui.Statistics

import androidx.lifecycle.ViewModel
import com.example.scheduler.core.Date
import com.example.scheduler.core.Tag
import com.example.scheduler.core.Time
import io.paperdb.Paper

class StatisticsViewModel : ViewModel() {
  // TODO: Implement the ViewModel
  val tags : MutableList<Tag>
    get() = Paper.book("tags").read("list")

  fun getTagIds() : List<Int> {
    val list : MutableList<Int> = mutableListOf()
    tags.forEachIndexed { index, tag ->
      if (tag.isActive) list.add(index)
    }
    return list
  }
  /*
  init {
    if (!Paper.book("tags").contains("list")) {
      val tagList : MutableList<String> = mutableListOf()
      tagList.add("Miscellaneous")
      Paper.book("tags").write("list", tagList)
      val map : MutableMap<Date, Pair<Long, Long>> = mutableMapOf()
      Paper.book("stats").write("Miscellaneous",map)
    }
  }
  fun addTag(name : String){
    val tagList : MutableList<String> = Paper.book("tags").read("list")
    tagList.add(name)
    Paper.book("tags").write("list",tagList)
    val map : MutableMap<Date, Pair<Long, Long>> = mutableMapOf()
    Paper.book("stats").write(name,map)
  }
   */

  fun loadStatistics(index: Int, numDays: Int = -1): Pair<String,String> {
    val statList: MutableList<Map<Date, Pair<Long, Long>>> = Paper.book("stats").read("list")
    val statsMap: Map<Date, Pair<Long, Long>> = statList[index]
    var doneTime: Long = 0L
    var totalTime: Long = 0L
    for(date in statsMap.keys){
      if (numDays != -1 && Date.difference(Date.current(), date) >= numDays) continue
      doneTime += statsMap[date]!!.first
      totalTime += statsMap[date]!!.second
    }
    var doneInTimeFormat = Time.timeFromMillis(doneTime)
    var totalInTimeFormat = Time.timeFromMillis(totalTime)
    var done : String = doneInTimeFormat.h.toString() + "h" + doneInTimeFormat.m.toString() + "m"
    var total : String = totalInTimeFormat.h.toString() + "h" + totalInTimeFormat.m.toString() + "m"
    return Pair(tags[index].name,"$done / $total")
  }
}
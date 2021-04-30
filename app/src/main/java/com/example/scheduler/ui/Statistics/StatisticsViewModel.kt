package com.example.scheduler.ui.Statistics

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.scheduler.core.Date
import com.example.scheduler.core.Time
import io.paperdb.Paper

class StatisticsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val tags : MutableList<String>
        get() = Paper.book().read("tags")

    init {
        if (!Paper.book().contains("tags")) {
            Paper.book().write("tags", mutableListOf<String>())
        }
    }
    fun addTag(name : String){
        val tagList : MutableList<String> = Paper.book().read("tags")
        tagList.add(name)
        Paper.book().write("tags",tagList)
        val map : MutableMap<Int,Any?> = mutableMapOf()
        Paper.book().write(name,map)
    }
    fun loadStatistics(tagName : String): Pair<String,String> {
        val statisticsMap : Map<Date,Pair<Time, Time>> = Paper.book().read(tagName)
        var doneTime : Time = Time(0,0)
        var totalTime : Time = Time(0,0)
        Log.d("DBG",tagName+statisticsMap.toString())
        for(stat in statisticsMap.keys){
            doneTime += statisticsMap[stat]!!.first
            totalTime += statisticsMap[stat]!!.second
        }
        return Pair(tagName,"$doneTime / $totalTime")
    }
}
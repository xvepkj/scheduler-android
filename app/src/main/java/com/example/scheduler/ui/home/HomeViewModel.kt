package com.example.scheduler.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scheduler.core.*
import io.paperdb.Book
import io.paperdb.Paper

class HomeViewModel : ViewModel() {

  val worker: Worker
    get() = Paper.book().read("worker")

  private val _schedule: MutableLiveData<List<ScheduledEvent>> = MutableLiveData()
  val schedule: MutableLiveData<List<ScheduledEvent>>
    get() = _schedule

  init {
    if (!Paper.book().contains("worker")) {
      Paper.book().write("worker", Worker())
    }
    _schedule.value = mutableListOf()
  }

  fun addToPool (activeTemplate: ActiveTemplate) {
    val w = worker
    w.addToPool(activeTemplate)
    updateWorker(w)
  }

  fun loadSchedule (d : Date) {
    _schedule.value = worker.generate(d)
  }

  fun updateWorker (w: Worker) {
    Paper.book().write("worker", w)
  }
}
package com.example.scheduler.ui.templates.add

import androidx.lifecycle.ViewModel
import com.example.scheduler.core.Time

class EventAddViewModel : ViewModel() {
  // TODO: Implement the ViewModel
  var startTime = Time(0, 0)
  var endTime = Time(0, 0)
}
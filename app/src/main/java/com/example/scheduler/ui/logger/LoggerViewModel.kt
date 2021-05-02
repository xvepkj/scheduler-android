package com.example.scheduler.ui.logger

import android.app.Application
import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scheduler.BuildConfig
import java.util.*

class LoggerViewModel(private val app: Application) : AndroidViewModel(app) {
  // TODO: Implement the ViewModel

  // Consider using Enum instead?
  val TIMER_FINISHED = -1
  val TIMER_PAUSED = 0
  val TIMER_RUNNING = 1

  private var _timerState = MutableLiveData<Int>(TIMER_PAUSED)
  val timerState : LiveData<Int>
    get() = _timerState

  /* Elapsed time in ms */
  private var _cur = MutableLiveData<Long>()
  val cur: LiveData<Long>
    get() = _cur

  /* Total time in ms */
  private var lim = 0L

  private var baseTime = 0L

  private lateinit var timer : Timer

  fun initialize(elapsed : Long, t: Long) {
    _timerState.value = TIMER_PAUSED
    _cur.value = elapsed

    lim = t
  }

  fun start() {
    baseTime = System.currentTimeMillis() - cur.value!!
    timer = Timer()
    timer.schedule(
      object : TimerTask() {
        override fun run() {
          Log.d("DBG", "hi")
          async_update()
        }
      }, 0L, 500L
    )
    _timerState.value = TIMER_RUNNING
  }

  fun pause() {
    if (BuildConfig.DEBUG && _timerState.value != TIMER_RUNNING) {
      error("Assertion failed")
    }
    _timerState.value = TIMER_PAUSED
    timer.cancel()
  }

  private fun finish() {
    _timerState.postValue(TIMER_FINISHED)
    timer.cancel()

    Log.d("DBG", "FINISHED")
  }

  fun async_update() {
    val t = System.currentTimeMillis() - baseTime
    val z = min(lim, t)
    _cur.postValue(z)
    //Progress
    val progress = (100.0 * z) / lim
    Log.d("DBG", "progress: $progress")
    if (t >= lim) finish()
  }

  private fun min (a: Long, b: Long) : Long {
    return if (a < b) a else b
  }

  fun getLim(): Long {
    return lim
  }
}
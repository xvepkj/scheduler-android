package com.example.scheduler.ui.logger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.scheduler.MainActivity
import com.example.scheduler.R
import com.example.scheduler.ui.home.HomeViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator

class LoggerFragment : Fragment() {

  companion object {
    fun newInstance() = LoggerFragment()
  }

  private lateinit var viewModel: LoggerViewModel
  private lateinit var homeViewModel: HomeViewModel

  private lateinit var eventNameText: TextView
  private lateinit var elapsedText: TextView
  private lateinit var totalText: TextView
  private lateinit var button: Button
  private lateinit var progressBar : CircularProgressIndicator

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.logger_fragment, container, false)
    (activity as MainActivity?)?.supportActionBar?.title = "Logger"

    eventNameText = root.findViewById(R.id.logger_event_name)
    elapsedText = root.findViewById(R.id.logger_elapsed_text)
    totalText = root.findViewById(R.id.logger_total_text)
    button = root.findViewById(R.id.logger_start_pause_button)
    progressBar = root.findViewById(R.id.circular_bar)

    viewModel = ViewModelProvider(this).get(LoggerViewModel::class.java)
    homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

    viewModel.timerState.observe(viewLifecycleOwner,
      Observer<Int> { state ->
        when (state) {
          viewModel.TIMER_FINISHED -> {
            button.isEnabled = false
            button.setText("Finished")
          }
          viewModel.TIMER_PAUSED -> {
            button.isEnabled = true
            button.setText("Start")
          }
          viewModel.TIMER_RUNNING -> {
            button.isEnabled = true
            button.setText("Pause")
          }
        }
      }
    )

    button.setOnClickListener {
      when (viewModel.timerState.value) {
        viewModel.TIMER_PAUSED -> viewModel.start()
        viewModel.TIMER_RUNNING -> viewModel.pause()
      }
    }

    val event = homeViewModel.getLoggedEvent()
    eventNameText.setText(event.name)
    val eventTotalTime = (event.endTime.toMillis() - event.startTime.toMillis())

    viewModel.cur.observe(viewLifecycleOwner,
      Observer<Long> {
        cur -> elapsedText.setText((cur/1000).toString())
        progressBar.progress = (cur*100/viewModel.getLim()).toInt()
        homeViewModel.updateLoggedEventProgress((1.0 * cur) / eventTotalTime)
      }
    )

    viewModel.initialize((eventTotalTime * event.log_progress).toLong(), eventTotalTime)
    totalText.setText((viewModel.getLim()/1000).toString())

    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel
  }

}
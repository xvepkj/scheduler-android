package com.xve.scheduler.ui.logger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xve.scheduler.MainActivity
import com.xve.scheduler.ui.home.HomeViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.xve.scheduler.databinding.LoggerFragmentBinding

class LoggerFragment : Fragment() {

  companion object {
    fun newInstance() = LoggerFragment()
  }

  private lateinit var viewModel: LoggerViewModel
  private lateinit var homeViewModel: HomeViewModel

  private var _binding: LoggerFragmentBinding? = null
  private val binding get() = _binding!!

  private lateinit var progressBar : CircularProgressIndicator

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View {
    _binding = LoggerFragmentBinding.inflate(inflater, container, false)
    (activity as MainActivity?)?.supportActionBar?.title = "Logger"

    viewModel = ViewModelProvider(this).get(LoggerViewModel::class.java)
    homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

    viewModel.timerState.observe(viewLifecycleOwner,
      Observer<Int> { state ->
        binding.loggerStartPauseButton.apply {
          isEnabled = state != viewModel.TIMER_FINISHED
          when (state) {
            viewModel.TIMER_FINISHED -> setText("Finished")
            viewModel.TIMER_PAUSED -> setText("Start")
            viewModel.TIMER_RUNNING -> setText("Pause")
          }
        }
      }
    )

    binding.loggerStartPauseButton.setOnClickListener {
      when (viewModel.timerState.value) {
        viewModel.TIMER_PAUSED -> viewModel.start()
        viewModel.TIMER_RUNNING -> viewModel.pause()
      }
    }

    val event = homeViewModel.getLoggedEvent()
    binding.loggerEventName.text = event.name
    val eventTotalTime = (event.endTime.toMillis() - event.startTime.toMillis())

    viewModel.cur.observe(viewLifecycleOwner,
      Observer<Long> {
        cur -> binding.loggerElapsedText.text = "%02d:%02d:%02d".format(cur/3600000,(cur/60000)%60,(cur%60000)/1000)
        binding.circularBar.progress = (cur*100/viewModel.getLim()).toInt()
        homeViewModel.updateLoggedEventProgress((1.0 * cur) / eventTotalTime)
      }
    )

    viewModel.initialize((eventTotalTime * event.log_progress).toLong(), eventTotalTime)
    val tot = viewModel.getLim()
    binding.loggerTotalText.text = "%02d:%02d:%02d".format(tot/3600000,(tot/60000)%60,(tot%60000)/1000)

    return binding.root
  }

}
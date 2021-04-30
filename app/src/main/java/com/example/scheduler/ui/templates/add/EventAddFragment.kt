package com.example.scheduler.ui.templates.add

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.scheduler.MainActivity
import com.example.scheduler.R
import com.example.scheduler.core.EventType
import com.example.scheduler.core.ScheduledEvent
import com.example.scheduler.core.Time
import com.example.scheduler.ui.Statistics.StatisticsViewModel
import com.example.scheduler.ui.home.HomeFragment
import com.example.scheduler.ui.home.HomeViewModel


class EventAddFragment : Fragment() {

  companion object {
    fun newInstance() = EventAddFragment()
  }

  private lateinit var viewModel: EventAddViewModel
  private lateinit var templateAddViewModel: TemplateAddViewModel
  private lateinit var homeViewModel : HomeViewModel
  private lateinit var statisticsViewModel : StatisticsViewModel

  private lateinit var startTimeTextView: TextView
  private lateinit var endTimeTextView: TextView
  private lateinit var nameEditText: EditText

  private lateinit var untracked : RadioButton
  private lateinit var tracked : RadioButton
  private lateinit var logged : RadioButton

  private lateinit var addButton: Button

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    (activity as MainActivity?)?.supportActionBar?.title = "Add event"
    val root = inflater.inflate(R.layout.event_add_fragment, container, false)
    startTimeTextView = root.findViewById(R.id.eventAddStartTime)
    endTimeTextView = root.findViewById(R.id.eventAddEndTime)
    nameEditText = root.findViewById(R.id.eventAddNameTextField)
    addButton = root.findViewById(R.id.eventAddAddButton)

    untracked = root.findViewById(R.id.untracked)
    tracked = root.findViewById(R.id.tracked)
    logged = root.findViewById(R.id.logged)


    viewModel = ViewModelProvider(this).get(EventAddViewModel::class.java)
    templateAddViewModel = ViewModelProvider(requireActivity()).get(TemplateAddViewModel::class.java)
    homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    statisticsViewModel = ViewModelProvider(requireActivity()).get(StatisticsViewModel::class.java)

    setStartTime(Time(0, 0))
    setEndTime(Time(0, 0))

    val spinner: Spinner = root.findViewById(R.id.select_Tag)
    untracked.setOnClickListener{
      spinner.visibility = GONE
    }
    tracked.setOnClickListener{
      spinner.visibility = VISIBLE
    }
    logged.setOnClickListener{
      spinner.visibility = VISIBLE
    }
    val array: Array<String> = statisticsViewModel.tags.toTypedArray()
    Log.d("DBG",statisticsViewModel.tags.toString())
    val adapter = ArrayAdapter<String>(
      activity?.applicationContext!!,R.layout.spinner_main, array
    )
    adapter.setDropDownViewResource(R.layout.spinner_item);
    spinner.adapter = adapter

    startTimeTextView.setOnClickListener {
      val timePicker = context?.let {
        TimePickerDialog(
          it, R.style.DialogTheme,
          TimePickerDialog.OnTimeSetListener { _, h, m -> setStartTime(Time(h, m)) },
          viewModel.startTime.h,
          viewModel.startTime.m,
          true
        )
      }
      timePicker?.show()
    }

    endTimeTextView.setOnClickListener {
      val timePicker = context?.let {
        TimePickerDialog(
          it, R.style.DialogTheme,
          TimePickerDialog.OnTimeSetListener { _, h, m -> setEndTime(Time(h, m)) },
          viewModel.endTime.h,
          viewModel.endTime.m,
          true
        )
      }
      timePicker?.show()
    }

    addButton.setOnClickListener {
      val eventType = when {
        untracked.isChecked -> EventType.UNTRACKED
        tracked.isChecked -> EventType.TRACKED
        logged.isChecked -> EventType.LOGGED
        else -> EventType.UNTRACKED // For now
      }
      if(!HomeFragment.fromhome)
         templateAddViewModel.events.value?.add(
           ScheduledEvent(
             nameEditText.text.toString(),
             viewModel.startTime,
             viewModel.endTime,
             eventType,
             spinner.selectedItem.toString()
           )
         )
      else
         homeViewModel.addCustomEvent(
           ScheduledEvent(
             nameEditText.text.toString(),
             viewModel.startTime,
             viewModel.endTime,
             eventType,
             spinner.selectedItem.toString()
           ),
           HomeFragment.selecteddate
         )
      viewModel.startTime = Time(0, 0)
      viewModel.endTime = Time(0, 0)
      nameEditText.setText("")
      if(!HomeFragment.fromhome)
        findNavController().navigate(R.id.action_eventAddFragment_to_templateAddFragment)
      else
        findNavController().navigate(R.id.action_eventAddFragment_to_homeFragment)
    }
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel
  }

  fun setStartTime(t: Time) {
    viewModel.startTime = t
    startTimeTextView.text = t.toString()
  }

  fun setEndTime(t: Time) {
    viewModel.endTime = t
    endTimeTextView.text = t.toString()
  }
}
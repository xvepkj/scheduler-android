package com.example.scheduler.ui.templates.add

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.scheduler.R
import com.example.scheduler.core.ScheduledEvent
import com.example.scheduler.core.Time
import com.example.scheduler.ui.home.HomeFragment
import com.example.scheduler.ui.home.HomeViewModel

class EventAddFragment : Fragment() {

  companion object {
    fun newInstance() = EventAddFragment()
  }

  private lateinit var viewModel: EventAddViewModel
  private lateinit var templateAddViewModel: TemplateAddViewModel
  private lateinit var homeViewModel : HomeViewModel

  private lateinit var startTimeTextView: TextView
  private lateinit var endTimeTextView: TextView
  private lateinit var nameEditText: EditText

  private lateinit var addButton: Button

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.event_add_fragment, container, false)
    startTimeTextView = root.findViewById(R.id.eventAddStartTime)
    endTimeTextView = root.findViewById(R.id.eventAddEndTime)
    nameEditText = root.findViewById(R.id.eventAddNameTextField)
    addButton = root.findViewById(R.id.eventAddAddButton)

    viewModel = ViewModelProvider(this).get(EventAddViewModel::class.java)
    templateAddViewModel = ViewModelProvider(requireActivity()).get(TemplateAddViewModel::class.java)
    homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

    setStartTime(Time(0, 0))
    setEndTime(Time(0, 0))

    startTimeTextView.setOnClickListener {
      val timePicker = context?.let {
        TimePickerDialog(
          it,
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
          it,
          TimePickerDialog.OnTimeSetListener { _, h, m -> setEndTime(Time(h, m)) },
          viewModel.endTime.h,
          viewModel.endTime.m,
          true
        )
      }
      timePicker?.show()
    }

    addButton.setOnClickListener {
      if(!HomeFragment.fromhome)
         templateAddViewModel.events.value?.add(ScheduledEvent(nameEditText.text.toString(), viewModel.startTime, viewModel.endTime))
      else
         homeViewModel.addCustomEvent(ScheduledEvent(nameEditText.text.toString(), viewModel.startTime, viewModel.endTime))
        Log.d("DBG","Eh")
      viewModel.startTime = Time(0, 0)
      viewModel.endTime = Time(0, 0)
      nameEditText.setText("")
      Log.d("DBG", HomeFragment.fromhome.toString())
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

  fun setStartTime (t : Time) {
    viewModel.startTime = t
    startTimeTextView.text = t.toString()
  }

  fun setEndTime (t: Time) {
    viewModel.endTime = t
    endTimeTextView.text = t.toString()
  }
}
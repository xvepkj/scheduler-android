package com.xve.scheduler.ui.templates.add

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.xve.scheduler.MainActivity
import com.xve.scheduler.R
import com.xve.scheduler.core.EventType
import com.xve.scheduler.core.ScheduledEvent
import com.xve.scheduler.core.Time
import com.xve.scheduler.ui.Statistics.StatisticsViewModel
import com.xve.scheduler.ui.home.HomeFragment
import com.xve.scheduler.ui.home.HomeViewModel
import com.xve.scheduler.ui.tags.TagsViewModel


class EventAddFragment : Fragment() {

  companion object {
    fun newInstance() = EventAddFragment()
  }

  private lateinit var viewModel: EventAddViewModel
  private lateinit var templateAddViewModel: TemplateAddViewModel
  private lateinit var homeViewModel : HomeViewModel
  private lateinit var statisticsViewModel : StatisticsViewModel
  private lateinit var tagsViewModel: TagsViewModel

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
    tagsViewModel = ViewModelProvider(requireActivity()).get(TagsViewModel::class.java)

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

    // Change to name (id hidden)
    val tagNames: MutableList<String> = mutableListOf()
    val tagIds: MutableList<Int> = mutableListOf()
    tagsViewModel.tags.forEachIndexed { index, tag ->
      if (tag.isActive) {
        tagNames.add(tag.name)
        tagIds.add(index)
      }
    }

    val array: Array<String> = tagNames.toTypedArray()
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
      timePicker?.getButton(TimePickerDialog.BUTTON_POSITIVE)!!
        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
      timePicker?.getButton(TimePickerDialog.BUTTON_NEGATIVE)!!
        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
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
      timePicker?.getButton(TimePickerDialog.BUTTON_POSITIVE)!!
        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
      timePicker?.getButton(TimePickerDialog.BUTTON_NEGATIVE)!!
        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
    }

    addButton.setOnClickListener {
      if (viewModel.endTime < viewModel.startTime) {
        Toast.makeText(context, "End time cannot be earlier than start time", Toast.LENGTH_SHORT).show()
      } else {
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
               tagIds[spinner.selectedItemPosition]
             )
           )
        else
           homeViewModel.addCustomEvent(
             ScheduledEvent(
               nameEditText.text.toString(),
               viewModel.startTime,
               viewModel.endTime,
               eventType,
               tagIds[spinner.selectedItemPosition]
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
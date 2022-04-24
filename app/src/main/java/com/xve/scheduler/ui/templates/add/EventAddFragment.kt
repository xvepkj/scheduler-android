package com.xve.scheduler.ui.templates.add

import android.app.TimePickerDialog
import android.content.Context
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
import com.xve.scheduler.databinding.EventAddFragmentBinding
import com.xve.scheduler.ui.Statistics.StatisticsViewModel
import com.xve.scheduler.ui.home.HomeFragment
import com.xve.scheduler.ui.home.HomeViewModel
import com.xve.scheduler.ui.tags.TagsViewModel
import kotlin.reflect.KClass


class EventAddFragment : Fragment() {

  private lateinit var viewModel: EventAddViewModel
  private lateinit var templateAddViewModel: TemplateAddViewModel
  private lateinit var homeViewModel : HomeViewModel
  private lateinit var statisticsViewModel : StatisticsViewModel
  private lateinit var tagsViewModel: TagsViewModel

  private var _binding: EventAddFragmentBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = EventAddFragmentBinding.inflate(inflater, container, false)

    (activity as MainActivity?)?.supportActionBar?.title = "Add event"

    viewModel = ViewModelProvider(this).get(EventAddViewModel::class.java)
    templateAddViewModel = ViewModelProvider(requireActivity()).get(TemplateAddViewModel::class.java)
    homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    statisticsViewModel = ViewModelProvider(requireActivity()).get(StatisticsViewModel::class.java)
    tagsViewModel = ViewModelProvider(requireActivity()).get(TagsViewModel::class.java)

    setStartTime(Time(0, 0))
    setEndTime(Time(0, 0))

    binding.untracked.setOnClickListener{
      binding.selectTag.visibility = GONE
    }
    binding.tracked.setOnClickListener{
      binding.selectTag.visibility = VISIBLE
    }
    binding.logged.setOnClickListener{
      binding.selectTag.visibility = VISIBLE
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

    val adapter = ArrayAdapter<String>(
      activity?.applicationContext!!,R.layout.spinner_main, tagNames.toTypedArray()
    )
    adapter.setDropDownViewResource(R.layout.spinner_item);
    binding.selectTag.adapter = adapter

    binding.eventAddStartTime.setOnClickListener {
      context?.let { getTimePickerDialog(it, viewModel.startTime.h, viewModel.startTime.m) }?.setButtonColorsAndShow()
    }
    binding.eventAddEndTime.setOnClickListener {
      context?.let { getTimePickerDialog(it, viewModel.endTime.h, viewModel.endTime.m) }?.setButtonColorsAndShow()
    }

    binding.eventAddAddButton.setOnClickListener {
      if (viewModel.endTime < viewModel.startTime)
        Toast.makeText(context, "End time cannot be earlier than start time", Toast.LENGTH_SHORT).show()
       else {
        val eventType = getEventType()

        if(!HomeFragment.fromhome)
           templateAddViewModel.events.value?.add(getScheduledEvent(eventType, tagIds))
        else
           homeViewModel.addCustomEvent(getScheduledEvent(eventType, tagIds), HomeFragment.selecteddate)

        viewModel.startTime = Time(0, 0)
        viewModel.endTime = Time(0, 0)
        binding.eventAddNameTextField.setText("")

        findNavController().navigate(
          if(!HomeFragment.fromhome) R.id.action_eventAddFragment_to_templateAddFragment
          else R.id.action_eventAddFragment_to_homeFragment
        )
      }
    }
    return binding.root
  }

  private fun setStartTime(t: Time) {
    viewModel.startTime = t
    binding.eventAddStartTime.text = t.toString()
  }

  private fun setEndTime(t: Time) {
    viewModel.endTime = t
    binding.eventAddEndTime.text = t.toString()
  }

  private fun TimePickerDialog?.setButtonColorsAndShow() {
    this?.let{
      it.show()
      it.getButton(TimePickerDialog.BUTTON_POSITIVE)!!
        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
      it.getButton(TimePickerDialog.BUTTON_NEGATIVE)!!
        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
    }
  }

  private fun getTimePickerDialog(context : Context, hour : Int, minute : Int) = TimePickerDialog(
    context, R.style.DialogTheme,
    { _, h, m -> setEndTime(Time(h, m)) },
    hour,
    minute,
    true
  )

  private fun getScheduledEvent(eventType: EventType, tagIds: MutableList<Int>) = ScheduledEvent(
    binding.eventAddNameTextField.text.toString(),
    viewModel.startTime,
    viewModel.endTime,
    eventType,
    tagIds[binding.selectTag.selectedItemPosition]
  )

  private fun getEventType() = when {
    binding.untracked.isChecked -> EventType.UNTRACKED
    binding.tracked.isChecked -> EventType.TRACKED
    binding.logged.isChecked -> EventType.LOGGED
    else -> EventType.UNTRACKED // For now
  }
}
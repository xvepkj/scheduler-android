package com.xve.scheduler.ui.templates.add

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.xve.scheduler.MainActivity
import com.xve.scheduler.R
import com.xve.scheduler.core.EventType
import com.xve.scheduler.core.ScheduleTemplate
import com.xve.scheduler.core.ScheduledEvent
import com.xve.scheduler.databinding.TemplateAddFragmentBinding
import com.xve.scheduler.ui.home.HomeFragment
import com.xve.scheduler.ui.tags.TagsViewModel
import com.xve.scheduler.ui.templates.main.TemplateFragment
import com.xve.scheduler.ui.templates.main.TemplateViewModel

class TemplateAddFragment : Fragment() {

  companion object {
    fun newInstance() = TemplateAddFragment()
  }

  private var _binding: TemplateAddFragmentBinding? = null
  private val binding get() = _binding!!

  private lateinit var viewModel: TemplateAddViewModel
  private lateinit var templateViewModel: TemplateViewModel
  private lateinit var templateApplyViewModel: TemplateApplyViewModel
  private lateinit var tagsViewModel: TagsViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = TemplateAddFragmentBinding.inflate(inflater, container, false)
    if(TemplateFragment.TemplateEdit) {
      (activity as MainActivity?)?.supportActionBar?.title = "Edit Template"
      binding.templateEditFinish.visibility= VISIBLE
      binding.templateAddFinish.visibility = GONE
    }
    else {
      (activity as MainActivity?)?.supportActionBar?.title = "New Template"
      binding.templateEditFinish.visibility= GONE
      binding.templateAddFinish.visibility = VISIBLE
    }
    viewModel = ViewModelProvider(requireActivity()).get(TemplateAddViewModel::class.java)
    templateViewModel = ViewModelProvider(requireActivity()).get(TemplateViewModel::class.java)
    templateApplyViewModel= ViewModelProvider(requireActivity()).get(TemplateApplyViewModel::class.java)
    tagsViewModel = ViewModelProvider(requireActivity()).get(TagsViewModel::class.java)
    viewModel.events.observe(viewLifecycleOwner, Observer<List<ScheduledEvent>> { events ->
      showEvents(
        events
      )
    })
    Log.d("DBG",viewModel.template_name)
    val templateNames : List<String> = templateViewModel.getTemplateNames()
    binding.templateAddNameField.doOnTextChanged{_,_,_,_ ->
      val input = binding.templateAddNameField.text.toString()
      for(templateName in templateNames) {
        if (!TemplateFragment.TemplateEdit && input.trim().equals(templateName, ignoreCase = true)) {
          binding.templateAddNameField.error = "Template Name should be unique"
          break
        }
        else
          binding.templateAddNameField.error = null
      }
    }
    binding.templateAddNameField.isEnabled = !TemplateFragment.TemplateEdit
    binding.templateAddFinish.isEnabled = !TemplateFragment.TemplateEdit
    binding.templateEditFinish.isEnabled = TemplateFragment.TemplateEdit
    binding.templateAddNameField.setText(viewModel.template_name)
    binding.templateAddEventAdd.setOnClickListener {
      // viewModel.addEvent(ScheduledEvent("test", Time(0, 0), Time(1, 0)))
      HomeFragment.fromhome = false
      viewModel.template_name = binding.templateAddNameField.text.toString()
      findNavController().navigate(R.id.action_templateAddFragment_to_eventAddFragment)
    }

    binding.templateAddFinish.setOnClickListener {
      val newTemplate = ScheduleTemplate(binding.templateAddNameField.text.toString())
      for (e in viewModel.events.value!!) newTemplate.add(e)
      templateViewModel.addTemplate(newTemplate)
      viewModel.clear()
      findNavController().navigate(R.id.action_templateAddFragment_to_templateFragment)
    }

    binding.templateEditFinish.setOnClickListener {
      val newTemplate = ScheduleTemplate(binding.templateAddNameField.text.toString())
      for (e in viewModel.events.value!!) newTemplate.add(e)
      templateViewModel.removeTemplate(templateApplyViewModel.template)
      templateViewModel.addTemplate(newTemplate)
      viewModel.clear()
      findNavController().navigate(R.id.action_templateAddFragment_to_templateFragment)
    }
    return binding.root
  }
  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel
  }

  @SuppressLint("NewApi")
  fun showEvents(events: List<ScheduledEvent>) {
    binding.templateAddEventList.removeAllViews()
    for (i in events.indices) {
      val event = events[i]
      val view: View = layoutInflater.inflate(R.layout.event, null)
      val t = view.findViewById<TextView>(R.id.eventdetails)
      val starttime = view.findViewById<TextView>(R.id.starttime)
      val endtime = view.findViewById<TextView>(R.id.endtime)
      val crossbutton = view.findViewById<ImageButton>(R.id.removeevent)
      val trackedEventCheckbox = view.findViewById<CheckBox>(R.id.tracked_checkbox)
      val loggedProgress = view.findViewById<TextView>(R.id.log_progress_text)

      // tag info if tracked/logged
      val tagColorView = view.findViewById<ImageButton>(R.id.event_tag_color_template)
      tagColorView.visibility = View.GONE
      if (event.eventType != EventType.UNTRACKED) {
        tagColorView.visibility = View.VISIBLE
        var tag = tagsViewModel.get(event.tagId)
        if (!tag.isActive) tag = tagsViewModel.get(0)
        tagColorView.setColorFilter(
          tag.color,
          PorterDuff.Mode.SRC_ATOP)
      }

      if(event.eventType == EventType.TRACKED){
        trackedEventCheckbox.visibility = VISIBLE
        trackedEventCheckbox.isEnabled = false
      }
      if(event.eventType == EventType.LOGGED){
        loggedProgress.text = "0.0"
        loggedProgress.visibility = VISIBLE
        loggedProgress.isEnabled = false
      }

      t.text = event.name
      starttime.text = event.startTime.toString()
      endtime.text = event.endTime.toString()
      crossbutton.setOnClickListener{
        viewModel.events.value?.removeAt(i)
        showEvents(events)
      }
      binding.templateAddEventList.addView(view)
    }
  }
}
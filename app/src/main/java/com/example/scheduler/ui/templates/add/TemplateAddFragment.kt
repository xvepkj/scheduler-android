package com.example.scheduler.ui.templates.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.scheduler.MainActivity
import com.example.scheduler.R
import com.example.scheduler.core.ScheduleTemplate
import com.example.scheduler.core.ScheduledEvent
import com.example.scheduler.databinding.TemplateAddFragmentBinding
import com.example.scheduler.ui.home.HomeFragment
import com.example.scheduler.ui.templates.main.TemplateViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TemplateAddFragment : Fragment() {

  companion object {
    fun newInstance() = TemplateAddFragment()
  }

  private var _binding: TemplateAddFragmentBinding? = null
  private val binding get() = _binding!!

  private lateinit var viewModel: TemplateAddViewModel
  private lateinit var templateViewModel: TemplateViewModel


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = TemplateAddFragmentBinding.inflate(inflater, container, false)

    (activity as MainActivity?)?.supportActionBar?.title = "New Template"
    viewModel = ViewModelProvider(requireActivity()).get(TemplateAddViewModel::class.java)
    templateViewModel = ViewModelProvider(requireActivity()).get(TemplateViewModel::class.java)

    viewModel.events.observe(viewLifecycleOwner, Observer<List<ScheduledEvent>> { events ->
      showEvents(
        events
      )
    })
    Log.d("DBG",viewModel.template_name)
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

    return binding.root
  }
  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel
  }

  fun showEvents(events: List<ScheduledEvent>) {
    binding.templateAddEventList.removeAllViews()
    for (i in events.indices) {
      val event = events[i]
      val view: View = layoutInflater.inflate(R.layout.event, null)
      val t = view.findViewById<TextView>(R.id.eventdetails)
      val crossbutton = view.findViewById<FloatingActionButton>(R.id.removeevent)
      t.text = event.toString()
      crossbutton.setOnClickListener{
        viewModel.events.value?.removeAt(i)
        showEvents(events)
      }
      binding.templateAddEventList.addView(view)
    }
  }
}
package com.example.scheduler.ui.templates.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.scheduler.MainActivity
import com.example.scheduler.R
import com.example.scheduler.core.ScheduleTemplate
import com.example.scheduler.core.ScheduledEvent
import com.example.scheduler.core.Time
import com.example.scheduler.ui.templates.main.TemplateViewModel

class TemplateAddFragment : Fragment() {

  companion object {
    fun newInstance() = TemplateAddFragment()
  }

  private lateinit var viewModel: TemplateAddViewModel
  private lateinit var templateViewModel: TemplateViewModel

  private lateinit var linearLayout: LinearLayout
  private lateinit var addEventButton: Button
  private lateinit var removeEventButton: Button
  private lateinit var addTemplateButton: Button
  private lateinit var cancelButton: Button

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.template_add_fragment, container, false)

    // UI elements
    linearLayout = root.findViewById(R.id.newtemplateLinearLayout)
    addEventButton = root.findViewById(R.id.newtemplateadd)
    removeEventButton = root.findViewById(R.id.newtemplateremove)
    addTemplateButton = root.findViewById(R.id.newtemplatetick)
    cancelButton = root.findViewById(R.id.newtemplatecross)

    (activity as MainActivity?)?.supportActionBar?.title = "New Template"
    viewModel = ViewModelProvider(requireActivity()).get(TemplateAddViewModel::class.java)
    templateViewModel = ViewModelProvider(requireActivity()).get(TemplateViewModel::class.java)

    viewModel.template.observe(viewLifecycleOwner, Observer<ScheduleTemplate> {
      template -> showTemplate(template)
    })

    addEventButton.setOnClickListener {
      // viewModel.addEvent(ScheduledEvent("test", Time(0, 0), Time(1, 0)))
      findNavController().navigate(R.id.action_templateAddFragment_to_eventAddFragment)
    }

    addTemplateButton.setOnClickListener {
      templateViewModel.addTemplate(viewModel.template.value!!)
      viewModel.clear()
      findNavController().navigate(R.id.action_templateAddFragment_to_templateFragment)
    }

    cancelButton.setOnClickListener {
      findNavController().navigate(R.id.action_templateAddFragment_to_templateFragment)
    }

    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel
  }

  fun showTemplate(template: ScheduleTemplate) {
    linearLayout.removeAllViews()
    for (event in template.events) {
      val textView = TextView(activity)
      textView.text = event.toString()
      linearLayout.addView(textView)
    }
  }
}
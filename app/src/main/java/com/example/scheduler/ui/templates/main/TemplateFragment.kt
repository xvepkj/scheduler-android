package com.example.scheduler.ui.templates.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.scheduler.R
import com.example.scheduler.core.*
import com.example.scheduler.ui.home.HomeViewModel
import com.example.scheduler.ui.templates.add.TemplateApplyViewModel

class TemplateFragment : Fragment() {

  companion object {
    fun newInstance() = TemplateFragment()
  }

  private lateinit var viewModel: TemplateViewModel
  private lateinit var applyViewModel: TemplateApplyViewModel

  // UI Components
  private lateinit var listLinearLayout: LinearLayout
  private lateinit var descLinearLayout: LinearLayout
  private lateinit var addButton: Button
  private lateinit var removeButton: Button
  private lateinit var applyButton: Button

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.template_fragment, container, false)

    // Set UI Components
    listLinearLayout = root.findViewById(R.id.templateListLinearLayout)
    descLinearLayout = root.findViewById(R.id.templateDescLinearLayout)
    addButton = root.findViewById(R.id.templateAddButton)
    removeButton = root.findViewById(R.id.templateRemoveButton)
    applyButton = root.findViewById(R.id.templateApplyButton)
    applyButton.isEnabled = false

    viewModel = ViewModelProvider(requireActivity()).get(TemplateViewModel::class.java)
    applyViewModel = ViewModelProvider(requireActivity()).get(TemplateApplyViewModel::class.java)

    viewModel.templates.observe(viewLifecycleOwner, Observer<MutableList<ScheduleTemplate>> { templates -> showTemplateList(templates) })
    addButton.setOnClickListener {
      view?.findNavController()?.navigate(R.id.action_templateFragment_to_templateAddFragment)
    }

    applyButton.setOnClickListener {
      view?.findNavController()?.navigate(R.id.action_templateFragment_to_templateApplyFragment)
    }
    // showTemplateList(viewModel.templates.value!!)
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel
  }

  fun showTemplateList(templates: List<ScheduleTemplate>) {
    listLinearLayout.removeAllViews()
    for (i in templates.indices) {
      val textView = TextView(activity)
      textView.text = "Template $i"
      textView.setOnClickListener {
        showTemplateDesc(i)
      }
      listLinearLayout.addView(textView)
    }
  }

  fun showTemplateDesc(index: Int) {
    descLinearLayout.removeAllViews()
    applyButton.isEnabled = true
    val template = viewModel.templates.value?.get(index)
    applyViewModel.template = template!!
      for (event in template!!.events) {
      val textView = TextView(activity)
      textView.text = event.toString()
      descLinearLayout.addView(textView)
    }
  }
}
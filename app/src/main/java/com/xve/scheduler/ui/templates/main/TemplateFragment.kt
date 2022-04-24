package com.xve.scheduler.ui.templates.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.xve.scheduler.MainActivity
import com.xve.scheduler.R
import com.xve.scheduler.databinding.TemplateFragmentBinding
import com.xve.scheduler.ui.home.HomeViewModel
import com.xve.scheduler.ui.templates.add.TemplateAddViewModel
import com.xve.scheduler.ui.templates.add.TemplateApplyViewModel


class TemplateFragment : Fragment() {

  companion object {
    var TemplateEdit : Boolean = false
  }

  private var _binding: TemplateFragmentBinding? = null
  private val binding get() = _binding!!

  private lateinit var viewModel: TemplateViewModel
  private lateinit var applyViewModel: TemplateApplyViewModel
  private lateinit var homeViewModel: HomeViewModel
  private lateinit var templateViewModel: TemplateViewModel
  private lateinit var templateAddViewModel: TemplateAddViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    (activity as MainActivity?)?.supportActionBar?.title = "Templates"
    _binding = TemplateFragmentBinding.inflate(inflater, container, false)

    viewModel = ViewModelProvider(requireActivity()).get(TemplateViewModel::class.java)
    applyViewModel = ViewModelProvider(requireActivity()).get(TemplateApplyViewModel::class.java)
    homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    templateViewModel = ViewModelProvider(requireActivity()).get(TemplateViewModel::class.java)
    templateAddViewModel = ViewModelProvider(requireActivity()).get(TemplateAddViewModel::class.java)

    binding.templateApplyButton.isEnabled = false
    binding.templateEditButton.isEnabled = false
    binding.templateRemoveButton.isEnabled = false

    binding.activeTemplates.setOnClickListener{
      findNavController().navigate(R.id.action_templateFragment_to_templatePoolFragment)
    }
    binding.templateAddButton.setOnClickListener {
      TemplateEdit=false
      templateAddViewModel.clear()
      view?.findNavController()?.navigate(R.id.action_templateFragment_to_templateAddFragment)
    }
    binding.templateApplyButton.setOnClickListener {
      view?.findNavController()?.navigate(R.id.action_templateFragment_to_templateApplyFragment)
    }
    binding.templateEditButton.setOnClickListener{
      TemplateEdit=true
      templateAddViewModel.template_name = applyViewModel.template.name
      templateAddViewModel.events.value = applyViewModel.template.events.toMutableList()
      view?.findNavController()?.navigate(R.id.action_templateFragment_to_templateAddFragment)
    }
    binding.templateRemoveButton.setOnClickListener{
      var currentlyapplied : Boolean = false
      for(active_template in homeViewModel.worker.getPool())
        if(active_template.templatename == applyViewModel.template.name)
          currentlyapplied = true
      if(!currentlyapplied){
        binding.templateDescLinearLayout.removeAllViews()
        templateViewModel.removeTemplate(applyViewModel.template)}
      else
        Toast.makeText(context, "This template is currently applied", Toast.LENGTH_SHORT).show()
      showTemplateList(viewModel.getTemplateNames())
    }
    showTemplateList(viewModel.getTemplateNames())
    return binding.root
  }

  private fun showTemplateList(names: List<String>) {
    binding.templateListLinearLayout.removeAllViews()
    for (name in names) {
      val textView = TextView(activity)
      textView.apply {
        text = name
        textSize= 18F
        typeface = ResourcesCompat.getFont(requireContext(), R.font.biorhyme_light)
        setTextColor(Color.parseColor("#000000"));
        setOnClickListener {
          showTemplateDesc(name)
        }
      }
      binding.templateListLinearLayout.addView(textView)
    }
  }

  private fun showTemplateDesc(name: String) {
    binding.templateDescLinearLayout.removeAllViews()
    binding.templateApplyButton.isEnabled = true
    binding.templateEditButton.isEnabled = true
    binding.templateRemoveButton.isEnabled = true

    val template = viewModel.getTemplate(name)
    applyViewModel.template = template
      for (event in template.events) {
      val view: View = layoutInflater.inflate(R.layout.template_list_item, null)
      val name = view.findViewById<TextView>(R.id.name_field)
      val startTime = view.findViewById<TextView>(R.id.start_time)
      val endTime = view.findViewById<TextView>(R.id.end_time)
      name.text = event.name
      startTime.text = event.startTime.toString()
      endTime.text = event.endTime.toString()
      binding.templateDescLinearLayout.addView(view)
    }
  }
}
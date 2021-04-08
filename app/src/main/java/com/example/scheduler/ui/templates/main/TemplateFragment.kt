package com.example.scheduler.ui.templates.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.scheduler.R
import com.example.scheduler.databinding.TemplateFragmentBinding
import com.example.scheduler.ui.home.HomeViewModel
import com.example.scheduler.ui.templates.add.TemplateApplyViewModel

class TemplateFragment : Fragment() {

  companion object {
    fun newInstance() = TemplateFragment()
  }

  private var _binding: TemplateFragmentBinding? = null
  private val binding get() = _binding!!

  private lateinit var viewModel: TemplateViewModel
  private lateinit var applyViewModel: TemplateApplyViewModel
  private lateinit var homeViewModel: HomeViewModel
  private lateinit var templateViewModel: TemplateViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = TemplateFragmentBinding.inflate(inflater, container, false)

    viewModel = ViewModelProvider(requireActivity()).get(TemplateViewModel::class.java)
    applyViewModel = ViewModelProvider(requireActivity()).get(TemplateApplyViewModel::class.java)
    homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    templateViewModel = ViewModelProvider(requireActivity()).get(TemplateViewModel::class.java)

    binding.templateApplyButton.isEnabled = false
    binding.templateAddButton.setOnClickListener {
      view?.findNavController()?.navigate(R.id.action_templateFragment_to_templateAddFragment)
    }

    binding.templateApplyButton.setOnClickListener {
      view?.findNavController()?.navigate(R.id.action_templateFragment_to_templateApplyFragment)
    }
    binding.templateRemoveButton.setOnClickListener{
      var currentlyapplied : Boolean = false
      for(active_template in homeViewModel.worker.getPool())
        if(active_template.template.name == applyViewModel.template.name)
          currentlyapplied = true
      if(!currentlyapplied)
        templateViewModel.removeTemplate(applyViewModel.template)
      showTemplateList(viewModel.getTemplateNames())
    }
    showTemplateList(viewModel.getTemplateNames())
    return binding.root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel
  }

  fun showTemplateList(names: List<String>) {
    binding.templateListLinearLayout.removeAllViews()
    for (name in names) {
      val textView = TextView(activity)
      textView.text = name
      textView.setOnClickListener {
        showTemplateDesc(name)
      }
      binding.templateListLinearLayout.addView(textView)
    }
  }

  fun showTemplateDesc(name: String) {
    binding.templateDescLinearLayout.removeAllViews()
    binding.templateApplyButton.isEnabled = true
    val template = viewModel.getTemplate(name)
    applyViewModel.template = template
      for (event in template.events) {
      val textView = TextView(activity)
      textView.text = event.toString()
      binding.templateDescLinearLayout.addView(textView)
    }
  }
}
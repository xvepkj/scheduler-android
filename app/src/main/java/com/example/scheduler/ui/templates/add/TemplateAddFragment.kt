package com.example.scheduler.ui.templates.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.scheduler.MainActivity
import com.example.scheduler.R

class TemplateAddFragment : Fragment() {

  companion object {
    fun newInstance() = TemplateAddFragment()
  }

  private lateinit var viewModel: TemplateAddViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    (activity as MainActivity?)?.supportActionBar?.title = "New Template"
    return inflater.inflate(R.layout.template_add_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(TemplateAddViewModel::class.java)
    // TODO: Use the ViewModel
  }

}
package com.example.scheduler.ui.templates.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.scheduler.R

class TemplateApplyFragment : Fragment() {

  companion object {
    fun newInstance() = TemplateApplyFragment()
  }

  private lateinit var viewModel: TemplateApplyViewModel
  private  lateinit var repeatView : LinearLayout
  private  lateinit var customview : LinearLayout
  private lateinit var repeat : RadioButton
  private lateinit var customselec : RadioButton
  private lateinit var weekly : RadioButton
  private lateinit var monthly : RadioButton
  private lateinit var frequency : RadioButton
  private lateinit var enterfrequency : EditText
  private lateinit var daypicker : MaterialDayPicker

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root =inflater.inflate(R.layout.template_apply_fragment, container, false)
    repeatView = root.findViewById(R.id.templateapplyrepeatview)
    customview = root.findViewById(R.id.templatecustomview)
    repeat = root.findViewById(R.id.templateapplyrepeat)
    customselec = root.findViewById(R.id.templateapplycustomselec)
    weekly = root.findViewById(R.id.templateapplyweekly)
    monthly = root.findViewById(R.id.templateapplymonthly)
    frequency = root.findViewById(R.id.templateapplyfrequency)
    enterfrequency = root.findViewById(R.id.enterfrequency)
    daypicker = root.findViewById(R.id.day_picker)
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(TemplateApplyViewModel::class.java)
    // TODO: Use the ViewModel
    repeat.setOnClickListener(){
      repeatView.visibility = VISIBLE
      customview.visibility = GONE
    }
    customselec.setOnClickListener(){
      repeatView.visibility = GONE
      customview.visibility = VISIBLE
    }
    weekly.setOnClickListener(){
      enterfrequency.visibility=GONE
      daypicker.visibility= VISIBLE
    }
    frequency.setOnClickListener(){
      enterfrequency.visibility= VISIBLE
      daypicker.visibility= GONE
    }
    monthly.setOnClickListener(){
      enterfrequency.visibility=GONE
      daypicker.visibility= GONE
    }
  }

}
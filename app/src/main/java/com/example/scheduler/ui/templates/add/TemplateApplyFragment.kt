package com.example.scheduler.ui.templates.add

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.scheduler.MainActivity
import com.example.scheduler.R
import com.example.scheduler.core.ActiveTemplate
import com.example.scheduler.core.Date
import com.example.scheduler.core.RepeatCriteria
import com.example.scheduler.core.RepeatType
import com.example.scheduler.ui.home.HomeViewModel

class TemplateApplyFragment : Fragment() {

  companion object {
    fun newInstance() = TemplateApplyFragment()
  }

  private lateinit var customAddDateButton: Button
  private lateinit var customDateLinearLayout: LinearLayout
  private lateinit var viewModel: TemplateApplyViewModel
  private lateinit var homeViewModel: HomeViewModel
  private  lateinit var repeatView : LinearLayout
  private  lateinit var customview : ConstraintLayout
  private lateinit var repeat : RadioButton
  private lateinit var customselec : RadioButton
  private lateinit var weekly : RadioButton
  private lateinit var monthly : RadioButton
  private lateinit var frequency : RadioButton
  private lateinit var enterfrequency : EditText
  private lateinit var daypicker : MaterialDayPicker
  private lateinit var applyButton: Button
  private lateinit var datepicker : LinearLayout

  @RequiresApi(Build.VERSION_CODES.N)
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    (activity as MainActivity?)?.supportActionBar?.title = "Apply Template"
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
    datepicker = root.findViewById(R.id.datepickerlayout)
    applyButton = root.findViewById(R.id.applyTemplateButton)
    customAddDateButton = root.findViewById(R.id.applyCustomAddDate)
    customDateLinearLayout = root.findViewById(R.id.applyCustomDatesList)
    viewModel = ViewModelProvider(requireActivity()).get(TemplateApplyViewModel::class.java)
    homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

    viewModel.customDatesList.clear()

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
      datepicker.visibility=GONE
    }
    frequency.setOnClickListener(){
      enterfrequency.visibility= VISIBLE
      daypicker.visibility= GONE
      datepicker.visibility=GONE
    }
    monthly.setOnClickListener(){
      enterfrequency.visibility=GONE
      daypicker.visibility= GONE
      datepicker.visibility= VISIBLE
    }

    applyButton.setOnClickListener {
      val repeats = repeat.isChecked
      val activeTemplate = ActiveTemplate(viewModel.template.name, repeats)
      if (repeats) {
        val repeatType = when {
          weekly.isChecked -> RepeatType.WEEKLY
          monthly.isChecked -> RepeatType.MONTHLY
          frequency.isChecked -> RepeatType.FREQUENCY
          else -> RepeatType.MONTHLY // For now
        }
        val list: MutableList<Int> = mutableListOf()
        when (repeatType) {
          RepeatType.WEEKLY -> {
            for (d in daypicker.selectedDays) {
              list.add(
                when(d) {
                  MaterialDayPicker.Weekday.SUNDAY -> 1
                  MaterialDayPicker.Weekday.MONDAY -> 2
                  MaterialDayPicker.Weekday.TUESDAY -> 3
                  MaterialDayPicker.Weekday.WEDNESDAY -> 4
                  MaterialDayPicker.Weekday.THURSDAY -> 5
                  MaterialDayPicker.Weekday.FRIDAY -> 6
                  MaterialDayPicker.Weekday.SATURDAY -> 7
                }
              )
            }
          }
          RepeatType.FREQUENCY -> list.add(enterfrequency.text.toString().toInt())
          RepeatType.MONTHLY -> {
            for(i in 1..30) {
              val currentdate = "date$i"
              val button: ToggleButton? = activity?.findViewById<ToggleButton>(resources.getIdentifier(currentdate, "id", requireActivity().packageName))
              if(button!!.isChecked)
                list.add(i)
            }
          }
        }
        activeTemplate.setRepeatCriteria(RepeatCriteria(Date.current(), repeatType, list))
      } else {
        for (d in viewModel.customDatesList) {
          activeTemplate.addDay(d)
        }
      }
      Log.d("DBG", activeTemplate.toString())
      homeViewModel.addToPool(activeTemplate)
      findNavController().navigate(R.id.action_templateApplyFragment_to_homeFragment)
    }

    customAddDateButton.setOnClickListener {
      val datePicker = context?.let { DatePickerDialog(it,R.style.DialogTheme) }
      datePicker?.setOnDateSetListener { _, year, month, day ->
        val d = Date(day, month + 1, year)
        viewModel.customDatesList.add(d)
        refreshCustomDatesList()
      }
      datePicker?.show()
    }

    return root
  }

  fun refreshCustomDatesList() {
    customDateLinearLayout.removeAllViews()
    for (d in viewModel.customDatesList) {
      val textView = TextView(activity)
      textView.text = d.toString()
      customDateLinearLayout.addView(textView)
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel
  }

}
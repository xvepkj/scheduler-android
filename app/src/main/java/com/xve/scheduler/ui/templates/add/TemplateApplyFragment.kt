package com.xve.scheduler.ui.templates.add

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.xve.scheduler.MainActivity
import com.xve.scheduler.R
import com.xve.scheduler.core.ActiveTemplate
import com.xve.scheduler.core.Date
import com.xve.scheduler.core.RepeatCriteria
import com.xve.scheduler.core.RepeatType
import com.xve.scheduler.databinding.TemplateApplyFragmentBinding
import com.xve.scheduler.ui.home.HomeViewModel

class TemplateApplyFragment : Fragment(), View.OnClickListener {

  private var _binding: TemplateApplyFragmentBinding? = null
  private val binding get() = _binding!!

  private lateinit var viewModel: TemplateApplyViewModel
  private lateinit var homeViewModel: HomeViewModel

  @RequiresApi(Build.VERSION_CODES.N)
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = TemplateApplyFragmentBinding.inflate(inflater, container, false)

    (activity as MainActivity?)?.supportActionBar?.title = "Apply Template"

    viewModel = ViewModelProvider(requireActivity()).get(TemplateApplyViewModel::class.java)
    homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

    viewModel.customDatesList.clear()

    setListeners()

    return binding.root
  }

  private fun setListeners() {
    binding.templateapplyrepeat.setOnClickListener(this)
    binding.templateapplycustomselec.setOnClickListener(this)
    binding.templateapplyweekly.setOnClickListener(this)
    binding.templateapplyfrequency.setOnClickListener(this)
    binding.templateapplymonthly.setOnClickListener(this)
    binding.applyTemplateButton.setOnClickListener(this)
    binding.applyCustomAddDate.setOnClickListener(this)
  }

  @RequiresApi(Build.VERSION_CODES.N)
  override fun onClick(view: View) {
    when(view) {
      binding.templateapplyrepeat ->setCustomOrRepeatView(VISIBLE, GONE)
      binding.templateapplycustomselec -> setCustomOrRepeatView(GONE, VISIBLE)
      binding.templateapplyweekly -> setRepeatCriteriaView(GONE, VISIBLE, GONE)
      binding.templateapplyfrequency -> setRepeatCriteriaView(VISIBLE, GONE, GONE)
      binding.templateapplymonthly -> setRepeatCriteriaView(GONE, GONE, VISIBLE)
      binding.applyCustomAddDate -> showDatePickerDialog()
      binding.applyTemplateButton ->  applyTemplate()
    }
  }

  private fun refreshCustomDatesList() {
    binding.applyCustomDatesList.removeAllViews()
    for (d in viewModel.customDatesList) {
      val textView = TextView(activity).apply {
        text = d.toString()
        textSize = 15F
      }
      binding.applyCustomDatesList.addView(textView)
    }
  }

  private fun setCustomOrRepeatView(repeat : Int, custom : Int){
    binding.templateapplyrepeatview.visibility = repeat
    binding.templatecustomview.visibility = custom
  }

  private fun setRepeatCriteriaView(frequency : Int, dayPicker : Int, datePicker : Int){
    binding.enterfrequency.visibility = frequency
    binding.dayPicker.visibility = dayPicker
    binding.datepickerlayout.maindatepickerlayout.visibility = datePicker
  }

  private fun getRepeatType() = when {
    binding.templateapplyweekly.isChecked -> RepeatType.WEEKLY
    binding.templateapplymonthly.isChecked -> RepeatType.MONTHLY
    binding.templateapplyfrequency.isChecked -> RepeatType.FREQUENCY
    else -> RepeatType.MONTHLY // For now
  }

  @RequiresApi(Build.VERSION_CODES.N)
  private fun showDatePickerDialog() {
    context?.let { DatePickerDialog(it,R.style.DialogTheme) }?.let {
      it.setOnDateSetListener { _, year, month, day ->
        val d = Date(day, month + 1, year)
        viewModel.customDatesList.add(d)
        refreshCustomDatesList()
      }
      it.show()
      it.getButton(DatePickerDialog.BUTTON_POSITIVE)!!
        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
      it.getButton(DatePickerDialog.BUTTON_NEGATIVE)!!
        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
    }
  }

  private fun getSelectedDates() : MutableList<Int> {
    val localList : MutableList<Int> = mutableListOf()
    for(i in 1..30) {
      val currentDate = "date$i"
      val button: ToggleButton? = activity?.findViewById(resources.getIdentifier(currentDate, "id", requireActivity().packageName))
      if(button!!.isChecked) localList.add(i)
    }
    return localList
  }

  private fun applyTemplate() {
    val repeats = binding.templateapplyrepeat.isChecked
    val activeTemplate = ActiveTemplate(viewModel.template.name, repeats)
    if (repeats) {
      val repeatType = getRepeatType()
      val list: MutableList<Int> = mutableListOf()
      when (repeatType) {
        RepeatType.WEEKLY -> for (d in binding.dayPicker.selectedDays) list.add(d.ordinal + 1)
        RepeatType.FREQUENCY -> list.add(binding.enterfrequency.text.toString().toInt())
        RepeatType.MONTHLY -> list.addAll(getSelectedDates())
      }
      activeTemplate.setRepeatCriteria(RepeatCriteria(Date.current(), repeatType, list))
    } else for (d in viewModel.customDatesList) activeTemplate.addDay(d)

    Log.d("DBG", activeTemplate.toString())
    homeViewModel.addToPool(activeTemplate)
    findNavController().navigate(R.id.action_templateApplyFragment_to_homeFragment)
  }
}
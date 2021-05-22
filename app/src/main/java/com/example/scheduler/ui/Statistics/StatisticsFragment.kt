package com.example.scheduler.ui.Statistics

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.scheduler.MainActivity
import com.example.scheduler.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


class StatisticsFragment : Fragment() {

  companion object {
    fun newInstance() = StatisticsFragment()
  }

  private val COUNT_ALL_TIME = -1
  private val COUNT_LAST_MONTH = 30
  private val COUNT_LAST_WEEK = 7
  private val COUNT_TODAY = 1

  private lateinit var viewModel: StatisticsViewModel
  private lateinit var statsList: LinearLayout
  private lateinit var statsList2: LinearLayout
  private lateinit var stats_spinner : Spinner
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root =inflater.inflate(R.layout.statistics_fragment, container, false)
    (activity as MainActivity?)?.supportActionBar?.title = "Statistics"
    viewModel = ViewModelProvider(requireActivity()).get(StatisticsViewModel::class.java)
    val addTag = root.findViewById<ExtendedFloatingActionButton>(R.id.add_Tag)
    statsList = root.findViewById<LinearLayout>(R.id.linear_layout_stats)
    stats_spinner = root.findViewById<Spinner>(R.id.stats_spinner)
    val array: Array<String> = arrayOf("All time", "Today's", "Last Week's", "Last Month's")
    val adapter = ArrayAdapter<String>(
      activity?.applicationContext!!, R.layout.stats_spinner_main, array
    )
    adapter.setDropDownViewResource(R.layout.stats_spinner_item);
    stats_spinner.adapter = adapter

    stats_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        loadStatisticstoUI(
          when (position) {
            0 -> COUNT_ALL_TIME
            1 -> COUNT_TODAY
            2 -> COUNT_LAST_WEEK
            3 -> COUNT_LAST_MONTH
            else -> COUNT_ALL_TIME
          }
        )
      }

      override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(context, "Nothing selected", Toast.LENGTH_SHORT).show()
      }
    }
    addTag.setOnClickListener{
      val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(context)
      val inflater = this.layoutInflater
      val dialogView = inflater.inflate(R.layout.custom_dialog_box_edittext, null)
      builder.setTitle("New Tag")

      val input : EditText = dialogView.findViewById(R.id.edit1)

      input.hint = "Enter Tag name"
      input.inputType = InputType.TYPE_CLASS_TEXT
      builder.setView(dialogView)

      builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
        var newTagName = input.text.toString()
        Log.d("DBG", newTagName)
        viewModel.addTag(newTagName)
        Log.d("DBG", viewModel.tags.toString())
        loadStatisticstoUI(COUNT_ALL_TIME)
      })
      builder.setNegativeButton(
        "Cancel",
        DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
      val dialog = builder.create()
      dialog.show()
      dialog?.getButton(DatePickerDialog.BUTTON_POSITIVE)!!
        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
      dialog?.getButton(DatePickerDialog.BUTTON_NEGATIVE)!!
        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))

    }
    loadStatisticstoUI(COUNT_ALL_TIME)
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)
    // TODO: Use the ViewModel
  }
  fun loadStatisticstoUI(numDays: Int){
    statsList.removeAllViews()
    for(tag in viewModel.tags){
      val tagStats : Pair<String, String> = viewModel.loadStatistics(tag, numDays)
      val view: View = layoutInflater.inflate(R.layout.stats_item, null)
      val textView = view.findViewById<TextView>(R.id.name_field)
      textView.text = tagStats.first
      textView.textSize= 17F
      val typeface = ResourcesCompat.getFont(requireContext(), R.font.biorhyme_light)
      textView.typeface = typeface
      textView.gravity = Gravity.LEFT
      textView.setTextColor(Color.parseColor("#000000"));
      val textView2 = view.findViewById<TextView>(R.id.statistics_field)
      textView2.text = tagStats.second
      textView2.gravity = Gravity.RIGHT
      textView2.textSize= 17F
      val typeface2 = ResourcesCompat.getFont(requireContext(), R.font.biorhyme_light)
      textView2.typeface = typeface2
      textView2.setTextColor(Color.parseColor("#000000"));
      statsList.addView(view)
    }
  }
}
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.scheduler.MainActivity
import com.example.scheduler.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class StatisticsFragment : Fragment() {

    companion object {
        fun newInstance() = StatisticsFragment()
    }

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
        statsList = root.findViewById<LinearLayout>(R.id.statisticsList)
        statsList2 = root.findViewById<LinearLayout>(R.id.statisticsList2)
        stats_spinner = root.findViewById<Spinner>(R.id.stats_spinner)
        val array: Array<String> = arrayOf("All time", "Today's", "Last Week's", "Last Month's")
        val adapter = ArrayAdapter<String>(
                activity?.applicationContext!!, R.layout.stats_spinner_main, array
        )
        adapter.setDropDownViewResource(R.layout.stats_spinner_item);
        stats_spinner.adapter = adapter

        addTag.setOnClickListener{
                val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(context)
                builder.setTitle("New Tag")

                val input = EditText(context)

                input.hint = "   Enter Tag name"
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    var newTagName = input.text.toString()
                    Log.d("DBG", newTagName)
                    viewModel.addTag(newTagName)
                    Log.d("DBG", viewModel.tags.toString())
                    loadStatisticstoUI()
                })
                builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                val dialog = builder.create()
                dialog.show()
                dialog?.getButton(DatePickerDialog.BUTTON_POSITIVE)!!
                    .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
                dialog?.getButton(DatePickerDialog.BUTTON_NEGATIVE)!!
                    .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))

        }
        loadStatisticstoUI()
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)
        // TODO: Use the ViewModel
    }
    fun loadStatisticstoUI(){
        statsList.removeAllViews()
        statsList2.removeAllViews()
        for(tag in viewModel.tags){
            val tagStats : Pair<String, String> = viewModel.loadStatistics(tag)
            val textView = TextView(activity)
            textView.text = tagStats.first
            textView.textSize= 17F
            textView.gravity = Gravity.LEFT
            textView.setTextColor(Color.parseColor("#000000"));
            statsList.addView(textView)
            val textView2 = TextView(activity)
            textView2.text = tagStats.second
            textView2.textSize= 17F
            textView2.gravity = Gravity.RIGHT
            textView2.setTextColor(Color.parseColor("#000000"));
            statsList2.addView(textView2)
        }
    }
}
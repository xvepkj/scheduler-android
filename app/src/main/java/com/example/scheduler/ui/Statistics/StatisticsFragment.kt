package com.example.scheduler.ui.Statistics

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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
        addTag.setOnClickListener{
                val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(context)
                builder.setTitle("New Tag")

                val input = EditText(context)

                input.hint = "   Enter Tag name"
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    var newTagName = input.text.toString()
                    Log.d("DBG",newTagName)
                    viewModel.addTag(newTagName)
                    Log.d("DBG",viewModel.tags.toString())
                })
                builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

                builder.show()
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
            val tagStats : Pair<String,String> = viewModel.loadStatistics(tag)
            val textView = TextView(activity)
            textView.text = tagStats.first
            textView.textSize= 20F
            textView.gravity = Gravity.LEFT
            textView.setTextColor(Color.parseColor("#000000"));
            statsList.addView(textView)
            val textView2 = TextView(activity)
            textView2.text = tagStats.second
            textView2.textSize= 20F
            textView2.gravity = Gravity.RIGHT
            textView2.setTextColor(Color.parseColor("#000000"));
            statsList2.addView(textView2)
        }
    }
}
package com.xve.scheduler.ui.Statistics

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xve.scheduler.MainActivity
import com.xve.scheduler.R


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
  private lateinit var stats_spinner : Spinner

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root =inflater.inflate(R.layout.statistics_fragment, container, false)

    (activity as MainActivity?)?.supportActionBar?.title = "Statistics"

    viewModel = ViewModelProvider(requireActivity()).get(StatisticsViewModel::class.java)

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
    viewModel.tags.forEachIndexed { index, tag ->
      if (tag.isActive) {

        val tagStats : Pair<String, String> = viewModel.loadStatistics(index, numDays)
        val view: View = layoutInflater.inflate(R.layout.stats_item, null)

        val textView = view.findViewById<TextView>(R.id.name_field)
        textView.apply {
          text = tagStats.first
          textSize= 17F
          typeface = ResourcesCompat.getFont(requireContext(), R.font.biorhyme_light)
          gravity = Gravity.LEFT
          setTextColor(Color.parseColor("#000000"))
        }

        val textView2 = view.findViewById<TextView>(R.id.statistics_field)
        textView2.apply {
          text = tagStats.second
          gravity = Gravity.RIGHT
          textSize= 17F
          typeface = ResourcesCompat.getFont(requireContext(), R.font.biorhyme_light)
          setTextColor(Color.parseColor("#000000"));
        }

        statsList.addView(view)
      }
    }
  }
}
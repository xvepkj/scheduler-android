package com.example.scheduler.ui.home

import android.app.DatePickerDialog
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.scheduler.MainActivity
import com.example.scheduler.R
import com.example.scheduler.core.Date
import com.example.scheduler.core.ScheduledEvent

class HomeFragment : Fragment() {

  companion object {
    fun newInstance() = HomeFragment()
  }

  private lateinit var viewModel: HomeViewModel

  private lateinit var linearLayout: LinearLayout

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.home_fragment, container, false)

    // Initialize UI elements
    linearLayout = root.findViewById(R.id.homeLinearLayout)

    setHasOptionsMenu(true)
    (activity as MainActivity?)?.supportActionBar?.title = "Hello"

    // viewModel related stuff
    viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    viewModel.schedule.observe(viewLifecycleOwner, Observer<List<ScheduledEvent>> { schedule -> loadScheduleToUI(schedule)})
    loadSchedule(Date.current())
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel

  }

  @RequiresApi(Build.VERSION_CODES.N)
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.calendar -> {
        val datePicker = context?.let { DatePickerDialog(it) }
        datePicker?.setOnDateSetListener { _, year, month, day ->
          val d = Date(day, month + 1, year)
          loadSchedule(d)
        }
        datePicker?.show()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    // Inflate the menu; this adds items to the action bar if it is present.
    inflater.inflate(R.menu.home_menu, menu)
  }

  fun loadSchedule(d: Date) {
    (activity as MainActivity?)?.supportActionBar?.title = d.toString()
    viewModel.loadSchedule(d)
  }

  // Load schedule to UI
  fun loadScheduleToUI(schedule: List<ScheduledEvent>) {
    linearLayout.removeAllViews()
    for (event in schedule) {
      val t = TextView(activity)
      t.text = event.toString()
      linearLayout.addView(t)
    }
  }
}
package com.example.scheduler.ui.home

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.scheduler.MainActivity
import com.example.scheduler.R
import com.example.scheduler.core.Date
import com.example.scheduler.core.EventType
import com.example.scheduler.core.ScheduledEvent
import com.example.scheduler.ui.logger.LoggerViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text

class HomeFragment : Fragment() {

  companion object {
    fun newInstance() = HomeFragment()
    var fromhome : Boolean = false
    var selecteddate : Date = Date.current()
  }

  private lateinit var viewModel: HomeViewModel

  private lateinit var linearLayout: LinearLayout
  private lateinit var addcustomevent : FloatingActionButton
  private lateinit var loggerViewModel: LoggerViewModel

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
    loggerViewModel = ViewModelProvider(requireActivity()).get(LoggerViewModel::class.java)

    viewModel.schedule.observe(viewLifecycleOwner, Observer<List<ScheduledEvent>> { schedule -> loadScheduleToUI(schedule) })
    loadSchedule(Date.current())
    selecteddate = Date.current()
    addcustomevent = root.findViewById(R.id.addcustomevent)
    addcustomevent.setOnClickListener{
      fromhome = true
      findNavController().navigate(R.id.action_homeFragment_to_eventAddFragment)
    }
    createChannel(getString(R.string.default_channel_id), "channelName")

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
        val datePicker = context?.let { DatePickerDialog(it,R.style.DialogTheme) }
        datePicker?.setOnDateSetListener { _, year, month, day ->
          val d = Date(day, month + 1, year)
          selecteddate = d
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
    for (i in schedule.indices) {
      val event = schedule[i]
      val view: View = layoutInflater.inflate(R.layout.event, null)
      val eventname = view.findViewById<TextView>(R.id.eventdetails)
      val starttime = view.findViewById<TextView>(R.id.starttime)
      val endtime = view.findViewById<TextView>(R.id.endtime)
      val crossbutton = view.findViewById<ImageButton>(R.id.removeevent)
      val tracked_checkbutton = view.findViewById<CheckBox>(R.id.tracked_checkbox)
      val log_progress_text = view.findViewById<TextView>(R.id.log_progress_text)
      eventname.text = event.name
      starttime.text = event.startTime.toString()
      endtime.text = event.endTime.toString()
      if(selecteddate!=Date.current() && event.index == -1)
          crossbutton.visibility= GONE
      if(event.eventType==EventType.TRACKED){
        tracked_checkbutton.visibility = VISIBLE
        if(selecteddate!=Date.current())
          tracked_checkbutton.isEnabled=false
      } else if (event.eventType == EventType.LOGGED) {
        log_progress_text.visibility = VISIBLE
        log_progress_text.setText(event.log_progress.toString())
      }
      tracked_checkbutton.isChecked = event.completed==1
      tracked_checkbutton.setOnClickListener {
        Log.d("DBG",tracked_checkbutton.isChecked.toString())
        viewModel.updateEvent(i,if(tracked_checkbutton.isChecked) 1 else 0)
        loadSchedule(Date.current())
      }
      log_progress_text.setOnClickListener {
        viewModel.logged_event_index = i
        findNavController().navigate(R.id.action_homeFragment_to_loggerFragment)
      }
      crossbutton.setOnClickListener{
          if(selecteddate == Date.current())
            viewModel.removeEvent(i,selecteddate)
          else
            viewModel.removeEvent(event.index, selecteddate)
          selecteddate= Date.current()
          loadSchedule(Date.current())
      }
      linearLayout.addView(view)
    }
  }

  private fun createChannel(channelId: String, channelName: String) {
    // TODO: Step 1.6 START create a channel
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationChannel = NotificationChannel(
              channelId,
              channelName,
              // TODO: Step 2.4 change importance
              NotificationManager.IMPORTANCE_HIGH
      )
      // TODO: Step 2.6 disable badges for this channel

      notificationChannel.enableLights(true)
      notificationChannel.lightColor = Color.RED
      notificationChannel.enableVibration(true)
      notificationChannel.description = "Schedule Stuff"

      val notificationManager = requireActivity().getSystemService(
              NotificationManager::class.java
      )
      notificationManager.createNotificationChannel(notificationChannel)
    }
    // TODO: Step 1.6 END create channel
  }

}
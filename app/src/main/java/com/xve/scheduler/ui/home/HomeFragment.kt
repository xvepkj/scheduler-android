package com.xve.scheduler.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.graphics.PorterDuff
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.xve.scheduler.MainActivity
import com.xve.scheduler.R
import com.xve.scheduler.core.Date
import com.xve.scheduler.core.EventType
import com.xve.scheduler.core.ScheduledEvent
import com.xve.scheduler.ui.logger.LoggerViewModel
import com.xve.scheduler.ui.tags.TagsViewModel
import com.xve.scheduler.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

  companion object {
    fun newInstance() = HomeFragment()
    var fromhome : Boolean = false
    var customToFuture : Boolean = false
    var selecteddate : Date = Date.current()
  }

  private var _binding: HomeFragmentBinding? = null
  private val binding get() = _binding!!

  private lateinit var viewModel: HomeViewModel

  // For obtaining tag info
  private lateinit var tagsViewModel: TagsViewModel
  private lateinit var loggerViewModel: LoggerViewModel

  override fun onCreateView(
          inflater: LayoutInflater, container: ViewGroup?,
          savedInstanceState: Bundle?
  ): View? {
    _binding = HomeFragmentBinding.inflate(inflater, container, false)
    // Initialize UI elements

    setHasOptionsMenu(true)

    // viewModel related stuff
    viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
    loggerViewModel = ViewModelProvider(requireActivity()).get(LoggerViewModel::class.java)
    tagsViewModel = ViewModelProvider(requireActivity()).get(TagsViewModel::class.java)

    viewModel.schedule.observe(viewLifecycleOwner, Observer<List<ScheduledEvent>> { schedule -> loadScheduleToUI(schedule) })
    if(customToFuture) {
      loadSchedule(selecteddate)
      customToFuture = false
    }
    else {
      loadSchedule(Date.current())
      selecteddate = Date.current()
    }
    binding.addcustomevent.setOnClickListener{
      fromhome = true
      if(selecteddate!= Date.current())
        customToFuture = true
      findNavController().navigate(R.id.action_homeFragment_to_eventAddFragment)
    }
    createChannel(getString(R.string.default_channel_id), "channelName")

    return binding.root
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
        datePicker?.let {
          it.show()
          it.getButton(DatePickerDialog.BUTTON_POSITIVE)!!
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
          it.getButton(DatePickerDialog.BUTTON_NEGATIVE)!!
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
        }
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
  @SuppressLint("NewApi")
  fun loadScheduleToUI(schedule: List<ScheduledEvent>) {
    binding.homeLinearLayout.removeAllViews()
    for (i in schedule.indices) {
      val event = schedule[i]
      val view: View = layoutInflater.inflate(R.layout.event_home, null)
      val eventname = view.findViewById<TextView>(R.id.eventdetails)
      val starttime = view.findViewById<TextView>(R.id.starttime)
      val endtime = view.findViewById<TextView>(R.id.endtime)
      val crossbutton = view.findViewById<ImageButton>(R.id.removeevent)
      val tracked_checkbutton = view.findViewById<CheckBox>(R.id.tracked_checkbox)
      val log_progress_text = view.findViewById<TextView>(R.id.log_progress_text)

      // tag info if tracked/logged
      val tagColor = view.findViewById<ImageButton>(R.id.event_tag_color)
      tagColor.visibility = View.GONE
      if (event.eventType != EventType.UNTRACKED) {
        tagColor.visibility = View.VISIBLE
        var tag = tagsViewModel.get(event.tagId)
        if (!tag.isActive) tag = tagsViewModel.get(0)
        tagColor.setColorFilter(
          tag.color,
          PorterDuff.Mode.SRC_ATOP)
      }

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
        val number = (event.log_progress*100.0)
        val number3digits:Double = String.format("%.3f", number).toDouble()
        val number2digits:Double = String.format("%.2f", number3digits).toDouble()
        val solution: Int = String.format("%.1f", number2digits).toDouble().toInt()
        log_progress_text.visibility = VISIBLE
        if(selecteddate!=Date.current())
          log_progress_text.isEnabled=false
        log_progress_text.text = "$solution%"
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
      binding.homeLinearLayout.addView(view)
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

      notificationChannel.apply {
        enableLights(true)
        lightColor = Color.RED
        enableVibration(true)
        description = "Schedule Stuff"
      }

      val notificationManager = requireActivity().getSystemService(
              NotificationManager::class.java
      )
      notificationManager.createNotificationChannel(notificationChannel)
    }
    // TODO: Step 1.6 END create channel
  }

}
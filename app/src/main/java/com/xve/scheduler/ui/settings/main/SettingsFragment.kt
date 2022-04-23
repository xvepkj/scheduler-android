package com.xve.scheduler.ui.settings.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.xve.scheduler.MainActivity
import com.xve.scheduler.R
import com.xve.scheduler.intro.SchedulerIntro
import com.xve.scheduler.ui.home.HomeViewModel

class SettingsFragment : Fragment() {

  private lateinit var viewModel: SettingsViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    (activity as MainActivity?)?.supportActionBar?.title = "Settings"
    val root = inflater.inflate(R.layout.settings_fragment, container, false)

    val about : ImageView = root.findViewById(R.id.imageView3)
    val tutorial : ImageView = root.findViewById(R.id.imageView4)

    about.setOnClickListener{ findNavController().navigate(R.id.action_settingsFragment_to_blankFragment) }

    tutorial.setOnClickListener{ startActivity(Intent(activity, SchedulerIntro::class.java)) }

    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
    // TODO: Use the ViewModel
  }
}
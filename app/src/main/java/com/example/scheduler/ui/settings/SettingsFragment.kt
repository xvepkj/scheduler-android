package com.example.scheduler.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.scheduler.R

class SettingsFragment : Fragment() {

  companion object {
    fun newInstance() = SettingsFragment()
  }

  private lateinit var viewModel: SettingsViewModel

  private lateinit var button: Button

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.settings_fragment, container, false)
    val button:Button = root.findViewById(R.id.button)
    button.setOnClickListener {
      findNavController().navigate(R.id.action_settingsFragment_to_templatePoolFragment)
    }
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
    // TODO: Use the ViewModel
  }

}
package com.example.scheduler.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.scheduler.R

class SettingsFragment : Fragment() {

  companion object {
    fun newInstance() = SettingsFragment()
  }

  private lateinit var viewModel: SettingsViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.settings_fragment, container, false)
    val textView = root.findViewById<TextView>(R.id.settingsTextView)
    var cnt = 1
    textView.setOnClickListener {
      Toast.makeText(context, "Count $cnt", Toast.LENGTH_SHORT).show()
      cnt++
    }
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
    // TODO: Use the ViewModel
  }

}
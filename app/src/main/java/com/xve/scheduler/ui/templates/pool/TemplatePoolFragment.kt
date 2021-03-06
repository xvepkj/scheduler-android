package com.xve.scheduler.ui.templates.pool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xve.scheduler.MainActivity
import com.xve.scheduler.R
import com.xve.scheduler.core.ActiveTemplate
import com.xve.scheduler.ui.home.HomeViewModel

class TemplatePoolFragment : Fragment() {

  companion object {
    fun newInstance() = TemplatePoolFragment()
  }

  private lateinit var viewModel: TemplatePoolViewModel
  private lateinit var homeViewModel: HomeViewModel

  private lateinit var linearLayout: LinearLayout

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.template_pool_fragment, container, false)
    (activity as MainActivity?)?.supportActionBar?.title = "Active Templates"

    linearLayout = root.findViewById(R.id.templatePoolLinearLayout)
    viewModel = ViewModelProvider(this).get(TemplatePoolViewModel::class.java)
    homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

    showList(homeViewModel.worker.getPool())

    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel
  }

  fun showList(pool: List<ActiveTemplate>) {
    linearLayout.removeAllViews()
    for (i in pool.indices) {
      val activeTemplate : ActiveTemplate = pool[i]
      val view: View = layoutInflater.inflate(R.layout.active_template_item, null)
      val t = view.findViewById<TextView>(R.id.eventdetails)
      val starttime = view.findViewById<TextView>(R.id.starttime)
      val endtime = view.findViewById<TextView>(R.id.endtime)
      val crossbutton = view.findViewById<ImageButton>(R.id.removeevent)
      t.text = activeTemplate.toString()
      crossbutton.setOnClickListener{
        homeViewModel.removeFromPool(i)
        showList(homeViewModel.worker.getPool())
      }
      linearLayout.addView(view)
    }
  }

}
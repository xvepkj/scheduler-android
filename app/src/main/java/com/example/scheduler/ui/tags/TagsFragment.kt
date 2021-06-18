package com.example.scheduler.ui.tags

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.scheduler.MainActivity
import com.example.scheduler.R
import com.example.scheduler.core.Tag


class TagsFragment : Fragment() {

  companion object {
    fun newInstance() = TagsFragment()
  }

  private lateinit var viewModel: TagsViewModel

  private lateinit var linearLayout: LinearLayout
  private lateinit var addButton: Button

  // DialogView for use in dialog builder
  private lateinit var dialogView: View

  // Dialog elements
  private lateinit var spinner: Spinner
  private lateinit var nameInput: EditText

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.tags_fragment, container, false)
    (activity as MainActivity?)?.supportActionBar?.title = "Tags"
    linearLayout = root.findViewById(R.id.tag_lin_layout)
    addButton = root.findViewById(R.id.tag_add_btn)

    viewModel = ViewModelProvider(this).get(TagsViewModel::class.java)

    addButton.setOnClickListener {
      showDialog(
        "",
        Color.WHITE,
        "Add tag",
        positiveCallback = { dialog, which ->
          val tagName = nameInput.text.toString()
          val tagColor = spinner.selectedItem as Int
          Log.d("DBG", "${tagName}, ${tagColor}")
          viewModel.add(Tag(tagName, tagColor))
          loadTagList()
        }
      )
    }

    loadTagList()
    return root
  }

  fun showDialog(
    tagName: String,
    tagColor: Int,
    dialogTitle: String,
    positiveCallback: (DialogInterface, Int) -> Unit
  ) {
    // Setup elements
    dialogView = layoutInflater.inflate(R.layout.custom_dialog_box_addtag, null)
    spinner = dialogView.findViewById(R.id.tag_select_color)
    nameInput = dialogView.findViewById(R.id.tag_name_input)

    // Customize DialogView
    // setup spinner
    val array: Array<Int> = arrayOf(
      ResourcesCompat.getColor(resources, R.color.WeldonBlue, null),
      ResourcesCompat.getColor(resources, R.color.ElectricBrown, null),
      ResourcesCompat.getColor(resources, R.color.CyberGrape, null),
      ResourcesCompat.getColor(resources, R.color.Melon, null),
      ResourcesCompat.getColor(resources, R.color.Flavescent, null),
      ResourcesCompat.getColor(resources, R.color.Cornsilk, null),
      ResourcesCompat.getColor(resources, R.color.MagicMint, null),
      ResourcesCompat.getColor(resources, R.color.DeepSaffron, null),
      ResourcesCompat.getColor(resources, R.color.LightGold, null),
      ResourcesCompat.getColor(resources, R.color.PhilippineSilver, null),
      ResourcesCompat.getColor(resources, R.color.Deer, null),
      ResourcesCompat.getColor(resources, R.color.Bone, null),
      ResourcesCompat.getColor(resources, R.color.PaleRobinEggBlue, null)
    )
    val customAdapter = object : BaseAdapter() {
      override fun getCount(): Int {
        return array.size
      }

      override fun getItem(position: Int): Any {
        return array[position]
      }

      override fun getItemId(position: Int): Long {
        return position.toLong()
      }

      override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view : TextView = layoutInflater.inflate(R.layout.spinner_item, null) as TextView
        view.setBackgroundColor(array[position])
        return view
      }
    }
    /*
    val adapter = ArrayAdapter<Int>(
      activity?.applicationContext!!,R.layout.spinner_main, array
    )*/
    spinner.adapter = customAdapter
    // Given color should be selected
    // TODO: Fix
    var colorPos = 0
    for (i in array.indices) {
      if (array[i] == tagColor) {
        colorPos = i
        break
      }
    }
    spinner.setSelection(colorPos)

    nameInput.hint = "Enter Tag name"
    nameInput.inputType = InputType.TYPE_CLASS_TEXT
    nameInput.setText(tagName)

    // Build dialog box containing DialogView
    val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(context)
    builder.setTitle(dialogTitle)
    builder.setView(dialogView)
    builder.setPositiveButton("OK", positiveCallback)
    builder.setNegativeButton("Cancel", { dialog, which -> dialog.cancel() })

    val dialog = builder.create()
    dialog.show()

    // Aesthetic
    dialog?.getButton(DatePickerDialog.BUTTON_POSITIVE)!!
      .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
    dialog?.getButton(DatePickerDialog.BUTTON_NEGATIVE)!!
      .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
  }

  @SuppressLint("NewApi")
  fun loadTagList() {
    linearLayout.removeAllViews()
    viewModel.tags.forEachIndexed { index, tag ->
      if (tag.isActive) {
        val view: View = layoutInflater.inflate(R.layout.tag, null)
        val framelayout : ConstraintLayout = view.findViewById(R.id.frameLayout11)
        val tagNameTextView = view.findViewById<TextView>(R.id.tag_name)
        val editButton = view.findViewById<ImageButton>(R.id.tag_edit_button)
        val removeButton = view.findViewById<ImageButton>(R.id.tag_remove_button)

        // Edit and remove buttons should not be present on Misc tag
        if (index == 0) {
          editButton.visibility = View.GONE
          removeButton.visibility = View.GONE
        }

        editButton.setOnClickListener {
          showDialog(
            tag.name,
            tag.color,
            "Edit tag",
            positiveCallback = { dialog, which ->
              val tagName = nameInput.text.toString()
              val tagColor = spinner.selectedItem as Int
              viewModel.modify(index, Tag(tagName, tagColor))
              loadTagList()
            }
          )
        }
        removeButton.setOnClickListener {
          val builder = AlertDialog.Builder(context)
          builder.setMessage("Are you sure?")
          builder.setPositiveButton("Yes", { dialog, which ->
              viewModel.remove(index)
              loadTagList()
            })
          builder.setNegativeButton("No", { dialog, which -> dialog.cancel() })
          val dialog = builder.create()
          dialog.show()
          // Aesthetic
          dialog?.getButton(DatePickerDialog.BUTTON_POSITIVE)!!
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
          dialog?.getButton(DatePickerDialog.BUTTON_NEGATIVE)!!
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
        }
        tagNameTextView.setText(tag.name)
        val framebackground = DrawableCompat.wrap(framelayout.background)
        DrawableCompat.setTint(framebackground,tag.color);
        linearLayout.addView(view)
      }
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel
  }
}
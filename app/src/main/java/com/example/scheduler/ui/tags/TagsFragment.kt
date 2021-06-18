package com.example.scheduler.ui.tags

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.opengl.Visibility
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
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

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.tags_fragment, container, false)

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

  fun showDialog (
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
      Color.RED,
      Color.BLUE,
      Color.GREEN
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
        val view = layoutInflater.inflate(R.layout.spinner_item, null) as TextView
        view.setBackgroundColor(array[position])
        view.setText(array[position].toString())
        return view
      }

    }
    /*
    val adapter = ArrayAdapter<Int>(
      activity?.applicationContext!!,R.layout.spinner_main, array
    )
    adapter.setDropDownViewResource(R.layout.spinner_item);
     */
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
    builder.setNegativeButton( "Cancel", { dialog, which -> dialog.cancel() })

    val dialog = builder.create()
    dialog.show()

    // Aesthetic
    dialog?.getButton(DatePickerDialog.BUTTON_POSITIVE)!!
      .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
    dialog?.getButton(DatePickerDialog.BUTTON_NEGATIVE)!!
      .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
  }

  fun loadTagList() {
    linearLayout.removeAllViews()
    viewModel.tags.forEachIndexed { index, tag ->
      if (tag.isActive) {
        val view: View = layoutInflater.inflate(R.layout.tag, null)
        val tagNameTextView = view.findViewById<TextView>(R.id.tag_name)
        val tagColorTextView = view.findViewById<TextView>(R.id.tag_color_view)
        val editButton = view.findViewById<ImageButton>(R.id.tag_edit_button)
        val removeButton = view.findViewById<ImageButton>(R.id.tag_remove_button)

        // Edit and remove buttons should not be present on Misc tag
        if (index == 0) {
          editButton.visibility = View.GONE
          removeButton.visibility = View.GONE
        }

        editButton.setOnClickListener {
          Toast.makeText(context, "Edit Tag: ${index}", Toast.LENGTH_SHORT).show()
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
            .setPositiveButton("Yes", { dialog, which ->
              viewModel.remove(index)
              loadTagList()
            })
            .setNegativeButton("No", { dialog, which -> dialog.cancel() })
            .show()
        }
        tagNameTextView.setText(tag.name)
        tagColorTextView.setBackgroundColor(tag.color)
        linearLayout.addView(view)
      }
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // TODO: Use the ViewModel
  }

}
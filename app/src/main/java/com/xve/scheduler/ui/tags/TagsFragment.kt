package com.xve.scheduler.ui.tags

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
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
import com.xve.scheduler.MainActivity
import com.xve.scheduler.R
import com.xve.scheduler.core.Tag


class TagsFragment : Fragment() {

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
        dialogTitle = "Add tag",
        positiveCallback = { dialog, which ->
          viewModel.add(Tag(name = nameInput.text.toString(), color = spinner.selectedItem as Int))
          loadTagList()
        }
      )
    }

    loadTagList()
    return root
  }

  fun showDialog(
    tagName: String = "",
    tagColor: Int = Color.WHITE,
    dialogTitle: String,
    positiveCallback: (DialogInterface, Int) -> Unit
  ) {
    // Setup elements
    dialogView = layoutInflater.inflate(R.layout.custom_dialog_box_addtag, null)
    spinner = dialogView.findViewById(R.id.tag_select_color)
    nameInput = dialogView.findViewById(R.id.tag_name_input)

    // Customize DialogView
    // setup spinner

    val arrayColors: Array<Int> = getColors()

    val customAdapter = object : BaseAdapter() {

      override fun getCount() = arrayColors.size

      override fun getItem(position: Int) = arrayColors[position]

      override fun getItemId(position: Int) = position.toLong()

      override fun getView(position: Int, convertView: View?, parent: ViewGroup?) =
        (layoutInflater.inflate(R.layout.spinner_item, null) as TextView).apply {
          setBackgroundColor(arrayColors[position])
        }
    }

    spinner.adapter = customAdapter
    // Given color should be selected
    // TODO: Fix
    var colorPos = 0
    for (i in arrayColors.indices)
      if (arrayColors[i] == tagColor) {
        colorPos = i
        break
      }

    spinner.setSelection(colorPos)

    nameInput.apply {
      hint = "Enter Tag name"
      InputType.TYPE_CLASS_TEXT
      setText(tagName)
    }

    // Build dialog box containing DialogView
    AlertDialog.Builder(context). apply {
      setTitle(dialogTitle)
      setView(dialogView)
      setPositiveButton("OK", positiveCallback)
      setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
      val dialog = create()
      dialog.apply {
        show()
        getButton(DatePickerDialog.BUTTON_POSITIVE)!!
          .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
        getButton(DatePickerDialog.BUTTON_NEGATIVE)!!
          .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
      }
    }
  }

  @SuppressLint("NewApi")
  fun loadTagList() {
    linearLayout.removeAllViews()
    viewModel.tags.forEachIndexed { index, tag ->
      if (tag.isActive) {
        val view: View = layoutInflater.inflate(R.layout.tag, null)
        val frameLayout : ConstraintLayout = view.findViewById(R.id.frameLayout11)
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
          AlertDialog.Builder(context).apply {
            setMessage("Are you sure?")
            setPositiveButton("Yes") { dialog, which ->
              viewModel.remove(index)
              loadTagList()
              setNegativeButton("No") { dialog, which -> dialog.cancel() }
              val dialog = create()
              dialog.apply {
                dialog.show()
                getButton(DatePickerDialog.BUTTON_POSITIVE)!!
                  .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
                getButton(DatePickerDialog.BUTTON_NEGATIVE)!!
                  .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
              }
            }
          }
        }
        tagNameTextView.text = tag.name
        DrawableCompat.setTint(DrawableCompat.wrap(frameLayout.background),tag.color)
        linearLayout.addView(view)
      }
    }
  }

  private fun getColors() : Array<Int> {
    val arrayColorIds = arrayOf(R.color.WeldonBlue, R.color.ElectricBrown, R.color.CyberGrape, R.color.Melon, R.color.Flavescent, R.color.Cornsilk, R.color.MagicMint,
      R.color.DeepSaffron,R.color.LightGold,  R.color.PhilippineSilver,  R.color.Deer, R.color.Bone, R.color.PaleRobinEggBlue)
    val arrayNew = mutableListOf<Int>()
    for(id in arrayColorIds)
      arrayNew.add(ResourcesCompat.getColor(resources,id, null))
    return arrayNew.toTypedArray()
  }
}
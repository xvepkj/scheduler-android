package com.example.scheduler.ui.tags

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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
    private lateinit var removeButton: Button
    private lateinit var editButton: Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.tags_fragment, container, false)

        linearLayout = root.findViewById(R.id.tag_lin_layout)
        addButton = root.findViewById(R.id.tag_add_btn)
        removeButton = root.findViewById(R.id.tag_remove_btn)
        editButton = root.findViewById(R.id.tag_edit_btn)

        viewModel = ViewModelProvider(this).get(TagsViewModel::class.java)

        addButton.setOnClickListener{
            val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(context)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.custom_dialog_box_edittext, null)
            builder.setTitle("New Tag")

            val input : EditText = dialogView.findViewById(R.id.edit1)

            input.hint = "Enter Tag name"
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(dialogView)

            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                var newTagName = input.text.toString()
                Log.d("DBG", newTagName)
                viewModel.add(Tag(newTagName))
                loadTagList()
                Log.d("DBG", viewModel.tags.toString())
            })
            builder.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            val dialog = builder.create()
            dialog.show()
            dialog?.getButton(DatePickerDialog.BUTTON_POSITIVE)!!
                .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
            dialog?.getButton(DatePickerDialog.BUTTON_NEGATIVE)!!
                .setTextColor(ContextCompat.getColor(requireActivity(), R.color.dark_pink))
        }

        loadTagList()
        return root
    }

    fun loadTagList() {
        linearLayout.removeAllViews()
        viewModel.tags.forEachIndexed { index, tag ->
            val textView = TextView(context)
            val label = "${index} => ${tag.name}"
            Log.d("DBG", label)
            textView.setText(label)
            linearLayout.addView(textView)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}
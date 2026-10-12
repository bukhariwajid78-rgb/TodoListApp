package com.choubapp.practice

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TaskAdapter(
    context: Context,
    private val tasks: ArrayList<Task>,
    private val onDeleteClick: (Int) -> Unit,
    private val onTaskChanged: () -> Unit
) : ArrayAdapter<Task>(context, 0, tasks) {

    override fun getView(

        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_task, parent, false)

        val checkTask = view.findViewById<CheckBox>(R.id.checkTask)
        checkTask.isFocusable = false
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        val task = tasks[position]
        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        checkTask.setOnCheckedChangeListener(null)

        checkTask.text = task.title
        checkTask.isChecked = task.isCompleted

        if (task.isCompleted) {
            checkTask.paintFlags =
                checkTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            checkTask.paintFlags =
                checkTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        checkTask.setOnCheckedChangeListener { _, isChecked ->

            task.isCompleted = isChecked

            if (isChecked) {
                checkTask.paintFlags =
                    checkTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                checkTask.paintFlags =
                    checkTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            onTaskChanged()
        }

        btnDelete.setOnClickListener {
            onDeleteClick(position)
        }
        btnEdit.setOnClickListener {
            (context as AppCompatActivity).let { activity ->
                val editText = EditText(activity)
                editText.setText(task.title)

                androidx.appcompat.app.AlertDialog.Builder(activity)
                    .setTitle("Edit Task")
                    .setView(editText)
                    .setPositiveButton("Update") { _, _ ->
                        val newTitle = editText.text.toString().trim()

                        if (newTitle.isNotEmpty()) {
                            task.title = newTitle
                            onTaskChanged()
                            notifyDataSetChanged()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

        return view
    }
}
package com.choubapp.practice

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox

class TaskAdapter(
    context: Context,
    private val tasks: ArrayList<Task>,
    private val onToggle: (Task, Boolean) -> Unit,
    private val onEdit: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : ArrayAdapter<Task>(context, 0, tasks) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_task, parent, false)

        val checkTask = view.findViewById<CheckBox>(R.id.checkTask)
        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        val task = tasks[position]

        checkTask.isFocusable = false
        checkTask.setOnCheckedChangeListener(null)
        checkTask.text = task.title
        checkTask.isChecked = task.isCompleted
        setStrikeThrough(checkTask, task.isCompleted)

        checkTask.setOnCheckedChangeListener { _, isChecked ->
            setStrikeThrough(checkTask, isChecked)
            onToggle(task, isChecked)
        }

        btnEdit.setOnClickListener { onEdit(task) }
        btnDelete.setOnClickListener { onDelete(task) }

        return view
    }

    private fun setStrikeThrough(checkBox: CheckBox, on: Boolean) {
        checkBox.paintFlags = if (on) {
            checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}
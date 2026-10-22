package com.choubapp.practice

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var etTask: EditText
    private lateinit var btnAdd: Button
    private lateinit var listViewTasks: ListView
    private lateinit var txtTaskCount: TextView
    private lateinit var taskProgress: ProgressBar
    private lateinit var adapter: TaskAdapter
    private lateinit var dao: TaskDao

    private val tasks = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Get the DAO from the database
        dao = AppDatabase.getInstance(this).taskDao()

        // 2. Find the views
        etTask = findViewById(R.id.edttext)
        btnAdd = findViewById(R.id.btnAdd)
        listViewTasks = findViewById(R.id.listViewTasks)
        txtTaskCount = findViewById(R.id.txtTaskCount)
        taskProgress = findViewById(R.id.taskProgress)
        listViewTasks.emptyView = findViewById(R.id.emptyTaskMessage)

        // 3. Set up the adapter: each click saves to Room
        adapter = TaskAdapter(
            this,
            tasks,
            onToggle = { task, checked ->
                lifecycleScope.launch {
                    dao.update(task.copy(isCompleted = checked))
                }
            },
            onEdit = { task -> showEditDialog(task) },
            onDelete = { task ->
                lifecycleScope.launch {
                    dao.delete(task)
                }
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            }
        )
        listViewTasks.adapter = adapter

        // Tapping a row also opens the edit dialog
        listViewTasks.setOnItemClickListener { _, _, position, _ ->
            showEditDialog(tasks[position])
        }

        // 4. Add button: insert a new task
        btnAdd.setOnClickListener {
            val taskTitle = etTask.text.toString().trim()

            if (taskTitle.isEmpty()) {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    dao.insert(Task(title = taskTitle))
                }
                etTask.text.clear()
            }
        }

        // 5. Listen to the database: runs every time tasks change
        lifecycleScope.launch {
            dao.getAllTasks().collect { newTasks ->
                tasks.clear()
                tasks.addAll(newTasks)
                adapter.notifyDataSetChanged()
                updateTaskCount()
            }
        }
    }

    private fun updateTaskCount() {
        val total = tasks.size
        val completed = tasks.count { it.isCompleted }

        txtTaskCount.text = "$total tasks · $completed completed"
        taskProgress.progress = if (total == 0) 0 else (completed * 100) / total
    }

    private fun showEditDialog(task: Task) {
        val editText = EditText(this)
        editText.setText(task.title)

        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newTitle = editText.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    lifecycleScope.launch {
                        dao.update(task.copy(title = newTitle))
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
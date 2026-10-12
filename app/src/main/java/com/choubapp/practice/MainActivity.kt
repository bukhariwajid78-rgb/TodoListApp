package com.choubapp.practice

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var etTask: EditText
    private lateinit var btnAdd: Button
    private lateinit var listViewTasks: ListView
    private lateinit var adapter: TaskAdapter
    private val tasks = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val txtTaskCount = findViewById<TextView>(R.id.txtTaskCount)
        val taskProgress = findViewById<ProgressBar>(R.id.taskProgress)
        fun updateTaskCount() {
            val total = tasks.size
            val completed = tasks.count { it.isCompleted }

            txtTaskCount.text = "$total tasks · $completed completed"

            val progress = if (total == 0) {
                0
            } else {
                (completed * 100) / total
            }

            taskProgress.progress = progress
        }

        updateTaskCount()
        val sharedPreferences = getSharedPreferences("TodoApp", MODE_PRIVATE)
        fun saveTasks() {

            val jsonArray = JSONArray()

            for (task in tasks) {

                val jsonObject = JSONObject()

                jsonObject.put("title", task.title)
                jsonObject.put("isCompleted", task.isCompleted)

                jsonArray.put(jsonObject)
            }

            getSharedPreferences("TodoApp", MODE_PRIVATE)
                .edit()
                .putString("tasks", jsonArray.toString())
                .apply()
        }

        fun loadTasks() {

            val sharedPreferences =
                getSharedPreferences("TodoApp", MODE_PRIVATE)

            val savedTasks =
                sharedPreferences.getString("tasks", null)

            if (savedTasks != null) {

                val jsonArray = JSONArray(savedTasks)

                for (i in 0 until jsonArray.length()) {

                    val jsonObject = jsonArray.getJSONObject(i)

                    val title = jsonObject.getString("title")

                    val isCompleted =
                        jsonObject.getBoolean("isCompleted")

                    tasks.add(
                        Task(
                            title,
                            isCompleted
                        )
                    )
                }
            }
        }
        loadTasks()
        etTask = findViewById(R.id.edttext)
        btnAdd = findViewById(R.id.btnAdd)
        listViewTasks = findViewById(R.id.listViewTasks)
        val emptyTaskMessage = findViewById<TextView>(R.id.emptyTaskMessage)
        listViewTasks.emptyView = emptyTaskMessage
        adapter = TaskAdapter(
            this,
            tasks,
            { position ->

                tasks.removeAt(position)

                saveTasks()
                updateTaskCount()

                adapter.notifyDataSetChanged()
                updateTaskCount()
                Toast.makeText(
                    this,
                    "Task deleted",
                    Toast.LENGTH_SHORT
                ).show()
            },
            {
                saveTasks()
                updateTaskCount()
            }
        )

        listViewTasks.adapter = adapter
        btnAdd.setOnClickListener {

            val taskTitle = etTask.text.toString().trim()

            if (taskTitle.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter a task",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val task = Task(taskTitle)

                tasks.add(task)

                saveTasks()

                adapter.notifyDataSetChanged()

                updateTaskCount()
                etTask.text.clear()
                updateTaskCount()

            }
        }
        listViewTasks.setOnItemClickListener { _, _, position, _ ->

            val task = tasks[position]

            val editText = EditText(this)
            editText.setText(task.title)

            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(editText)
                .setPositiveButton("Update") { _, _ ->

                    val newTitle = editText.text.toString().trim()

                    if (newTitle.isNotEmpty()) {
                        task.title = newTitle

                        saveTasks()
                        adapter.notifyDataSetChanged()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

}
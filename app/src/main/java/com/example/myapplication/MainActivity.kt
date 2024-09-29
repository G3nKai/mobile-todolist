import TaskAdapter
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.Task
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private var tasks = mutableListOf<Task>()
    private val gson = Gson()
    private val tasksFileName = "tasks.json"

    // ViewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTasks()

        taskAdapter = TaskAdapter(tasks) { task ->
            saveTasks()
        }

        // Используем binding для доступа к элементам интерфейса
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = taskAdapter

        binding.addTaskButton.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null)
        val input = dialogView.findViewById<EditText>(android.R.id.text1)
        input.hint = "Enter task description"

        AlertDialog.Builder(this)
            .setTitle("New Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val taskDescription = input.text.toString()
                if (taskDescription.isNotBlank()) {
                    val newTask = Task(taskDescription)
                    tasks.add(newTask)
                    taskAdapter.updateTasks(tasks)
                    saveTasks()
                } else {
                    Toast.makeText(this, "Task cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveTasks() {
        val jsonString = gson.toJson(tasks)
        openFileOutput(tasksFileName, MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    private fun loadTasks() {
        val file = File(filesDir, tasksFileName)
        if (file.exists()) {
            val jsonString = file.readText()
            val taskType = object : TypeToken<List<Task>>() {}.type
            tasks = gson.fromJson(jsonString, taskType)
        }
    }
}

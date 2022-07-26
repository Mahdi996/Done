package com.example.done.feature.task.detail

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.done.R
import com.example.done.common.AlarmReceiver
import com.example.done.common.EXTRA_KEY_DATA
import com.example.done.common.formatDate
import com.example.done.common.getFullTime
import com.example.done.data.date.DoneDate
import com.example.done.data.date.TYPE_CREATE
import com.example.done.data.date.TYPE_DEADLINE
import com.example.done.data.date.TYPE_UPDATE
import com.example.done.data.subtask.SubTask
import com.example.done.data.task.Task
import com.example.done.feature.task.detail.deadline.DeadlineFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class TaskDetailActivity : AppCompatActivity(),
    SubTasksInDetailAdapter.SubTasksAdapterOnClickListener,
    AddOrEditSubTask.AddOrEditSubTaskCallback {

    //send task to viewModel for get id to work with subTasks
    private val viewModel: TaskDetailViewModel by viewModel { parametersOf(intent.extras) }

    private lateinit var task: Task

    //for use in AddOrEditSubTask callback
    private var idTask: Int = 0

    private var updateDateId = 0
    private var deadline: DoneDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL


        //bundle
        task = intent.extras!!.getParcelable(EXTRA_KEY_DATA)!!
        idTask = task.id


        //Views
        val addSubTaskBtn = findViewById<MaterialButton>(R.id.addSubTaskBtn)
        val delete = findViewById<MaterialButton>(R.id.detailDeleteTask)
        val note = findViewById<TextView>(R.id.detailNoteTv)
        val subTaskTitleTv = findViewById<TextView>(R.id.subTaskTitleTv)
        val addNoteBtn = findViewById<TextView>(R.id.detailAddNote)
        val save = findViewById<MaterialButton>(R.id.detailButtonSave)
        val titleTaskL = findViewById<TextInputLayout>(R.id.detailTitleTextLayout)
        val titleTask = findViewById<TextInputEditText>(R.id.detailTitleEditText)
        val rv = findViewById<RecyclerView>(R.id.DetailSubTaskRv)
        val createdDate = findViewById<TextView>(R.id.detailCreatedDate)

        val setReminder = findViewById<MaterialButton>(R.id.DetailSetReminder)
        val deleteReminder = findViewById<ImageView>(R.id.detailDeleteReminder)

        //Subs
        viewModel.subs.observe(this) {
            if (it.isNotEmpty())
                subTaskTitleTv.visibility = View.VISIBLE
            else
                subTaskTitleTv.visibility = View.GONE

            rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            rv.adapter = SubTasksInDetailAdapter(it as MutableList<SubTask>, this)
        }

        //TASKS
        //set items
        titleTask.setText(task.title)

        if (task.note.isEmpty()) {
            note.visibility = View.GONE
            addNoteBtn.visibility = View.VISIBLE
        } else note.text = task.note

        //set dates on view
        viewModel.getDates().observe(this@TaskDetailActivity) {

            it.forEach { date ->
                when (date.type) {
                    TYPE_CREATE -> createdDate.text = formatDate(date)
                    TYPE_DEADLINE -> {
                        setReminder.text = getFullTime(date)
                        deleteReminder.visibility = View.VISIBLE
                        deadline = date
                    }
                    TYPE_UPDATE -> updateDateId = date.id
                }
            }
        }

        deleteReminder.setOnClickListener {


            val e = Intent(this@TaskDetailActivity, AlarmReceiver::class.java)

            val p = PendingIntent.getBroadcast(
                this@TaskDetailActivity, deadline!!.alarm,
                e,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.deleteReminder(deadline!!)
                deadline = null
            }
            val alarm =
                this@TaskDetailActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarm.cancel(p)

            deleteReminder.visibility = View.GONE
            setReminder.text = resources.getString(R.string.set_date_time)

            /* lifecycleScope.launch(Dispatchers.IO) {
                 viewModel.editReminder(task.copy(alarm = null))
                 deleteReminder.visibility = View.GONE
                 setReminder.text = resources.getString(R.string.set_date_time)
             }*/
        }

        //when title empty and click save, show error
        //this is for hide error when type a word
        titleTask.doOnTextChanged { _, _, _, count ->
            if (count > 0)
                if (titleTaskL.error != null)
                    titleTask.error = null
        }

        //save task
        save.setOnClickListener {
            //save if title is not empty
            if (titleTask.text.toString().isNotEmpty()) {
                task.title = titleTask.text.toString()

                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.editTask(task, updateDateId)
                    finish()
                }
            } else {
                //if title is empty show error
                titleTaskL.error = "Title can't be Empty"
            }
        }

        val typedValue = TypedValue()
        val theme: Resources.Theme = this.getTheme()
        theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        @ColorInt val color: Int = typedValue.data

        //delete task
        delete.setOnClickListener {
            // start a dialog for confirmation delete task
            val dialog = MaterialAlertDialogBuilder(
                this,
                R.style.RightJustifyTheme
            ).setMessage("این تسک حذف بشه؟")
                .setPositiveButton("حذف") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.deleteTask(task)
                        finish()
                    }
                }
                .setNegativeButton("نه", null).show()
            //change delete color to red
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.red))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(color)
        }


        //add note
        addNoteBtn.setOnClickListener {
            //create view for set in dialog
            val v = LayoutInflater.from(this).inflate(R.layout.fragment_note, null)
            val noteEd = v.findViewById<EditText>(R.id.noteEd)
            noteEd.setText(task.note)
            val noteBtn = v.findViewById<MaterialButton>(R.id.noteSaveBtn)

            //show dialog for add or edit note
            val dialog = MaterialAlertDialogBuilder(this)
                .setView(v)
                .show()
            //when click save in dialog
            noteBtn.setOnClickListener {
                task.note = noteEd.text.toString()
                if (task.note.isNotEmpty()) {
                    note.visibility = View.VISIBLE
                    addNoteBtn.visibility = View.GONE
                    note.text = task.note
                } else {
                    note.visibility = View.GONE
                    addNoteBtn.visibility = View.VISIBLE
                }
                dialog.dismiss()
            }
        }
        //edit note
        note.setOnClickListener {

            val v = LayoutInflater.from(this).inflate(R.layout.fragment_note, null)
            val noteEd = v.findViewById<EditText>(R.id.noteEd)
            noteEd.setText(task.note)
            val noteBtn = v.findViewById<MaterialButton>(R.id.noteSaveBtn)

            //show dialog for add or edit note
            val dialog = MaterialAlertDialogBuilder(this)
                .setView(v)
                .show()

            noteBtn.setOnClickListener {
                task.note = noteEd.text.toString()
                if (task.note.isNotEmpty()) {
                    note.visibility = View.VISIBLE
                    addNoteBtn.visibility = View.GONE
                    note.text = task.note
                } else {
                    note.visibility = View.GONE
                    addNoteBtn.visibility = View.VISIBLE
                }
                dialog.dismiss()
            }
        }

        //add SubTask
        addSubTaskBtn.setOnClickListener {
            AddOrEditSubTask(this).show(supportFragmentManager, "")
        }

        setReminder.setOnClickListener {

            val dialog = DeadlineFragment()

            dialog.show(supportFragmentManager, "Deadline")

            /* lifecycleScope.launch(Dispatchers.IO) {
                 viewModel.setReminder(DoneDate(deadlineDateId, idTask, TYPE_DEADLINE, 1401, 3, 22, 3, 4, 5),
                     this@TaskDetailActivity.createdDate
                 )

             }*/
        }
    }

    //callbacks from SubTask adapter
    override fun onItemChange(subTask: SubTask) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.editSubTask(subTask, updateDateId, task)
        }

    }

    override fun onItemClick(subTask: SubTask) {
        AddOrEditSubTask(this, subTask).show(supportFragmentManager, "")
    }

    override fun onItemDelete(subTask: SubTask) {
        //adapter update with SubTask LiveData and can't remove. delete from DB
        //if don't save detail, add SubTask again
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.deleteSubTask(subTask, updateDateId, task)
        }
    }

    //callback from AddOrEditSubTask
    override fun onFabClick(subTask: SubTask, isAdd: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (isAdd) {
                subTask.tasksId = idTask
                viewModel.addSubTask(subTask, updateDateId, task)
            } else {
                viewModel.editSubTask(subTask, updateDateId, task)
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

        //todo change to SnackBar
        Toast.makeText(this, "Changes Saved", Toast.LENGTH_LONG).show()
    }
}
package com.example.done.feature.task.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.example.done.R
import com.example.done.data.subtask.SubTask
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddOrEditSubTask(
    private val addOrEditSubTaskCallback: AddOrEditSubTaskCallback,
    var subTask: SubTask? = null
) :
    BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_task_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Views
        val fab = view.findViewById<FloatingActionButton>(R.id.addTaskFab)
        fab.hide()
        val titleL = view.findViewById<TextInputLayout>(R.id.addTaskIL)
        val title = view.findViewById<TextInputEditText>(R.id.addTaskED)

        //if open dialog for add SubTask, create a empty SubTask
        if (subTask?.title == null)
            subTask = SubTask(title = "", tasksId = 0)
        //if open dialog for edit, set title
        else
            title.setText(subTask?.title)

        //show fab when somethings write in title editText
        title.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty())
                fab.show()
            else
                fab.hide()
        }

        //save SubTask
        fab.setOnClickListener {
            subTask?.title=title.text.toString()
            addOrEditSubTaskCallback.onFabClick(
                subTask!!,
                //if taskId = 0 mean open dialog for create SubTasks
                subTask!!.tasksId == 0
            )
            dismiss()
        }

    }

    interface AddOrEditSubTaskCallback {
        fun onFabClick(subTask: SubTask, isAdd: Boolean)
    }
}
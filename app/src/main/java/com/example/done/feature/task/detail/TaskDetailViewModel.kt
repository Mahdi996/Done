package com.example.done.feature.task.detail

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.done.common.EXTRA_KEY_DATA
import com.example.done.common.addZero
import com.example.done.common.getTime
import com.example.done.data.date.DateRepository
import com.example.done.data.date.DoneDate
import com.example.done.data.date.TYPE_UPDATE
import com.example.done.data.subtask.SubTask
import com.example.done.data.subtask.SubTaskRepository
import com.example.done.data.task.Task
import com.example.done.data.task.TaskRepository
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    bundle: Bundle?,
    private val subTaskRepository: SubTaskRepository,
    private val taskRepository: TaskRepository,
    private val dateRepository: DateRepository
) : ViewModel() {

    val id = bundle!!.getParcelable<Task>(EXTRA_KEY_DATA)!!.id
    val subs = subTaskRepository.get(id).asLiveData()
    val task = bundle!!.getParcelable<Task>(EXTRA_KEY_DATA)


    fun getDates() =
        dateRepository.get(id).asLiveData()


    suspend fun editTask(task: Task, dateId: Int) {
        viewModelScope.launch {
            val (jalali, utc) = getTime()
            val date = addZero(jalali)
            taskRepository.edit(task.copy(updatedUtc = utc, date = date))
            updateDate(dateId, task.id, jalali)
        }
    }

    suspend fun deleteReminder(doneDate: DoneDate) {
        viewModelScope.launch {
            dateRepository.delete(doneDate)
        }
    }

    suspend fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.delete(task)
            dateRepository.deleteById(task.id)
        }
    }

    suspend fun addSubTask(subTask: SubTask, dateId: Int, task: Task) {
        viewModelScope.launch {
            val (jalali, utc) = getTime()
            val date = addZero(jalali)
            subTaskRepository.add(subTask)
            taskRepository.edit(task.copy(updatedUtc = utc, date = date))
            updateDate(dateId, task.id, jalali)
        }
    }

    suspend fun editSubTask(subTask: SubTask, dateId: Int, task: Task) {
        viewModelScope.launch {
            val (jalali, utc) = getTime()
            val date = addZero(jalali)
            subTaskRepository.edit(subTask)
            taskRepository.edit(task.copy(updatedUtc = utc, date = date))
            updateDate(dateId, task.id, jalali)
        }
    }

    suspend fun deleteSubTask(subTask: SubTask, dateId: Int, task: Task) {
        viewModelScope.launch {
            val (jalali, utc) = getTime()
            val date = addZero(jalali)
            subTaskRepository.delete(subTask)
            taskRepository.edit(task.copy(updatedUtc = utc, date = date))
            updateDate(dateId, task.id, jalali)
        }
    }

    /*suspend fun deleteSubs(idTask: Int, dateId: Int) {
        viewModelScope.launch {
            async { subTaskRepository.deleteSubs(idTask) }
            async { editUpdateDate(dateId) }
        }
    }*/

    private suspend fun updateDate(dateId: Int, taskId: Int, jalali: List<Int>) {
        dateRepository.edit(
            DoneDate(
                dateId,
                taskId,
                TYPE_UPDATE,
                jalali[0],
                jalali[1],
                jalali[2],
                jalali[3],
                jalali[4],
                jalali[5]
            )
        )
    }

    suspend fun addDeadline(doneDate: DoneDate) {
        dateRepository.add(doneDate.copy(taskId = id))
    }

    suspend fun editDeadline(doneDate: DoneDate) {
        dateRepository.edit(doneDate)
    }

    fun getDeadline(taskId: Int): DoneDate = dateRepository.getDeadline(taskId)

}
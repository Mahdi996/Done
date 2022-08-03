package com.example.done.feature.task.list

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.done.common.addZero
import com.example.done.common.getTime
import com.example.done.data.date.DateRepository
import com.example.done.data.date.DoneDate
import com.example.done.data.date.TYPE_CREATE
import com.example.done.data.date.TYPE_UPDATE
import com.example.done.data.setting.SettingContainer.hide
import com.example.done.data.setting.SettingRepository
import com.example.done.data.task.Task
import com.example.done.data.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val dateRepository: DateRepository,
    private val sharedPreferences: SettingRepository
) : ViewModel() {

    val search = MutableStateFlow("")
    val hideCompleted = MutableStateFlow(false)
    val sort = MutableStateFlow(SortTask.BY_NAME)


    /* private val taskFlow = combine(search, hideCompleted, sort) { search, hide, sort ->
          Triple(search, hide, sort)
      }.flatMapLatest { (search, hide, sort) ->
          taskRepository.get(search, hide, sort)
      }*/

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun test(): Flow<List<Task>> {
        val taskFlow = combine(search, hideCompleted, sort) { search, hide, sort ->
            Triple(search, hide, sort)
        }.flatMapLatest { (search, hide, sort) ->
            taskRepository.get(search, hide, sort)
        }
        return taskFlow
    }

    val tasks = test().asLiveData()


    fun saveSettings(hide: Boolean, sort: Int) {
        sharedPreferences.saveSetting(hide, sort)
    }

    suspend fun add(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //get date
            val (jalali, utc) = getTime()
            val date = addZero(jalali)
            //add task
            val id = taskRepository.add(Task(title = title, createdUtc = utc, date = date))
            //add create date
            dateRepository.add(
                DoneDate(
                    0,
                    id.toInt(),
                    TYPE_CREATE,
                    jalali[0],
                    jalali[1],
                    jalali[2],
                    jalali[3],
                    jalali[4],
                    jalali[5]
                )
            )
            //add update time = 0
            dateRepository.add(DoneDate(0, id.toInt(), TYPE_UPDATE, 0, 0, 0, 0, 0, 0))
        }
    }

    suspend fun edit(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val (jalali, utc) = getTime()
            taskRepository.edit(task.copy(updatedUtc = utc))
            dateRepository.edit(
                DoneDate(
                    0,
                    task.id,
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
    }

    suspend fun delete(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.delete(task)
            dateRepository.deleteById(task.id)
        }
    }

    fun deleteCompleted() = taskRepository.deleteCompleted()
}

enum class SortTask { BY_NAME, BY_DATE_ASC }
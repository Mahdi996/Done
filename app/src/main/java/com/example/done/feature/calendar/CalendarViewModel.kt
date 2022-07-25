package com.example.done.feature.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.done.data.calendar.CalendarDay
import com.example.done.data.calendar.CalendarRepository
import com.example.done.data.date.DateRepository
import com.example.done.data.task.Task
import com.example.done.data.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val calendarRepository: CalendarRepository,
    private val taskRepository: TaskRepository,
    private val dateRepository: DateRepository
) : ViewModel() {

    suspend fun days(year: Int, month: Int): List<CalendarDay> =
        calendarRepository.getDays(
            year,
            month,
            dateRepository.getMonthTasks(
                year,
                month
            )
        )


    fun getToday(): CalendarDay = calendarRepository.getToday()

    fun getOneDayTasks(year: Int, month: Int, day: Int): LiveData<List<Int>> =
        dateRepository.getOneDayTasks(year, month, day).asLiveData()


    fun tasks(list: List<Int>) = taskRepository.getTasksFromIdList(list).asLiveData()

    suspend fun editTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.edit(task)
        }
    }
}
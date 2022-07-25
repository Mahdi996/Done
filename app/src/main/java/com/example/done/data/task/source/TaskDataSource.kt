package com.example.done.data.task.source

import com.example.done.data.task.Task
import com.example.done.feature.task.list.SortTask
import kotlinx.coroutines.flow.Flow

interface TaskDataSource {

    fun get(search:String,hide:Boolean,sort: SortTask): Flow<List<Task>>

    fun getTasksFromIdList(list: List<Int>): Flow<List<Task>>

    suspend fun add(task: Task): Long

    suspend fun edit(task: Task)

    suspend fun delete(task: Task)

    fun deleteCompleted()
}
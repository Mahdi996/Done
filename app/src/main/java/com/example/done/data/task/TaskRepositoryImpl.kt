package com.example.done.data.task

import com.example.done.data.task.source.TaskLocalDataSource
import com.example.done.feature.task.list.SortTask
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val local: TaskLocalDataSource
) : TaskRepository {

    override fun get(search:String,hide:Boolean,sort: SortTask): Flow<List<Task>> = local.get(search,hide,sort)

    override fun getTasksFromIdList(list: List<Int>): Flow<List<Task>> = local.getTasksFromIdList(list)

    override suspend fun add(task: Task): Long = local.add(task)

    override suspend fun edit(task: Task) = local.edit(task)

    override suspend fun delete(task: Task) = local.delete(task)

    override fun deleteCompleted() = local.deleteCompleted()

}
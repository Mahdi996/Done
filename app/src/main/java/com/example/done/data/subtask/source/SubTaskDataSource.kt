package com.example.done.data.subtask.source

import com.example.done.data.subtask.SubTask
import kotlinx.coroutines.flow.Flow

interface SubTaskDataSource {

    fun get(a:Int):Flow<List<SubTask>>

    suspend fun add(subTask: SubTask)

    suspend fun edit(subTask: SubTask)

    suspend fun delete(subTask: SubTask)
}
package com.example.done.data.subtask

import kotlinx.coroutines.flow.Flow

interface SubTaskRepository {

    fun get(a:Int): Flow<List<SubTask>>

    suspend fun add(subTask: SubTask)

    suspend fun edit(subTask: SubTask)

    suspend fun delete(subTask: SubTask)

    suspend fun deleteSubs(idTask:Int)
}
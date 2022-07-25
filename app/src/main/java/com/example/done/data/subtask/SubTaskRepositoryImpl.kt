package com.example.done.data.subtask

import com.example.done.data.subtask.source.SubTaskLocalDataSource
import kotlinx.coroutines.flow.Flow

class SubTaskRepositoryImpl(
    private val local: SubTaskLocalDataSource
) : SubTaskRepository {
    override fun get(a: Int): Flow<List<SubTask>> = local.get(a)

    override suspend fun add(subTask: SubTask) = local.add(subTask)

    override suspend fun edit(subTask: SubTask) = local.edit(subTask)

    override suspend fun delete(subTask: SubTask) = local.delete(subTask)

    override suspend fun deleteSubs(idTask: Int) = local.deleteSubs(idTask)

}
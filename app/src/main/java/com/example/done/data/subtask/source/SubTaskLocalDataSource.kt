package com.example.done.data.subtask.source

import androidx.room.*
import com.example.done.data.subtask.SubTask
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskLocalDataSource {

    @Query("SELECT * FROM subTask where tasksId = :a")
    fun get(a: Int): Flow<List<SubTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(subTask: SubTask)

    @Update
    suspend fun edit(subTask: SubTask)

    @Delete
    suspend fun delete(subTask: SubTask)

    @Query("DELETE FROM subTask WHERE tasksId=:idTask")
    suspend fun deleteSubs(idTask: Int)
}
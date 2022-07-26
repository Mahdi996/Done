package com.example.done.data.date

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DateRepository {

    @Query("select * from date where taskId=:taskId")
    fun get(taskId: Int): Flow<List<DoneDate>>

    @Query("select taskId from date where year=:year and month=:month and day=:day")
    fun getOneDayTasks(year: Int, month: Int, day: Int): Flow<List<Int>>

    @Query("select day from date where year=:year and month=:month")
    suspend fun getMonthTasks(year: Int, month: Int): List<Int>

    @Query("select * from date where taskId=:task and type=2")
    fun getDeadline(task: Int): DoneDate

    @Insert
    suspend fun add(date: DoneDate)

    @Update
    suspend fun edit(date: DoneDate)

    @Delete
    suspend fun delete(date: DoneDate)

    @Query("delete from date where taskId=:taskId")
    suspend fun deleteById(taskId: Int)
}
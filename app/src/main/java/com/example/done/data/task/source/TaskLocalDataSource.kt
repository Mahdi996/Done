package com.example.done.data.task.source

import androidx.room.*
import com.example.done.data.task.Task
import com.example.done.feature.task.list.SortTask
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskLocalDataSource : TaskDataSource {

    override fun get(search: String, hide: Boolean, sort: SortTask): Flow<List<Task>> =
        if (hide) hides(search, sort) else normal(search, sort)

    fun hides(search: String, sort: SortTask): Flow<List<Task>> =
        when (sort) {
            SortTask.BY_NAME -> getByNameHide(search)
            SortTask.BY_DATE_ASC -> getByDateAscHide(search)
        }

    fun normal(search: String, sort: SortTask): Flow<List<Task>> =
        when (sort) {
            SortTask.BY_NAME -> getByName(search)
            SortTask.BY_DATE_ASC -> getByDateAsc(search)
        }

    @Query("SELECT * FROM tasks where title LIKE '%' || :search || '%' and isChecked = 0 order by title ASC")
    fun getByNameHide(search: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks where title LIKE '%' || :search || '%' and isChecked = 0 order by date ASC")
    fun getByDateAscHide(search: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks where title LIKE '%' || :search || '%' order by isChecked, title")
    fun getByName(search: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks where title LIKE '%' || :search || '%' order by isChecked, date")
    fun getByDateAsc(search: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks where id IN (:list)")
    override fun getTasksFromIdList(list: List<Int>): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun add(task: Task): Long

    @Update
    override suspend fun edit(task: Task)

    @Delete
    override suspend fun delete(task: Task)

    @Query("DELETE FROM tasks WHERE isChecked == 1")
    override fun deleteCompleted()
}
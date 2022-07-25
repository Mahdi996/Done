package com.example.done.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.done.data.date.DateRepository
import com.example.done.data.date.DoneDate
import com.example.done.data.subtask.SubTask
import com.example.done.data.subtask.source.SubTaskLocalDataSource
import com.example.done.data.task.Task
import com.example.done.data.task.source.TaskLocalDataSource

@Database(
    entities = [Task::class, SubTask::class,DoneDate::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tasksDao(): TaskLocalDataSource
    abstract fun subTasksDao(): SubTaskLocalDataSource
    abstract fun dateDao(): DateRepository
}


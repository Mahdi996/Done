package com.example.done.data.task

import android.app.PendingIntent
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var isChecked: Boolean = false,
    var title: String,
    var importance: Boolean = false,
    var note: String = "",
    var createdUtc: String = "",
    var updatedUtc: String = "",
    var deadlineUtc: String = "",
    var isSavedInServer: Boolean = false,
    var date: String
) : Parcelable

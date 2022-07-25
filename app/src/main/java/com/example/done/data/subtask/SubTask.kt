package com.example.done.data.subtask

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "subTask")
data class SubTask(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var tasksId: Int,
    var title: String,
    var isChecked: Boolean = false
) : Parcelable

package com.example.done.data.date

import androidx.room.Entity
import androidx.room.PrimaryKey

const val TYPE_CREATE = 0
const val TYPE_UPDATE = 1
const val TYPE_DEADLINE = 2


@Entity(tableName = "date")
data class DoneDate(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int,
    val type: Int,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int,
    var alarm: Int = 0
)

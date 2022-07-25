package com.example.done.data.task

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tasks")
data class Task(
    //TODO وقتی به سرور وصل کردی اتوش فالس باشه
    //TODO add utc create and update and deadline for easy send to server
    // and remove this items from DoneDate()

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

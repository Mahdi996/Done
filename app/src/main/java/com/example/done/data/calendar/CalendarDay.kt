package com.example.done.data.calendar

class CalendarDay(
    val mYear: Int,
    val mMonth: Int,
    val mDay: Int,
    val isToday: Boolean = false,
    val isCurrentMonth: Boolean = false,
    val formattedDate: String = "",
    val formattedDateSecondary: String = "",
    val haveTask: Boolean = false
)
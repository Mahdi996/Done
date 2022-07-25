package com.example.done.data.calendar

interface CalendarRepository {

    fun getToday(): CalendarDay

   suspend fun getDays(year: Int, month: Int,taskDays:List<Int>): List<CalendarDay>
}
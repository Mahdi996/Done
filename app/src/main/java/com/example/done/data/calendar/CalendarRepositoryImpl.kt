package com.example.done.data.calendar

import android.util.Log
import com.example.done.common.Constants
import ir.smartlab.persindatepicker.util.PersianCalendar
import java.util.*
import kotlin.collections.ArrayList


class CalendarRepositoryImpl(
    private val calendar: PersianCalendar
) : CalendarRepository {

    private val TAG = "A"

    override fun getToday(): CalendarDay {
        val date = (calendar.clone() as PersianCalendar)
        val georgianDate = GregorianCalendar().run {
            time = date.time
            "${Constants.weekdays_en[get(Calendar.DAY_OF_WEEK) - 1]}, ${
                Constants.months_en[get(
                    Calendar.MONTH
                )]
            } ${get(Calendar.DAY_OF_MONTH)} ${get(Calendar.YEAR)}"
        }

        return CalendarDay(
            date.persianYear,
            date.persianMonth,
            date.persianDay,
            true,
            true,
            date.persianLongDate,
            georgianDate
        )
    }

    override suspend fun getDays(year: Int, month: Int, taskDays: List<Int>): List<CalendarDay> {
        val todayCalendar = (calendar.clone() as PersianCalendar)
        val mToday = todayCalendar.persianDay
        val containToday = todayCalendar.persianYear == year && todayCalendar.persianMonth == month

        val currentYearCalendar =
            (calendar.clone() as PersianCalendar).setPersianDate(year, month, 1)
        val isLeapYear = currentYearCalendar.isPersianLeapYear
        val dayOfWeek = currentYearCalendar.persianWeekDay % 7
        var currentMonthDays = Constants.daysOfMonth_fa[month - 1]
        var previousMonthDays = Constants.daysOfMonth_fa[(month - 2 + 12) % 12]

        //Add Extra Day to current month in Case of Leap Year
        if (isLeapYear && month == 12)
            currentMonthDays++


        //Add Extra Day to Previous Month in Case of Leap Year
        val lastYearCalendar =
            (calendar.clone() as PersianCalendar).setPersianDate(year - 1, month, 1)
        if (month == 1 && lastYearCalendar.isPersianLeapYear)
            previousMonthDays++


        val days = ArrayList<CalendarDay>()
        //Add Trailing Days from Last Month if Needed
        if (dayOfWeek > 0)
            for (i in dayOfWeek - 1 downTo 0)
                days.add(CalendarDay(year, month - 1, previousMonthDays - i))

        //Add Month Days
        for (i in 1..currentMonthDays) {

            var haveTask = false
            taskDays.forEach {
                if (i == it) {
                    haveTask = true
                    return@forEach
                }
            }

            val currentDayCalendar =
                (calendar.clone() as PersianCalendar).setPersianDate(year, month, i)
            val georgianDate = GregorianCalendar().run {
                time = currentDayCalendar.time
                "${Constants.weekdays_en[get(Calendar.DAY_OF_WEEK) - 1]}, ${
                    Constants.months_en[get(
                        Calendar.MONTH
                    )]
                } ${get(Calendar.DAY_OF_MONTH)} ${get(Calendar.YEAR)}"
            }

            val isToday = i == mToday && containToday

            days.add(
                CalendarDay(
                    year, month, i, isToday, true,
                    currentDayCalendar.persianLongDate, georgianDate, haveTask
                )
            )
        }

        //Add Leading Month Days
        if (days.size % 7 > 0)
            for (i in 1..(7 - days.size % 7))
                days.add(CalendarDay(year, month + 1, i))
        Log.i(TAG, "getDays: ")

        return days
    }
}




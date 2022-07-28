package com.example.done.common

import com.example.done.R

/**
 * Created by Keivan Esbati on 11/18/2016.
 *
 * All of the day icons owned by persian-calendar
 * @see <a href="https://github.com/persian-calendar">persian-calendar"</a>
 *
 */

object Constants {
    val daysOfMonth_fa = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
    val weekdays_en = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val months_en = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val months=ArrayList<String>().apply {
        add("")
        add("فروردین")
        add("اردیبهشت")
        add("خرداد")
        add("تیر")
        add("مرداد")
        add("شهریور")
        add("مهر")
        add("آبان")
        add("آذر")
        add("دی")
        add("بهمن")
        add("اسفند")
    }
}

package com.example.done.common

import ir.smartlab.persindatepicker.util.PersianCalendar
import java.text.SimpleDateFormat
import java.util.*

const val EXTRA_KEY_BOOLEAN = "bool"
const val EXTRA_KEY_DATA = "task"

fun main() {


    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    //for send this pattern to server
    val utcPattern =
        "${utc.get(Calendar.YEAR)}-${utc.get(Calendar.MONTH)}-${utc.get(Calendar.DAY_OF_MONTH)}T${
            utc.get(Calendar.HOUR_OF_DAY)
        }:${utc.get(Calendar.MINUTE)}:${utc.get(Calendar.SECOND)}.${utc.get(Calendar.MILLISECOND)}Z"

    //for change utc to local
    val pattern =
        "${utc.get(Calendar.YEAR)},${utc.get(Calendar.MONTH) + 1},${utc.get(Calendar.DAY_OF_MONTH)}-${
            utc.get(Calendar.HOUR_OF_DAY)
        }:${utc.get(Calendar.MINUTE)}:${utc.get(Calendar.SECOND)}"

    val df = SimpleDateFormat("yyyy,MM,dd-hh:mm:ss", Locale.ENGLISH)
    df.timeZone = TimeZone.getTimeZone("UTC")
    val date = df.parse(pattern)
    df.timeZone = TimeZone.getDefault()


    //change date to jalali
    val persianCalendar = PersianCalendar()

    persianCalendar.timeInMillis = date.time

    println(persianCalendar.persianYear)
    println(persianCalendar.persianMonth)
    println(persianCalendar.persianDay)
    println()
    println(date.hours)
    println(date.minutes)
    println(date.seconds)
    println()
    println(utc.get(Calendar.YEAR))
    println(utc.get(Calendar.MONTH))
    println(utc.get(Calendar.DAY_OF_MONTH))
    println()
    println(utc.get(Calendar.HOUR_OF_DAY))
    println(utc.get(Calendar.MINUTE))
    println(utc.get(Calendar.SECOND))
}
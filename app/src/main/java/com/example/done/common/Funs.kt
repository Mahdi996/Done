package com.example.done.common

import com.example.done.data.date.DoneDate
import ir.smartlab.persindatepicker.util.PersianCalendar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun getTime(): Pair<ArrayList<Int>, String> {

    val list = ArrayList<Int>()

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

    list.add(persianCalendar.persianYear)
    list.add(persianCalendar.persianMonth)
    list.add(persianCalendar.persianDay)
    list.add(date.hours)
    list.add(date.minutes)
    list.add(date.day)

    return list to utcPattern
}

fun formatDate(date: DoneDate): String {
    var m = ""
    var d = ""

    m = if (date.month.toString().length == 1)
        "0${date.month}"
    else
        "${date.month}"

    d = if (date.day.toString().length == 1)
        "0${date.day}"
    else
        "${date.day}"

    return "${date.year}/$m/$d"
}

fun formatTime(date: DoneDate): String {
    var h = ""
    var m = ""
    var s = ""
    h = if (date.hour.toString().length == 1)
        "0${date.hour}"
    else
        "${date.hour}"

    m = if (date.minute.toString().length == 1)
        "0${date.minute}"
    else
        "${date.minute}"


    s = if (date.second.toString().length == 1)
        "0${date.second}"
    else
        "${date.second}"

    return "$h:$m:$s"
}

fun getFullTime(date: DoneDate): String = " تاریخ ${formatDate(date)} ساعت ${formatTime(date)}"

fun formatDateGregorian(date: DoneDate): String {
    val gregorian = jalaliToGregorian(date.year, date.month, date.day)
    return "${gregorian[0]}/${gregorian[1]}/${gregorian[2]}"
}


/**  Gregorian & Jalali (Hijri_Shamsi,Solar) Date Converter Functions
Author: JDF.SCR.IR =>> Download Full Version :  http://jdf.scr.ir/jdf
License: GNU/LGPL _ Open Source & Free :: Version: 2.80 : [2020=1399]
--------------------------------------------------------------------- */

fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): IntArray {
    var jy1: Int = jy + 1595
    var days: Int =
        -355668 + (365 * jy1) + ((jy1 / 33).toInt() * 8) + (((jy1 % 33) + 3) / 4).toInt() + jd + (if (jm < 7) ((jm - 1) * 31) else (((jm - 7) * 30) + 186))
    var gy: Int = 400 * (days / 146097).toInt()
    days %= 146097
    if (days > 36524) {
        gy += 100 * (--days / 36524).toInt()
        days %= 36524
        if (days >= 365) days++
    }
    gy += 4 * (days / 1461).toInt()
    days %= 1461
    if (days > 365) {
        gy += ((days - 1) / 365).toInt()
        days = (days - 1) % 365
    }
    var gd: Int = days + 1
    var sal_a: IntArray = intArrayOf(
        0,
        31,
        if ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)) 29 else 28,
        31,
        30,
        31,
        30,
        31,
        31,
        30,
        31,
        30,
        31
    )
    var gm: Int = 0
    while (gm < 13 && gd > sal_a[gm]) gd -= sal_a[gm++]
    return intArrayOf(gy, gm, gd)
}

fun addZero(date: ArrayList<Int>): String {
    var m = ""
    var d = ""
    m = if (date[1].toString().length == 1)
        "0${date[1]}"
    else
        date[1].toString()

    d = if (date[2].toString().length == 1)
        "0${date[2]}"
    else
        date[2].toString()

    return "${date[0]}/$m/$d"
}
